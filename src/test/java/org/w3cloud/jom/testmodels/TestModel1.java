package org.w3cloud.jom.testmodels;



import java.util.UUID;

import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity
public class TestModel1 {
	@CqlId(idType=IdType.PARTITION_KEY)
	public UUID id;

}
