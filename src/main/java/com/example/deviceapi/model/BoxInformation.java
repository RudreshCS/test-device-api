package com.example.deviceapi.model;

public class BoxInformation {

	private String boxId;
	private String status;
	private double temperature;
	private String humidity;
	private double weight;
	private String accelerometer;
	private String timestamp;
	private String microphone;

	public BoxInformation() {
		
	}

	public BoxInformation(String boxId, String status, double temperature, String humidity, double weight,
			String accelerometer, String timestamp, String microphone) {
		super();
		this.boxId = boxId;
		this.status = status;
		this.temperature = temperature;
		this.humidity = humidity;
		this.weight = weight;
		this.accelerometer = accelerometer;
		this.timestamp = timestamp;
		this.microphone = microphone;
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

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
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

	public String getMicrophone() {
		return microphone;
	}

	public void setMicrophone(String microphone) {
		this.microphone = microphone;
	}

}
