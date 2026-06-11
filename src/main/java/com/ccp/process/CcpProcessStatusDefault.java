package com.ccp.process;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public enum CcpProcessStatusDefault implements CcpProcessStatus{

	INACTIVE_RECORD(302),
	UNHAUTHORIZED(401),
	BAD_REQUEST(400),
	NOT_FOUND(404),
	CONFLICT(409),
	REDIRECT(301),
	OK(200),
	CREATED(201),
	UPDATED(204),
	UNPROCESSABLE_ENTITY(422)
	;

	public int asNumber() {
		return this.status;
	}
	
	
	
	private CcpProcessStatusDefault(int status) {
		this.status = status;
	}


	public CcpJsonFieldName asJsonFieldName() {
		CcpJsonFieldName ccpJsonFieldName = new CcpFieldName(this.status);
		return ccpJsonFieldName; 
	}
	
	private final int status;
}
