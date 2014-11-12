package org.w3cloud.jom.testmodels;



import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity(keyspace="jom_test2")
public class TestAnotherKeyspace {
	@CqlId(idType=IdType.PARTITION_KEY)
	@CqlAutoGen
	public UUID id;

}
