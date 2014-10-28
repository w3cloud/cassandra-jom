package org.w3cloud.jom.testmodels;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity
public class AllTypesModelEnc {
	@CqlId(idType=IdType.PARTITION_KEY)
	@CqlAutoGen
	private UUID uuid;
	private boolean boolVal;
	private long longVal;
	private BigDecimal bigDecimal;
	private double doubleVal;
	private float floatVal;
	private int intVal;
	private String stringVal;
	private Date date;
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public boolean isBoolVal() {
		return boolVal;
	}
	public void setBoolVal(boolean boolVal) {
		this.boolVal = boolVal;
	}
	public long getLongVal() {
		return longVal;
	}
	public void setLongVal(long longVal) {
		this.longVal = longVal;
	}
	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}
	public void setBigDecimal(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}
	public double getDoubleVal() {
		return doubleVal;
	}
	public void setDoubleVal(double doubleVal) {
		this.doubleVal = doubleVal;
	}
	public float getFloatVal() {
		return floatVal;
	}
	public void setFloatVal(float floatVal) {
		this.floatVal = floatVal;
	}
	public int getIntVal() {
		return intVal;
	}
	public void setIntVal(int intVal) {
		this.intVal = intVal;
	}
	public String getStringVal() {
		return stringVal;
	}
	public void setStringVal(String stringVal) {
		this.stringVal = stringVal;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
