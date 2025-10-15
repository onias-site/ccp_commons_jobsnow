package com.ccp.especifications.db.utils.entity.fields;

@SuppressWarnings("serial")
public class CcpErrorDbUtilsIncorrectEntityFields extends RuntimeException {

	public CcpErrorDbUtilsIncorrectEntityFields(String messageError) {
		super(messageError);
	}
}
