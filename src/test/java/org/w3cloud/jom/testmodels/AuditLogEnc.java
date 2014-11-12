package org.w3cloud.jom.testmodels;

import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEmbed;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlIndex;
import org.w3cloud.jom.annotations.CqlId.IdType;



@CqlEntity(keyspace="jom_test")
public class AuditLogEnc {
	public static class ACTION{
		public static String VOIDED_ORDER="Voided Order";
		public static String REFUNDED_ORDER="Refunded Order";
		public static String STARTED_TAKING_ORDER="Started Taking Order";
		public static String STOPPED_TAKING_ORDER="Stopped Taking Order";
	}
	//Composite key restaurantId and id
	@CqlId(idType=IdType.PARTITION_KEY)
	private UUID restaurantId;
	@CqlId(idType=IdType.PARTITION_KEY)
	@CqlAutoGen
	private UUID id;
	@CqlIndex
	private UUID employeeId; 
	
	private static String someStatic;
	public static String getSomeStatic() {
		return someStatic;
	}
	public static void setSomeStatic(String someStatic) {
		AuditLogEnc.someStatic = someStatic;
	}
	private static String someprivateStatic;
	
	public String getIdStr(){
		return (id==null)?"":id.toString();
	}
	public void setIdStr(String idStr){}
	private String action;
	private boolean active; 
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	private String details;
	@CqlEmbed
	private DateWithTimeZoneEnc createDt;
	public AuditLogEnc(){
		
	}
	public AuditLogEnc(UUID restaurantId, UUID employeeId, String action, String details, DateWithTimeZoneEnc createDt){
		this.restaurantId=restaurantId;
		this.employeeId=employeeId;
		this.action=action;
		this.details=details;
		this.createDt=createDt;
	}
	public UUID getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(UUID restaurantId) {
		this.restaurantId = restaurantId;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(UUID employeeId) {
		this.employeeId = employeeId;
	}
	public static String getSomeprivateStatic() {
		return someprivateStatic;
	}
	public static void setSomeprivateStatic(String someprivateStatic) {
		AuditLogEnc.someprivateStatic = someprivateStatic;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public DateWithTimeZoneEnc getCreateDt() {
		return createDt;
	}
	public void setCreateDt(DateWithTimeZoneEnc createDt) {
		this.createDt = createDt;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
