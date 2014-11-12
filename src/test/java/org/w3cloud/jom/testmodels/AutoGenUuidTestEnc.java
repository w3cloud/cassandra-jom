package org.w3cloud.jom.testmodels;

import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;



@CqlEntity(keyspace="jom_test")
public class AutoGenUuidTestEnc {
	//Composite key restaurantId and id
	@CqlId(idType=IdType.PARTITION_KEY)
	private UUID restaurantId;
	@CqlId(idType=IdType.PARTITION_KEY)
	@CqlAutoGen
	private UUID id;
	private UUID employeeId;
	public UUID getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(UUID restaurantId) {
		this.restaurantId = restaurantId;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(UUID employeeId) {
		this.employeeId = employeeId;
	} 
}
