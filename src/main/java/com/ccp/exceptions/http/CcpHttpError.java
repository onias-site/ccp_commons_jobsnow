package com.ccp.exceptions.http;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

@SuppressWarnings("serial")
public class CcpHttpError extends RuntimeException {

	public final CcpJsonRepresentation entity;
	
	public CcpHttpError(CcpJsonRepresentation entity) {
		super(getMessage(entity));
		this.entity = entity;
	}
	private static String getMessage(CcpJsonRepresentation entity) {
		String string = "\n\n\nTrace:{trace}\nDetails: {details}\n. All expected status: {expectedStatusList}";
		String message = new CcpStringDecorator(string).text().getMessage(entity).content;
		return message;
	}
	
	
	
}