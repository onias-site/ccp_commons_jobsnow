package com.ccp.especifications.json;

public interface CcpJsonHandler {

	String toJson(Object md);
	
	String asPrettyJson(Object md);
	
	<T> T fromJson(String md);
	
	boolean isValidJson(String src);
	
}
