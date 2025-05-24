package com.ccp.exceptions.json;

@SuppressWarnings("serial")
public class CcpJsonInvalid extends RuntimeException {
	public CcpJsonInvalid(String json, Throwable e) {
		super("The following json is an invalid json: " + json , e);

	}
}
