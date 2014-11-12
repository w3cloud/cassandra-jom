package org.w3cloud.jom.testmodels;



import java.util.UUID;

import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity
public class TestModel1Enc {
	@CqlId(idType=IdType.PARTITION_KEY)
	private UUID id;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

}
