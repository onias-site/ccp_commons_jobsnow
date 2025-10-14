package com.ccp.especifications.db.utils;

@SuppressWarnings("serial")
public class CcpErrorEntityConfigurationFieldsIsMissing extends RuntimeException{
	
	public CcpErrorEntityConfigurationFieldsIsMissing(Class<?> configurationClass) {
		super("The class '" + configurationClass.getName() + "' must declare a public static enum called 'FIELDS'");
	}
}
