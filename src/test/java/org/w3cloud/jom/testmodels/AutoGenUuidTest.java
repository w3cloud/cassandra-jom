package org.w3cloud.jom.testmodels;

import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;



@CqlEntity
public class AutoGenUuidTest {
	//Composite key restaurantId and id
	@CqlId
	public UUID restaurantId;
	@CqlId
	@CqlAutoGen
	public UUID id;
	public UUID employeeId; 
}
