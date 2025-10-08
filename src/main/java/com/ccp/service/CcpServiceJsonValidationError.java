package com.ccp.service;

import com.ccp.decorators.CcpErrorJsonInvalid;

@SuppressWarnings("serial")
public class CcpServiceJsonValidationError extends RuntimeException {
	
	public CcpServiceJsonValidationError(CcpErrorJsonInvalid e) {
		super(e);
	}
}
