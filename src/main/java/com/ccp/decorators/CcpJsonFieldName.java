package com.ccp.decorators;

public interface CcpJsonFieldName{

	default String getValue() {
		String name = this.name();
		return name;
	}

	String name();
}
