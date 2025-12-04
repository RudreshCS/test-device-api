package com.example.deviceapi.controller;

import com.example.deviceapi.model.DeviceData;
import com.example.deviceapi.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
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
