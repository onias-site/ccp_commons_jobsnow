package com.ccp.especifications.http;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

@SuppressWarnings("serial")
public class CcpErrorHttp extends RuntimeException {

	public final CcpJsonRepresentation entity;
	
	public CcpErrorHttp(CcpJsonRepresentation entity) {
		super(getMessage(entity));
		this.entity = entity;
	}
	private static String getMessage(CcpJsonRepresentation entity) {
		String string = "\n\n\nTrace:{trace}\nDetails: {details}\n. All expected status: {expectedStatusList}";
		String message = new CcpStringDecorator(string).text().getMessage(entity).content;
		return message;
	}
	
	
	
}