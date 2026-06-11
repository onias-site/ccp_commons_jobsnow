package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

@SuppressWarnings("serial")
public class CcpErrorFlowFieldsToReturnNotMentioned extends RuntimeException{
	
	public CcpErrorFlowFieldsToReturnNotMentioned(CcpJsonFieldName origin) {
		super("at least one field must be mentioned. Origin: " + origin.name());
	}
}
