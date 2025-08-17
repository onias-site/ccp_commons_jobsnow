package com.ccp.decorators;

@SuppressWarnings("serial")
public class CcpErrorInputStreamMissing extends RuntimeException{
	public CcpErrorInputStreamMissing(String filePath) {
		super("The file '" + filePath + "' is missing");
	}
}
