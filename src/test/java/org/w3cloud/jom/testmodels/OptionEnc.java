package org.w3cloud.jom.testmodels;

import java.util.List;

public class OptionEnc {
	private String id;
	private String name;
	private String optType;
	private List<OptionResponseEnc> responses;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOptType() {
		return optType;
	}
	public void setOptType(String optType) {
		this.optType = optType;
	}
	public List<OptionResponseEnc> getResponses() {
		return responses;
	}
	public void setResponses(List<OptionResponseEnc> responses) {
		this.responses = responses;
	}
	
}
