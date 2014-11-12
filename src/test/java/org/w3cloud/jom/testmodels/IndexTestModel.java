package org.w3cloud.jom.testmodels;

import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity
public class IndexTestModel {
	@CqlId(idType=IdType.PARTITION_KEY)
	public String key1;
	@CqlId(idType=IdType.CLUSTER_KEY)
	public String key2;
	public String someData;
}
