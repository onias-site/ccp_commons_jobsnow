package com.ccp.process;

public enum CcpDefaultProcessStatus implements CcpProcessStatus{

	INACTIVE_RECORD(302),
	UNHAUTHORIZED(401),
	BAD_REQUEST(400),
	NOT_FOUND(404),
	REDIRECT(301),
	SUCCESS(200),
	CREATED(201),
	UPDATED(204),
	UNPROCESSABLE_ENTITY(422)
	;

	public int asNumber() {
		return this.status;
	}
	
	
	
	private CcpDefaultProcessStatus(int status) {
		this.status = status;
	}



	private final int status;
}
