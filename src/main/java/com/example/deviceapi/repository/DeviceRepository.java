package com.example.deviceapi.repository;

import com.example.deviceapi.model.DeviceData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepository extends MongoRepository<DeviceData, String> {
}
