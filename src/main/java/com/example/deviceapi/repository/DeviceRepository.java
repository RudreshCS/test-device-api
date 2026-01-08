package com.example.deviceapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.deviceapi.model.DeviceTelementry;

public interface DeviceRepository extends MongoRepository<DeviceTelementry, String> {
	
	  List<DeviceTelementry> findAllByUnitId(String unitId);
}
