package org.w3cloud.jom.testmodels;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlStoreAsJson;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity
public class StoreAsJsonTest {
	@CqlId(idType=IdType.PARTITION_KEY)
	public UUID restaurantId;
	@CqlId(idType=IdType.PARTITION_KEY)
	public UUID catId;
	@CqlId(idType=IdType.CLUSTER_KEY)
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
