package org.w3cloud.jom.testmodels;

import java.math.BigDecimal;

public class OptionResponseEnc {
	private String respText;
	private BigDecimal extraCost;
	private boolean selected;
	public String getRespText() {
		return respText;
	}
	public void setRespText(String respText) {
		this.respText = respText;
	}
	public BigDecimal getExtraCost() {
		return extraCost;
	}
	public void setExtraCost(BigDecimal extraCost) {
		this.extraCost = extraCost;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}


