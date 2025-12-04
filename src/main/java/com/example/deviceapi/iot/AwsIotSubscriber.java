package com.example.deviceapi.iot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.deviceapi.model.DeviceData;
import com.example.deviceapi.service.DeviceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

@Component
@EnableScheduling
public class AwsIotSubscriber {

	private final ObjectMapper mapper = new ObjectMapper();

	private final DeviceService service;

	public AwsIotSubscriber(DeviceService service) {
		this.service = service;
	}

	@PostConstruct
	public void init() {
		try {
			System.out.println("Inside init ");
			// Load certificates from environment variables
			String deviceCert = System.getenv("DEVICE_CERTIFICATE");
			String privateKey = System.getenv("PRIVATE_KEY");

			// Create temporary files or use in-memory approach
			AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(
					createTempFile(deviceCert, "device-cert.pem"), createTempFile(privateKey, "private-key.pem"));

			builder.withEndpoint("a3e57rgpwqspn6-ats.iot.ap-south-1.amazonaws.com");
			builder.withClientId("SpringBootSubscriber");
			builder.withCleanSession(true);

			MqttClientConnection connection = builder.build();
			connection.connect().get();

			connection.subscribe("test", QualityOfService.AT_LEAST_ONCE, (message) -> {
				System.out.println("Received message on topic: test");

				try {
					String payload = new String(message.getPayload());
					System.out.println("Payload : " + payload);
					DeviceData deviceData = mapper.readValue(payload, DeviceData.class);
					System.out.println("Parsed device data: " + deviceData);
					service.save(deviceData);
					System.out.println("Successfully saved device data");
				} catch (JsonProcessingException e) {
					System.err.println("JSON parsing error: " + e.getMessage());
				} catch (Exception e) {
					System.err.println("Unexpected error while processing data: " + e.getMessage());
					e.printStackTrace();
				}

			}).get();

			System.out.println("Subscribed to device/data topic.");

		} catch (Exception e) {
			System.err.println("Unexpected error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Scheduled(fixedDelay = Long.MAX_VALUE)
	public void keepRunning() {
		System.out.println("Inside keepRunning");
	}

	private String createTempFile(String content, String filename) throws IOException {
		Path tempFile = Files.createTempFile(filename, ".tmp");
		Files.write(tempFile, content.getBytes());
		tempFile.toFile().deleteOnExit();
		return tempFile.toString();
	}

}
