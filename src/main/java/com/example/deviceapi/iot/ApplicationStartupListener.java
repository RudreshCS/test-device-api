package com.example.deviceapi.iot;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

	@Autowired
	private AwsIotSubscriber iotSubscriber;

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		logger.info("🚀 Application started successfully!");

		// Wait a bit for IoT connection to establish
		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(5000); // Wait 5 seconds

				String status = iotSubscriber.getConnectionStatus();
				logger.info("📡 AWS IoT Connection Status: {}", status);

				if ("FULLY_CONNECTED".equals(status)) {
					logger.info("✅ AWS IoT is fully operational!");
				} else {
					logger.warn("⚠️ AWS IoT connection issue detected: {}", status);
				}

			} catch (Exception e) {
				logger.error("Error checking IoT status on startup", e);
			}
		});
	}
}
