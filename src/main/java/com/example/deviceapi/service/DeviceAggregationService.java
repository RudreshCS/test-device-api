package com.example.deviceapi.service;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;

import com.example.deviceapi.model.GroupedDeviceResponse;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class DeviceAggregationService {

	private final MongoTemplate mongoTemplate;

	public DeviceAggregationService(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public List<GroupedDeviceResponse> getGroupedUnits() {

		// Group by unitId, customerId, number
		GroupOperation groupOperation = group("unitId", "customerId", "number");

		// Project only required fields
		ProjectionOperation projectionOperation = project().and("_id.unitId").as("unitId").and("_id.customerId")
				.as("customerId").and("_id.number").as("number").and("_id.unitStatus").as("unitStatus");

		Aggregation aggregation = newAggregation(groupOperation, projectionOperation);

		AggregationResults<GroupedDeviceResponse> results = mongoTemplate.aggregate(aggregation, "device_data",
				GroupedDeviceResponse.class);

		return results.getMappedResults();
	}
}
