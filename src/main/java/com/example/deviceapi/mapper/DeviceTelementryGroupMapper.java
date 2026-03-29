package com.example.deviceapi.mapper;

import java.util.ArrayList;
import java.util.List;

import com.example.deviceapi.model.BoxInformation;
import com.example.deviceapi.model.DeviceAndBoxData;
import com.example.deviceapi.model.DeviceTelementry;
import com.example.deviceapi.model.UnitData;

public class DeviceTelementryGroupMapper {

	public static UnitData mapToSingleResponse(List<DeviceTelementry> entities) {

		UnitData response = new UnitData();

		// Since unitId is SAME for all entries, pick first
		DeviceTelementry first = entities.get(0);

		response.setUnitId(first.getUnitId());
		response.setCustomerId(first.getCustomerId());
		response.setUnitStatus(first.getUnitStatus());
		response.setNumber(first.getNumber());

		// deviceId from latest or first record
		if (first.getDeviceData() != null) {
			response.setDeviceId(first.getDeviceData().getDeviceId());
		}

		List<DeviceAndBoxData> combinedList = new ArrayList<>();

		for (DeviceTelementry entity : entities) {

			if (entity.getDeviceData() == null || entity.getBoxInformation() == null) {
				continue;
			}

			for (BoxInformation box : entity.getBoxInformation()) {

				DeviceAndBoxData dto = new DeviceAndBoxData();

				// Device data
				dto.setExternalTemperature(entity.getDeviceData().getExternalTemperature());
				dto.setRainSensor(entity.getDeviceData().getRainSensor());
				dto.setBatteryStatus(entity.getDeviceData().getBatteryStatus());

				// Box data
				dto.setBoxId(box.getBoxId());
				dto.setStatus(box.getStatus());
				dto.setTemperature(box.getTemperature());
				dto.setHumidity(box.getHumidity());
				dto.setWeight(box.getWeight());
				dto.setAccelerometer(box.getAccelerometer());
				dto.setTimestamp(box.getTimestamp());

				combinedList.add(dto);
			}
		}

		response.setDeviceAndBoxData(combinedList);
		return response;
	}
}
