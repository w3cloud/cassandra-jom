package org.w3cloud.jom.testmodels;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlStoreAsJson;

@CqlEntity
public class StoreAsJsonTest {
	@CqlId
	public UUID restaurantId;
	@CqlId
	public UUID catId;
	@CqlId
	@CqlAutoGen
	public UUID id;
	public String getIdStr(){
		return id.toString();
	}
	public int sortOrder;
	
	public void setIdStr(String idStr){
		id=UUID.fromString(idStr);
	}
	public void setCatIdStr(String idStr){
		catId=UUID.fromString(idStr);
	}
	
	public String code;
	public String name;
	public String description;
	public BigDecimal price;
	@CqlStoreAsJson
	public List<Option> options;
}
