package com.example.deviceapi.model;

import java.util.List;

public class UnitData {

    private String unitId;
    private String customerId;
    private String unitStatus;
    private String number;
    private String deviceId;
    private List<DeviceAndBoxData> deviceAndBoxData;

    // Getters and Setters
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<DeviceAndBoxData> getDeviceAndBoxData() {
        return deviceAndBoxData;
    }

    public void setDeviceAndBoxData(List<DeviceAndBoxData> deviceAndBoxData) {
        this.deviceAndBoxData = deviceAndBoxData;
    }
}

