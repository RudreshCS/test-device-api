package com.example.deviceapi.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.deviceapi.model.DeviceTelementry;

public interface DeviceRepository extends MongoRepository<DeviceTelementry, String> {

	List<DeviceTelementry> findAllByUnitId(String unitId);

	@Aggregation(pipeline = {
			"{ $group: { " + "_id: { unitId: '$unitId', customerId: '$customerId', number: '$number' }, "
					+ "records: { $push: '$$ROOT' }" + "} }",
			"{ $project: { " + "_id: 0, " + "unitId: '$_id.unitId', " + "customerId: '$_id.customerId', "
					+ "number: '$_id.number', " + "records: 1" + "} }" })
	List<Map<String, Object>> groupByUnitCustomerAndNumber();
}
