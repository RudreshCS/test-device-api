package com.example.deviceapi.model;

public class GroupedDeviceResponse {

	private String unitId;
	private String customerId;
	private String number;
	private String unitStatus;

	public GroupedDeviceResponse() {

	}

	public GroupedDeviceResponse(String unitId, String customerId, String number, String unitStatus) {
		super();
		this.unitId = unitId;
		this.customerId = customerId;
		this.number = number;
		this.unitStatus = unitStatus;
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getUnitStatus() {
		return unitStatus;
	}

	public void setUnitStatus(String unitStatus) {
		this.unitStatus = unitStatus;
	}

}
