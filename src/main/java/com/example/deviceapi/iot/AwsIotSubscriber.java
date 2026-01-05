package com.example.deviceapi.iot;

import com.example.deviceapi.model.DeviceTelementry;
import com.example.deviceapi.service.DeviceDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AwsIotSubscriber {

	private static final Logger logger = LoggerFactory.getLogger(AwsIotSubscriber.class);

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DeviceDataService service;

	private MqttClientConnection connection;
	private final AtomicBoolean isConnected = new AtomicBoolean(false);
	private final AtomicBoolean isSubscribed = new AtomicBoolean(false);
	private final AtomicInteger messageCount = new AtomicInteger(0);
	private final AtomicBoolean processing = new AtomicBoolean(false);

	// Separate thread pool for message processing to avoid blocking MQTT thread
	private final ExecutorService messageProcessor = Executors.newFixedThreadPool(2);

	@PostConstruct
	public void init() {
		try {
			String deviceCert = System.getenv("DEVICE_CERTIFICATE");
			String privateKey = System.getenv("PRIVATE_KEY");

			logger.info("🚀 Starting AWS IoT connection initialization...");
			validateCertificates(deviceCert, privateKey);

			AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilder(deviceCert, privateKey);

			String endpoint = "a3e57rgpwqspn6-ats.iot.ap-south-1.amazonaws.com";
			String clientId = "SpringBootSubscriber-" + System.currentTimeMillis();

			builder.withEndpoint(endpoint);
			builder.withPort(443);
			builder.withClientId(clientId);
			builder.withCleanSession(true);

			// Add connection lifecycle callbacks
			builder.withConnectionEventCallbacks(new MqttClientConnectionEvents() {
				@Override
				public void onConnectionInterrupted(int errorCode) {
					isConnected.set(false);
					logger.warn("⚠️ AWS IoT connection interrupted. Error code: {}", errorCode);
				}

				@Override
				public void onConnectionResumed(boolean sessionPresent) {
					isConnected.set(true);
					logger.info("✅ AWS IoT connection resumed. Session present: {}", sessionPresent);
				}
			});

			connection = builder.build();

			logger.info("🔗 Attempting to connect to AWS IoT Core...");
			logger.info("📍 Endpoint: {}", endpoint);
			logger.info("🆔 Client ID: {}", clientId);

			// Connect with detailed logging
			CompletableFuture<Boolean> connectFuture = connection.connect();

			connectFuture.whenComplete((sessionPresent, throwable) -> {
				if (throwable != null) {
					isConnected.set(false);
					logger.error("❌ Failed to connect to AWS IoT Core", throwable);
				} else {
					isConnected.set(true);
					logger.info("✅ Successfully connected to AWS IoT Core!");
					logger.info("📋 Session present: {}", sessionPresent);

					// Subscribe after successful connection
					subscribeToTopic();
				}
			});

			// Wait for connection with timeout
			try {
				connectFuture.get(30, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				logger.error("⏰ Connection timeout after 30 seconds");
				throw new RuntimeException("AWS IoT connection timeout", e);
			}

		} catch (Exception e) {
			logger.error("💥 Failed to initialize AWS IoT connection", e);
			throw new RuntimeException("AWS IoT initialization failed", e);
		}
	}

	private void subscribeToTopic() {
		try {
			String topicName = "test";
			logger.info("📡 Attempting to subscribe to topic: {}", topicName);

			CompletableFuture<Integer> subscribeFuture = connection.subscribe(topicName, QualityOfService.AT_LEAST_ONCE,
					this::handleMessageSafely // Use safe handler
			);

			subscribeFuture.whenComplete((packetId, throwable) -> {
				if (throwable != null) {
					isSubscribed.set(false);
					logger.error("❌ Failed to subscribe to topic: {}", topicName, throwable);
				} else {
					isSubscribed.set(true);
					logger.info("✅ Successfully subscribed to topic: {} (Packet ID: {})", topicName, packetId);
					startHeartbeat();
				}
			});

			subscribeFuture.get(10, TimeUnit.SECONDS);

		} catch (Exception e) {
			logger.error("💥 Error during topic subscription", e);
		}
	}

	// CRITICAL: Safe message handler to prevent StackOverflowError
	private void handleMessageSafely(MqttMessage message) {
		// Prevent recursive calls and process in separate thread
		if (!processing.compareAndSet(false, true)) {
			logger.warn("⚠️ Message handler already processing, skipping message");
			return;
		}

		// Process message in separate thread pool to avoid blocking MQTT event loop
		messageProcessor.submit(() -> {
			try {
				handleMessage(message);
			} finally {
				processing.set(false);
			}
		});
	}

	private void handleMessage(MqttMessage message) {
		try {
			int count = messageCount.incrementAndGet();
			String payload = new String(message.getPayload());
			String topic = message.getTopic();

			logger.info("📨 Message #{} received from topic '{}': {}", count, topic, payload);

			// Avoid logging too much detail that might cause recursion
			if (count % 10 == 1) { // Log details only for every 10th message
				logger.info("📊 Message details - QoS: {}, Retain: {}, Duplicate: {}", message.getQos(),
						message.getRetain(), message.getDup());
			}

			// Only process JSON if it looks like device data
			if (payload.contains("temperature") || payload.contains("humidity")) {
				try {
					DeviceTelementry deviceData = mapper.readValue(payload, DeviceTelementry.class);
					logger.info("🔄 Parsed device data for message #{}", count);

					service.save(deviceData);
					logger.info("💾 Successfully saved message #{} to database", count);

				} catch (JsonProcessingException e) {
					logger.error("❌ JSON parsing error for message #{}: {}", count, e.getMessage());
				} catch (Exception e) {
					logger.error("❌ Error saving message #{}: {}", count, e.getMessage());
				}
			} else {
				logger.info("📝 Received non-device-data message #{}: {}", count,
						payload.length() > 100 ? payload.substring(0, 100) + "..." : payload);
			}

		} catch (Exception e) {
			logger.error("💥 Critical error processing message", e);
		}
	}

	private void startHeartbeat() {
		CompletableFuture.runAsync(() -> {
			while (isConnected.get()) {
				try {
					Thread.sleep(30000); // 30 seconds
					logger.info("💓 Heartbeat - Connected: {}, Subscribed: {}, Messages: {}", isConnected.get(),
							isSubscribed.get(), messageCount.get());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
	}

	private void validateCertificates(String deviceCert, String privateKey) {
		logger.info("🔍 Validating certificates...");

		if (deviceCert == null || deviceCert.trim().isEmpty()) {
			throw new IllegalStateException("DEVICE_CERTIFICATE environment variable is not set");
		}
		if (privateKey == null || privateKey.trim().isEmpty()) {
			throw new IllegalStateException("PRIVATE_KEY environment variable is not set");
		}

		if (!deviceCert.contains("-----BEGIN CERTIFICATE-----")) {
			throw new IllegalStateException("DEVICE_CERTIFICATE does not appear to be valid PEM");
		}
		if (!privateKey.contains("-----BEGIN") || !privateKey.contains("PRIVATE KEY-----")) {
			throw new IllegalStateException("PRIVATE_KEY does not appear to be valid PEM");
		}

		logger.info("✅ Certificates validation passed");
	}

	// Public methods to check connection status
	public boolean isConnected() {
		return isConnected.get();
	}

	public boolean isSubscribed() {
		return isSubscribed.get();
	}

	public int getMessageCount() {
		return messageCount.get();
	}

	public String getConnectionStatus() {
		boolean connected = isConnected.get();
		boolean subscribed = isSubscribed.get();

		if (connected && subscribed) {
			return "FULLY_CONNECTED";
		} else if (connected) {
			return "CONNECTED_NOT_SUBSCRIBED";
		} else {
			return "DISCONNECTED";
		}
	}

	@PreDestroy
	public void cleanup() {
		logger.info("🧹 Cleaning up AWS IoT connection...");

		// Shutdown message processor
		messageProcessor.shutdown();
		try {
			if (!messageProcessor.awaitTermination(10, TimeUnit.SECONDS)) {
				messageProcessor.shutdownNow();
			}
		} catch (InterruptedException e) {
			messageProcessor.shutdownNow();
		}

		if (connection != null) {
			try {
				connection.disconnect().get(10, TimeUnit.SECONDS);
				isConnected.set(false);
				isSubscribed.set(false);
				logger.info("✅ Successfully disconnected from AWS IoT Core");
			} catch (Exception e) {
				logger.error("❌ Error disconnecting from AWS IoT Core", e);
			}
		}
	}
}
