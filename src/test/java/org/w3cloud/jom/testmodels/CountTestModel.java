package org.w3cloud.jom.testmodels;

import org.w3cloud.jom.annotations.CqlColumn;
import org.w3cloud.jom.annotations.CqlColumn.DataType;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity(keyspace="jom_test")
public class CountTestModel {
	@CqlId(idType=IdType.PARTITION_KEY)
	public String someKey;
	@CqlColumn(dataType=DataType.COUNTER)
	public long ct;

}
