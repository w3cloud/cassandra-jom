package org.w3cloud.jom.testmodels;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlStoreAsJson;

@CqlEntity
public class StoreAsJsonTestEnc {
	@CqlId
	private UUID restaurantId;
	@CqlId
	private UUID catId;
	@CqlId
	@CqlAutoGen
	private UUID id;
	public String getIdStr(){
		return id.toString();
	}
	private int sortOrder;
	
	public void setIdStr(String idStr){
		id=UUID.fromString(idStr);
	}
	public void setCatIdStr(String idStr){
		catId=UUID.fromString(idStr);
	}
	
	private String code;
	private String name;
	private String description;
	private BigDecimal price;
	@CqlStoreAsJson
	private List<OptionEnc> options;
	public UUID getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(UUID restaurantId) {
		this.restaurantId = restaurantId;
	}
	public UUID getCatId() {
		return catId;
	}
	public void setCatId(UUID catId) {
		this.catId = catId;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public List<OptionEnc> getOptions() {
		return options;
	}
	public void setOptions(List<OptionEnc> options) {
		this.options = options;
	}
}
