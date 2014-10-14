package org.w3cloud.jom.testmodels;


import java.math.BigDecimal;
import java.util.List;


public class OrderItemEnc {
	public String name;
	public String desc;
	public String code;
	public BigDecimal price;
	public BigDecimal itemPrice;
	public List<OptionEnc> options;
	public int qty;
	public String notes; 
}
