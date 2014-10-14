package org.w3cloud.jom.testmodels;

import java.util.Date;
public class DateWithTimeZoneEnc {
	private Date d; //Do not set these fields directly. Only use the joda date time
	private String z;
	public Date getD() {
		return d;
	}
	public void setD(Date d) {
		this.d = d;
	}
	public String getZ() {
		return z;
	}
	public void setZ(String z) {
		this.z = z;
	}
}
