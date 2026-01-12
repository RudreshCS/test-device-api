package com.example.deviceapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.deviceapi.model.DeviceTelementry;
import com.example.deviceapi.model.GroupedDeviceResponse;
import com.example.deviceapi.model.UnitData;
import com.example.deviceapi.service.DeviceAggregationService;
import com.example.deviceapi.service.DeviceDataService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

	private final DeviceDataService service;
	
	private final DeviceAggregationService deviceAggregationService;

	public DeviceController(DeviceDataService service, DeviceAggregationService deviceAggregationService) {
		this.service = service;
		this.deviceAggregationService = deviceAggregationService;
	}

	@PostMapping
	public ResponseEntity<DeviceTelementry> create(@Valid @RequestBody DeviceTelementry data) {
		DeviceTelementry saved = service.save(data);
		return ResponseEntity.created(URI.create("/api/devices/" + saved.getUnitId())).body(saved);
	}

	@GetMapping
	public ResponseEntity<List<DeviceTelementry>> list() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DeviceTelementry> getById(@PathVariable("id") String id) {
		return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<DeviceTelementry> update(@PathVariable("id") String id,
			@Valid @RequestBody DeviceTelementry data) {
		return service.findById(id).map(existing -> {
			// update fields
			existing.setUnitId(id);
			DeviceTelementry updated = service.save(existing);
			return ResponseEntity.ok(updated);
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") String id) {
		return service.findById(id).map(existing -> {
			service.deleteById(id);
			return ResponseEntity.noContent().<Void>build();
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping("/grouped")
    public List<GroupedDeviceResponse> getGroupedDevices() {
        return deviceAggregationService.getGroupedUnits();
    }

	@GetMapping("/unitId/{unitId}")
	public ResponseEntity<UnitData> groupByUnitIdId(@PathVariable("unitId") String unitId) {

		return service.groupByUnitId(unitId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

	}
}
