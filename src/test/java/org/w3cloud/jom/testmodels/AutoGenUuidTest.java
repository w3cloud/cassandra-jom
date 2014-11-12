package org.w3cloud.jom.testmodels;

import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;



@CqlEntity
public class AutoGenUuidTest {
	//Composite key restaurantId and id
	@CqlId(idType=IdType.PARTITION_KEY)
	public UUID restaurantId;
	@CqlId(idType=IdType.PARTITION_KEY)
	@CqlAutoGen
	public UUID id;
	public UUID employeeId; 
}
