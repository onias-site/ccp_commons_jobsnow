package com.ccp.especifications.http;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTextDecorator;

@SuppressWarnings("serial")
public class CcpErrorHttp extends RuntimeException {
	public final CcpJsonRepresentation entity;
	protected CcpErrorHttp(CcpJsonRepresentation entity) {
		super(getMessage(entity));
		this.entity = entity;
	}
	private static String getMessage(CcpJsonRepresentation entity) {
		String string = "\n\n\nTrace:{trace}\nDetails: {details}\n. All expected status: {expectedStatusList}";
		com.ccp.decorators.CcpStringDecorator ccpStringDecorator = new com.ccp.decorators.CcpStringDecorator(string);
		CcpTextDecorator ccpStringDecoratorText = ccpStringDecorator.text();
		var resolveTemplate = ccpStringDecoratorText.resolveTemplate(entity);
		String message = resolveTemplate.content;
		return message;
	}
}
