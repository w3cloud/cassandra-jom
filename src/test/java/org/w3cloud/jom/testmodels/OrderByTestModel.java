package org.w3cloud.jom.testmodels;

import org.w3cloud.jom.annotations.CqlColumn;
import org.w3cloud.jom.annotations.CqlColumn.DataType;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity
public class OrderByTestModel {
	@CqlId(idType=IdType.PARTITION_KEY)
	public String key1;
	@CqlId(idType=IdType.CLUSTER_KEY)
	public String key2;
	public String someData;
}
