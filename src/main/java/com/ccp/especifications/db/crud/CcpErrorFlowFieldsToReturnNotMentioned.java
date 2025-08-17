package com.ccp.especifications.db.crud;

@SuppressWarnings("serial")
public class CcpErrorFlowFieldsToReturnNotMentioned extends RuntimeException{
	
	public CcpErrorFlowFieldsToReturnNotMentioned(String origin) {
		super("at least one field must be mentioned. Origin: " + origin);
	}
}
