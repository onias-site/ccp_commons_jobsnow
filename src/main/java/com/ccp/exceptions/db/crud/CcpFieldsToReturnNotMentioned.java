package com.ccp.exceptions.db.crud;

@SuppressWarnings("serial")
public class CcpFieldsToReturnNotMentioned extends RuntimeException{
	
	public CcpFieldsToReturnNotMentioned() {
		super("at least one field must be mentioned");
	}
}
