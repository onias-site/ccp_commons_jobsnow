package com.ccp.decorators;

import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public final class CcpFieldName implements CcpJsonFieldName{
	
	private final Object name;
	
	public CcpFieldName(Object name) {
		this.name = name;
	}

	public String name() {
		return "" + this.name;
	}
	
	public String toString() {
		String name = this.name();
		return name;
	}
}
