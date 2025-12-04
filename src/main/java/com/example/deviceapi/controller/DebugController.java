package com.example.deviceapi.controller;

import com.example.deviceapi.iot.AwsIotSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

	private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

	@Autowired
	private AwsIotSubscriber iotSubscriber;

	@GetMapping("/connection-details")
	public ResponseEntity<Map<String, Object>> getConnectionDetails() {
		Map<String, Object> details = new HashMap<>();

		// Environment variables (without showing actual values)
		details.put("deviceCertPresent", System.getenv("DEVICE_CERTIFICATE") != null);
		details.put("privateKeyPresent", System.getenv("PRIVATE_KEY") != null);
		details.put("deviceCertLength",
				System.getenv("DEVICE_CERTIFICATE") != null ? System.getenv("DEVICE_CERTIFICATE").length() : 0);
		details.put("privateKeyLength",
				System.getenv("PRIVATE_KEY") != null ? System.getenv("PRIVATE_KEY").length() : 0);

		// Connection status
		details.put("connected", iotSubscriber.isConnected());
		details.put("subscribed", iotSubscriber.isSubscribed());
		details.put("messageCount", iotSubscriber.getMessageCount());
		details.put("status", iotSubscriber.getConnectionStatus());

		// System info
		details.put("javaVersion", System.getProperty("java.version"));
		details.put("timestamp", Instant.now().toString());

		return ResponseEntity.ok(details);
	}

	@PostMapping("/test-publish")
	public ResponseEntity<String> testPublish(@RequestBody Map<String, Object> testMessage) {
		// This endpoint can be used to test if the issue is with publishing
		// You can call this from another service or manually
		logger.info("Test publish requested with message: {}", testMessage);
		return ResponseEntity.ok("Test message logged. Check if subscriber receives it.");
	}

	@GetMapping("/test/connectivity")
	public ResponseEntity<String> testConnectivity() {
		try {
			// Test if we can reach the IoT endpoint
			String endpoint = "a3e57rgpwqspn6-ats.iot.ap-south-1.amazonaws.com";
			InetAddress address = InetAddress.getByName(endpoint);
			boolean reachable = address.isReachable(5000);

			return ResponseEntity.ok("Endpoint reachable: " + reachable + ", IP: " + address.getHostAddress());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Connectivity test failed: " + e.getMessage());
		}
	}

	@GetMapping("/test/network-debug")
	public ResponseEntity<Map<String, Object>> networkDebug() {
		Map<String, Object> debug = new HashMap<>();

		String[] endpoints = { "a3e57rgpwqspn6-ats.iot.ap-south-1.amazonaws.com", "8.8.8.8", // Google DNS
				"1.1.1.1" // Cloudflare DNS
		};

		for (String endpoint : endpoints) {
			try {
				InetAddress address = InetAddress.getByName(endpoint);
				boolean reachable = address.isReachable(5000);
				debug.put(endpoint + "_reachable", reachable);
				debug.put(endpoint + "_ip", address.getHostAddress());
			} catch (Exception e) {
				debug.put(endpoint + "_error", e.getMessage());
			}
		}

		return ResponseEntity.ok(debug);
	}

}
