package com.ccp.exceptions.inputstream;

@SuppressWarnings("serial")
public class CcpMissingInputStream extends RuntimeException{
	public CcpMissingInputStream(String filePath) {
		super("The file '" + filePath + "' is missing");
	}
}
