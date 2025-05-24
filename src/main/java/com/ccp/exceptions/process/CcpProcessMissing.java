package com.ccp.exceptions.process;

@SuppressWarnings("serial")
public class CcpProcessMissing extends RuntimeException {

	public CcpProcessMissing(String processName) {
		super("The async process '" + processName + "' is missing");
	}
	
}
