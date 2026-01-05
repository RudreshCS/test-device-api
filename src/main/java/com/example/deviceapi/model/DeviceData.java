package com.example.deviceapi.model;

public class DeviceData {

	private String externalTemperature;
	private String rainSensor;
	private String networkType;
	private String gpsLocation;
	private String deviceId;
	private String sdCardPresent;
	private String sdCardMemory;
	private String batteryStatus;
	
	public DeviceData() {
		
	}

	public DeviceData(String externalTemperature, String rainSensor, String networkType, String gpsLocation,
			String deviceId, String sdCardPresent, String sdCardMemory, String batteryStatus) {
		super();
		this.externalTemperature = externalTemperature;
		this.rainSensor = rainSensor;
		this.networkType = networkType;
		this.gpsLocation = gpsLocation;
		this.deviceId = deviceId;
		this.sdCardPresent = sdCardPresent;
		this.sdCardMemory = sdCardMemory;
		this.batteryStatus = batteryStatus;
	}

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

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getGpsLocation() {
		return gpsLocation;
	}

	public void setGpsLocation(String gpsLocation) {
		this.gpsLocation = gpsLocation;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSdCardPresent() {
		return sdCardPresent;
	}

	public void setSdCardPresent(String sdCardPresent) {
		this.sdCardPresent = sdCardPresent;
	}

	public String getSdCardMemory() {
		return sdCardMemory;
	}

	public void setSdCardMemory(String sdCardMemory) {
		this.sdCardMemory = sdCardMemory;
	}

	public String getBatteryStatus() {
		return batteryStatus;
	}

	public void setBatteryStatus(String batteryStatus) {
		this.batteryStatus = batteryStatus;
	}

}
