package com.ccp.exceptions.mensageria.receiver;

@SuppressWarnings("serial")
public class CcpInvalidTopic extends RuntimeException {

	public CcpInvalidTopic(String processName) {
		super("The process '" + processName + "' is an invalid topic");
	}
}
