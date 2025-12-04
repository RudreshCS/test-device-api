package com.example.deviceapi.service;

import com.example.deviceapi.model.DeviceData;
import com.example.deviceapi.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceDataService {

    private final DeviceRepository repository;

    public DeviceDataService(DeviceRepository repository) {
        this.repository = repository;
    }

    public DeviceData save(DeviceData data) {
        return repository.save(data);
    }

    public Optional<DeviceData> findById(String id) {
        return repository.findById(id);
    }

    public List<DeviceData> findAll() {
        return repository.findAll();
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
