package com.example.deviceapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "device_data")
public class DeviceData {

    @Id
    private String id;

    @NotBlank(message = "device_id cannot be blank")
    private String device_id;

    @NotNull(message = "temperature is required")
    private Double temperature;

    @NotNull(message = "weight is required")
    @Positive(message = "weight must be positive")
    private Double weight;

    @NotNull(message = "timestamp is required")
    private String timestamp;

    public DeviceData() {}

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDevice_id() { return device_id; }
    public void setDevice_id(String device_id) { this.device_id = device_id; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
