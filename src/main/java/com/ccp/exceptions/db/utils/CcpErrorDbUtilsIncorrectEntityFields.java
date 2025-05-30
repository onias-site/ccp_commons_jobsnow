package com.ccp.exceptions.db.utils;

@SuppressWarnings("serial")
public class CcpErrorDbUtilsIncorrectEntityFields extends RuntimeException {

	public CcpErrorDbUtilsIncorrectEntityFields(String messageError) {
		super(messageError);
	}
}
