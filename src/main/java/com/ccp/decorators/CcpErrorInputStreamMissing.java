package com.ccp.decorators;


@SuppressWarnings("serial")
public class CcpErrorInputStreamMissing extends RuntimeException {
	CcpErrorInputStreamMissing(String filePath) {
		super("The file '" + filePath + "' is missing");
	}
}
