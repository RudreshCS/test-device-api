package com.example.deviceapi.service;

import com.example.deviceapi.mapper.DeviceTelementryGroupMapper;
import com.example.deviceapi.model.DeviceTelementry;
import com.example.deviceapi.model.UnitData;
import com.example.deviceapi.repository.DeviceRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceDataService {

	private final DeviceRepository repository;

	public DeviceDataService(DeviceRepository repository) {
		this.repository = repository;
	}

	public DeviceTelementry save(DeviceTelementry data) {
		return repository.save(data);
	}

	public Optional<DeviceTelementry> findById(String id) {
		return repository.findById(id);
	}

	public List<DeviceTelementry> findAll() {
		return repository.findAll();
	}

	public void deleteById(String id) {
		repository.deleteById(id);
	}

	public Optional<UnitData> groupByUnitId(String unitId) {

		List<DeviceTelementry> entities = repository.findAllByUnitId(unitId);

		if (entities.isEmpty()) {
			throw new RuntimeException("No data found for unitId: " + unitId);
		}

		return Optional.of(DeviceTelementryGroupMapper.mapToSingleResponse(entities));
	}
}
