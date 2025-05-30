package com.ccp.exceptions.mensageria.receiver;

@SuppressWarnings("serial")
public class CcpErrorMensageriaInvalidName extends RuntimeException {

	public CcpErrorMensageriaInvalidName(String processName) {
		super("The process '" + processName + "' is an invalid topic");
	}
}
