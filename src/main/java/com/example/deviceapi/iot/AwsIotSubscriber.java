package com.example.deviceapi.iot;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.deviceapi.model.DeviceData;
import com.example.deviceapi.service.DeviceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

@Component
public class AwsIotSubscriber {

	private static final Logger logger = LoggerFactory.getLogger(AwsIotSubscriber.class);

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DeviceDataService service;

	private MqttClientConnection connection;
	private volatile boolean isConnected = false;
	private volatile boolean isSubscribed = false;
	private final AtomicInteger messageCount = new AtomicInteger(0);

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
			builder.withClientId(clientId);
			builder.withCleanSession(true);

			// Add connection lifecycle callbacks
			builder.withConnectionEventCallbacks(new MqttClientConnectionEvents() {
				@Override
				public void onConnectionInterrupted(int errorCode) {
					isConnected = false;
					logger.warn("⚠️ AWS IoT connection interrupted. Error code: {}", errorCode);
				}

				@Override
				public void onConnectionResumed(boolean sessionPresent) {
					isConnected = true;
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
					isConnected = false;
					logger.error("❌ Failed to connect to AWS IoT Core", throwable);
				} else {
					isConnected = true;
					logger.info("✅ Successfully connected to AWS IoT Core!");
					logger.info("📋 Session present: {}", sessionPresent);

					// Subscribe after successful connection
					subscribeToMultipleTopics();
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

	private void subscribeToMultipleTopics() {
		String[] topics = { "test", "device/data", "#" }; // Try multiple topics including wildcard

		for (String topicName : topics) {
			try {
				logger.info("📡 Attempting to subscribe to topic: {}", topicName);

				CompletableFuture<Integer> subscribeFuture = connection.subscribe(topicName,
						QualityOfService.AT_LEAST_ONCE, (message) -> handleMessage(message, topicName));

				subscribeFuture.whenComplete((packetId, throwable) -> {
					if (throwable != null) {
						logger.error("❌ Failed to subscribe to topic: {}", topicName, throwable);
					} else {
						isSubscribed = true;
						logger.info("✅ Successfully subscribed to topic: {} (Packet ID: {})", topicName, packetId);

						// Start heartbeat only once
						if (topicName.equals("test")) {
							startHeartbeat();
						}
					}
				});

				subscribeFuture.get(10, TimeUnit.SECONDS);

			} catch (Exception e) {
				logger.error("💥 Error subscribing to topic {}: {}", topicName, e.getMessage());
			}
		}
	}

	private void startHeartbeat() {
		// Log every 30 seconds to show the subscriber is alive and waiting
		CompletableFuture.runAsync(() -> {
			while (isConnected && isSubscribed) {
				try {
					Thread.sleep(30000); // 30 seconds
					logger.info("💓 Heartbeat - Still connected and subscribed. Messages received: {}",
							messageCount.get());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
	}

	private void handleMessage(MqttMessage message, String subscribedTopic) {
		try {
			int count = messageCount.incrementAndGet();
			String payload = new String(message.getPayload());
			String actualTopic = message.getTopic();

			logger.info("📨 Message #{} received!", count);
			logger.info("📍 Subscribed to: {}", subscribedTopic);
			logger.info("📍 Actual topic: {}", actualTopic);
			logger.info("📄 Payload: {}", payload);
			logger.info("📊 QoS: {}, Retain: {}, Duplicate: {}", message.getQos(), message.getRetain(),
					message.getDup());

			// Only process JSON if it looks like device data
			if (payload.contains("temperature") || payload.contains("humidity")) {
				try {
					DeviceData deviceData = mapper.readValue(payload, DeviceData.class);
					logger.info("🔄 Parsed device data: {}", deviceData);
					service.save(deviceData);
					logger.info("💾 Successfully saved device data to database");
				} catch (Exception e) {
					logger.error("❌ Error processing device data: {}", e.getMessage());
				}
			} else {
				logger.info("📝 Received non-device-data message: {}", payload);
			}

		} catch (Exception e) {
			logger.error("💥 Error processing message", e);
		}
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
		return isConnected;
	}

	public boolean isSubscribed() {
		return isSubscribed;
	}

	public int getMessageCount() {
		return messageCount.get();
	}

	public String getConnectionStatus() {
		if (isConnected && isSubscribed) {
			return "FULLY_CONNECTED";
		} else if (isConnected) {
			return "CONNECTED_NOT_SUBSCRIBED";
		} else {
			return "DISCONNECTED";
		}
	}

	@PreDestroy
	public void cleanup() {
		logger.info("🧹 Cleaning up AWS IoT connection...");

		if (connection != null) {
			try {
				connection.disconnect().get(10, TimeUnit.SECONDS);
				isConnected = false;
				isSubscribed = false;
				logger.info("✅ Successfully disconnected from AWS IoT Core");
			} catch (Exception e) {
				logger.error("❌ Error disconnecting from AWS IoT Core", e);
			}
		}
	}
}
