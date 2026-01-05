package com.example.deviceapi.repository;

import com.example.deviceapi.model.DeviceTelementry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepository extends MongoRepository<DeviceTelementry, String> {
}
