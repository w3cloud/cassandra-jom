package org.w3cloud.jom.testmodels;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEmbed;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlStoreAsJson;

@CqlEntity
public class CustOrder {
	public static class ORDER_TYPE{
		public static String PICKUP="Pickup";
		public static String DINE_IN="Dine in";
		public static String DELIVERY="Delivery";
	}
	public static class PAYMENT_TYPE{
		public static String PAY_AT_PICKUP="Pay at pickup";
		public static String CREDIT_CARD="Credit Card";
	}
	//Draft, Placed, Accepted, In-Progress, Ready, Fulfilled, Complete
	public static class STATUS{
		public static String OPEN="Open";
		public static String ACCEPTED="Accepted";
		public static String INPROGRESS="In-Progress";
		public static String READY="Ready";
		public static String COMPLETE="Complete";
		public static String CANCELLED="Cancelled";
	}
	@CqlId
	public UUID restaurantId;
	@CqlId
	@CqlAutoGen
	public UUID id;
	@CqlId
	public UUID userId;

	public String getIdStr(){
		return (id==null)?"":id.toString();
	}
	public void setIdStr(String idStr){}
	public String orderNo;
	public String orderType; // Pickup, Eat-in, Delivery
	@CqlEmbed
	public DateWithTimeZone furtureOrderDt; // if not null, it is an immediate order.
	public String paymentType;
	public int estimatedTime; // Estimated order ready time in minutes
	@CqlStoreAsJson
	public List<OrderItemEnc> orderItems;
	public BigDecimal orderTotal;
	public BigDecimal deliveryFee;
	public String couponCode;
	public BigDecimal discount;
	public BigDecimal tip;
	public BigDecimal taxPercent;
	public BigDecimal tax; 
	public BigDecimal total;
	public String name;
	public String phone;
	public boolean sendTextMsg;
	@CqlEmbed
	public DateWithTimeZone createDt;
	public String status;
	public String cancelReason;  // If a transaction is voided or refunded, this field will have a reason.
	
	 
	//This field is used for two stage commits.
	public String dbStatus;
	//Braintree transaction id. Not null if paid by credit card
	public String transactionId;
}
