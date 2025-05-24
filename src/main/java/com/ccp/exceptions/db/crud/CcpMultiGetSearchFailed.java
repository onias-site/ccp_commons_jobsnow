package com.ccp.exceptions.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpMultiGetSearchFailed extends RuntimeException {

	public CcpMultiGetSearchFailed(CcpJsonRepresentation error) {
		super(error.getAsString("type") + ". Reason: " + error.getAsString("reason"));
	}
	
}
