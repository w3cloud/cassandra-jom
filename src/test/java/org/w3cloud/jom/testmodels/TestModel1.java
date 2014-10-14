package org.w3cloud.jom.testmodels;



import java.util.UUID;

import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;

@CqlEntity
public class TestModel1 {
	@CqlId
	public UUID id;

}
