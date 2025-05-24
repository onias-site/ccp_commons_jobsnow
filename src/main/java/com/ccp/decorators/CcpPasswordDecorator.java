package com.ccp.decorators;

public class CcpPasswordDecorator implements CcpDecorator<String> {

	public final String content;

	protected CcpPasswordDecorator(String content) {
		this.content = content;
	}

	public String toString() {
		return this.content;
	}

	public boolean isStrong() {
		if (this.content.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")){
		   return true;
		} 
		return false;
	}

	public String getContent() {
		return this.content;
	}
	
}
