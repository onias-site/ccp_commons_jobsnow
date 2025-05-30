package com.ccp.exceptions.json;

@SuppressWarnings("serial")
public class CcpErrorJsonInvalid extends RuntimeException {
	public CcpErrorJsonInvalid(String json, Throwable e) {
		super("The following json is an invalid json: " + json , e);

	}
}
