package com.example.deviceapi.iot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

	private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

	@Autowired
	private AwsIotSubscriber iotSubscriber;

	@GetMapping
	public ResponseEntity<Map<String, Object>> health() {
		Map<String, Object> health = new HashMap<>();

		try {
			boolean connected = iotSubscriber.isConnected();
			boolean subscribed = iotSubscriber.isSubscribed();
			String status = iotSubscriber.getConnectionStatus();

			health.put("status", connected && subscribed ? "UP" : "DOWN");
			health.put("aws_iot_connected", connected);
			health.put("aws_iot_subscribed", subscribed);
			health.put("connection_status", status);
			health.put("message_count", iotSubscriber.getMessageCount());
			health.put("timestamp", Instant.now().toString());

			logger.info("Health check - AWS IoT Status: {}", status);

			return ResponseEntity.ok(health);

		} catch (Exception e) {
			logger.error("Health check failed", e);
			health.put("status", "DOWN");
			health.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
		}
	}

	@GetMapping("/aws-iot")
	public ResponseEntity<Map<String, Object>> awsIotStatus() {
		Map<String, Object> status = new HashMap<>();

		status.put("connected", iotSubscriber.isConnected());
		status.put("subscribed", iotSubscriber.isSubscribed());
		status.put("status", iotSubscriber.getConnectionStatus());
		status.put("message_count", iotSubscriber.getMessageCount());
		status.put("timestamp", Instant.now().toString());

		return ResponseEntity.ok(status);
	}
}
