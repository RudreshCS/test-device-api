package com.example.deviceapi.model;

public class DeviceAndBoxData {

    private String externalTemperature;
    private String rainSensor;
    private String batteryStatus;
    private String boxId;
    private String status;
    private Double temperature;
    private String humidity;
    private Double weight;
    private String accelerometer;
    private String timestamp;

    // Getters and Setters
    public String getExternalTemperature() {
        return externalTemperature;
    }

    public void setExternalTemperature(String externalTemperature) {
        this.externalTemperature = externalTemperature;
    }

    public String getRainSensor() {
        return rainSensor;
    }

    public void setRainSensor(String rainSensor) {
        this.rainSensor = rainSensor;
    }

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(String accelerometer) {
        this.accelerometer = accelerometer;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

