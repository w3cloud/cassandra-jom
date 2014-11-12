package org.w3cloud.jom.testmodels;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity(keyspace="jom_test")
public class AllTypesModel {
	@CqlId(idType=IdType.PARTITION_KEY)
	@CqlAutoGen
	public UUID uuid;
	public boolean boolVal;
	public long longVal;
	public BigDecimal bigDecimal;
	public double doubleVal;
	public float floatVal;
	public int intVal;
	public String stringVal;
	public Date date;
}
