package com.ccp.exceptions.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpErrorDbCrudMultiGetSearchFailed extends RuntimeException {

	public CcpErrorDbCrudMultiGetSearchFailed(CcpJsonRepresentation error) {
		super(error.getAsString("type") + ". Reason: " + error.getAsString("reason"));
	}
	
}
