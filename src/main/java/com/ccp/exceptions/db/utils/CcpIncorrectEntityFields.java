package com.ccp.exceptions.db.utils;

@SuppressWarnings("serial")
public class CcpIncorrectEntityFields extends RuntimeException {

	public CcpIncorrectEntityFields(String messageError) {
		super(messageError);
	}
}
