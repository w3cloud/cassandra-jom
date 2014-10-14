package org.w3cloud.jom.testmodels;


import java.math.BigDecimal;
import java.util.List;


public class OrderItem {
	public String name;
	public String desc;
	public String code;
	public BigDecimal price;
	public BigDecimal itemPrice;
	public List<Option> options;
	public int qty;
	public String notes; 
}
