package com.example.deviceapi.iot;

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

	public DeviceData deviceData;

	public AwsIotSubscriber(DeviceService service) {
		this.service = service;
	}

	@PostConstruct
	public void init() {
		try {
			System.out.println("Inside init ");
			AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(
					"src/main/resources/certs/device-certificate.pem.crt", "src/main/resources/certs/private.pem.key");

			builder.withEndpoint("a3e57rgpwqspn6-ats.iot.ap-south-1.amazonaws.com");
			builder.withClientId("SpringBootSubscriber");
			builder.withCleanSession(true);

			MqttClientConnection connection = builder.build();
			connection.connect().get();

			connection.subscribe("device/data", QualityOfService.AT_LEAST_ONCE, (message) -> {
				String payload = new String(message.getPayload());
				System.out.println("[IoT Message Test] " + payload);
				System.out.println("Reading from mapper ");

				try {
					deviceData = mapper.readValue(payload, DeviceData.class);
					System.out.println("After Reading from mapper ");
					service.save(deviceData);
					System.out.println("After inserting");
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}

			}).get();

			System.out.println("Subscribed to device/data topic.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Scheduled(fixedDelay = Long.MAX_VALUE)
	public void keepRunning() {
		System.out.println("Inside keepRunning");
	}

}
