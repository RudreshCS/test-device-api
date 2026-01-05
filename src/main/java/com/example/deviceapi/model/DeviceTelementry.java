package com.example.deviceapi.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "device_data")
public class DeviceTelementry {

	private String unitId;
	private String customerId;
	private String timeStamp;
	private String location;
	private String imageId;
	private String unitStatus;
	private String number;
	private String imeiNumber;
	private boolean isServiceRequested;

	private DeviceData deviceData;
	private List<BoxInformation> boxInformation;
	
	public DeviceTelementry() {
		
	}

	public DeviceTelementry(String unitId, String customerId, String timeStamp, String location, String imageId,
			String unitStatus, String number, String imeiNumber, boolean isServiceRequested, DeviceData deviceData,
			List<BoxInformation> boxInformation) {
		super();
		this.unitId = unitId;
		this.customerId = customerId;
		this.timeStamp = timeStamp;
		this.location = location;
		this.imageId = imageId;
		this.unitStatus = unitStatus;
		this.number = number;
		this.imeiNumber = imeiNumber;
		this.isServiceRequested = isServiceRequested;
		this.deviceData = deviceData;
		this.boxInformation = boxInformation;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getUnitStatus() {
		return unitStatus;
	}

	public void setUnitStatus(String unitStatus) {
		this.unitStatus = unitStatus;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public boolean isServiceRequested() {
		return isServiceRequested;
	}

	public void setServiceRequested(boolean isServiceRequested) {
		this.isServiceRequested = isServiceRequested;
	}

	public DeviceData getDeviceData() {
		return deviceData;
	}

	public void setDeviceData(DeviceData deviceData) {
		this.deviceData = deviceData;
	}

	public List<BoxInformation> getBoxInformation() {
		return boxInformation;
	}

	public void setBoxInformation(List<BoxInformation> boxInformation) {
		this.boxInformation = boxInformation;
	}

}
