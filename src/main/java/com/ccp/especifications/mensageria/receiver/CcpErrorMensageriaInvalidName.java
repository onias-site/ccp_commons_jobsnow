package com.ccp.especifications.mensageria.receiver;

@SuppressWarnings("serial")
public class CcpErrorMensageriaInvalidName extends RuntimeException {

	public CcpErrorMensageriaInvalidName(String processName) {
		super("The process '" + processName + "' is an invalid topic");
	}
}
