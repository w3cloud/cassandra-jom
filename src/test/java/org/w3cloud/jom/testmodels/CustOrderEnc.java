package org.w3cloud.jom.testmodels;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEmbed;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlStoreAsJson;
import org.w3cloud.jom.annotations.CqlId.IdType;

@CqlEntity(keyspace="jom_test")
public class CustOrderEnc {
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
	@CqlId(idType=IdType.PARTITION_KEY)
	private UUID restaurantId;
	@CqlId(idType=IdType.PARTITION_KEY)
	@CqlAutoGen
	private UUID id;
	@CqlId(idType=IdType.CLUSTER_KEY)
	private UUID userId;

	public String getIdStr(){
		return (id==null)?"":id.toString();
	}
	public void setIdStr(String idStr){}
	private String orderNo;
	private String orderType; // Pickup, Eat-in, Delivery
	@CqlEmbed
	private DateWithTimeZoneEnc furtureOrderDt; // if not null, it is an immediate order.
	private String paymentType;
	private int estimatedTime; // Estimated order ready time in minutes
	@CqlStoreAsJson
	private List<OrderItem> orderItems;
	private BigDecimal orderTotal;
	private BigDecimal deliveryFee;
	private String couponCode;
	private BigDecimal discount;
	private BigDecimal tip;
	private BigDecimal taxPercent;
	private BigDecimal tax; 
	private BigDecimal total;
	private String name;
	private String phone;
	private boolean sendTextMsg;
	@CqlEmbed
	private DateWithTimeZoneEnc createDt;
	private String status;
	private String cancelReason;  // If a transaction is voided or refunded, this field will have a reason.
	
	 
	//This field is used for two stage commits.
	private String dbStatus;
	//Braintree transaction id. Not null if paid by credit card
	private String transactionId;

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
	public UUID getUserId() {
		return userId;
	}
	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public DateWithTimeZoneEnc getFurtureOrderDt() {
		return furtureOrderDt;
	}
	public void setFurtureOrderDt(DateWithTimeZoneEnc furtureOrderDt) {
		this.furtureOrderDt = furtureOrderDt;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public int getEstimatedTime() {
		return estimatedTime;
	}
	public void setEstimatedTime(int estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public BigDecimal getOrderTotal() {
		return orderTotal;
	}
	public void setOrderTotal(BigDecimal orderTotal) {
		this.orderTotal = orderTotal;
	}
	public BigDecimal getDeliveryFee() {
		return deliveryFee;
	}
	public void setDeliveryFee(BigDecimal deliveryFee) {
		this.deliveryFee = deliveryFee;
	}
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	public BigDecimal getTip() {
		return tip;
	}
	public void setTip(BigDecimal tip) {
		this.tip = tip;
	}
	public BigDecimal getTaxPercent() {
		return taxPercent;
	}
	public void setTaxPercent(BigDecimal taxPercent) {
		this.taxPercent = taxPercent;
	}
	public BigDecimal getTax() {
		return tax;
	}
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isSendTextMsg() {
		return sendTextMsg;
	}
	public void setSendTextMsg(boolean sendTextMsg) {
		this.sendTextMsg = sendTextMsg;
	}
	public DateWithTimeZoneEnc getCreateDt() {
		return createDt;
	}
	public void setCreateDt(DateWithTimeZoneEnc createDt) {
		this.createDt = createDt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	public String getDbStatus() {
		return dbStatus;
	}
	public void setDbStatus(String dbStatus) {
		this.dbStatus = dbStatus;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
