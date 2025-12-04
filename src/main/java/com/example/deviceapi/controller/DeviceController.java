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

import com.example.deviceapi.model.DeviceData;
import com.example.deviceapi.service.DeviceDataService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceDataService service;

    public DeviceController(DeviceDataService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DeviceData> create(@Valid @RequestBody DeviceData data) {
        DeviceData saved = service.save(data);
        return ResponseEntity.created(URI.create("/api/devices/" + saved.getId())).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<DeviceData>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceData> getById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceData> update(@PathVariable("id") String id, @Valid @RequestBody DeviceData data) {
        return service.findById(id)
                .map(existing -> {
                    // update fields
                    existing.setDevice_id(data.getDevice_id());
                    existing.setTemperature(data.getTemperature());
                    existing.setWeight(data.getWeight());
                    existing.setTimestamp(data.getTimestamp());
                    DeviceData updated = service.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return service.findById(id)
                .map(existing -> {
                    service.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
