package org.w3cloud.jom.testmodels;

import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEmbed;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlIndex;



@CqlEntity
public class AuditLog {
	public static class ACTION{
		public static String VOIDED_ORDER="Voided Order";
		public static String REFUNDED_ORDER="Refunded Order";
		public static String STARTED_TAKING_ORDER="Started Taking Order";
		public static String STOPPED_TAKING_ORDER="Stopped Taking Order";
	}
	//Composite key restaurantId and id
	@CqlId
	public UUID restaurantId;
	@CqlId
	@CqlAutoGen
	public UUID id;
	@CqlIndex
	public UUID employeeId; 
	
	private static String someStatic;
	public static String getSomeStatic() {
		return someStatic;
	}
	public static void setSomeStatic(String someStatic) {
		AuditLog.someStatic = someStatic;
	}
	public static String somePublicStatic;
	
	public String getIdStr(){
		return (id==null)?"":id.toString();
	}
	public void setIdStr(String idStr){}
	private String action;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String details;
	@CqlEmbed
	public DateWithTimeZone createDt;
	public AuditLog(){
		
	}
	public AuditLog(UUID restaurantId, UUID employeeId, String action, String details, DateWithTimeZone createDt){
		this.restaurantId=restaurantId;
		this.employeeId=employeeId;
		this.action=action;
		this.details=details;
		this.createDt=createDt;
	}
}
