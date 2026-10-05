package com.ccp.process;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonFieldName;

/** Default HTTP-like process statuses used throughout jobsnow. */
public enum CcpProcessStatusDefault implements CcpProcessStatus{

	/** The record exists but is inactive (302). */
	INACTIVE_RECORD(302),
	/** The caller is not authorized (401). */
	UNHAUTHORIZED(401),
	/** The request is malformed (400). */
	BAD_REQUEST(400),
	/** The record was not found (404). */
	NOT_FOUND(404),
	/** The record already exists or the state conflicts with the request (409). */
	CONFLICT(409),
	/** The caller must be redirected (301). */
	REDIRECT(301),
	/** Success (200). */
	OK(200),
	/** A record was created (201). */
	CREATED(201),
	/** A record was updated (204). */
	UPDATED(204),
	/** The request is well formed but violates a business rule (422). */
	UNPROCESSABLE_ENTITY(422)
	;

	/**
	 * Returns the HTTP code associated with the status.
	 * @return the HTTP code
	 */
	public int asNumber() {
		return this.status;
	}
	
	
	
	/**
	 * Associates the constant with its HTTP code.
	 * @param status the HTTP code
	 */
	private CcpProcessStatusDefault(int status) {
		this.status = status;
	}


	/**
	 * Returns a {@code CcpJsonFieldName} whose value is the numeric code as text (e.g. {@code "200"}), so the code can be used as a JSON key.
	 * @return the field name built from the code
	 */
	public CcpJsonFieldName asJsonFieldName() {
		CcpJsonFieldName ccpJsonFieldName = new CcpFieldName(this.status);
		return ccpJsonFieldName; 
	}
	
	/** The HTTP code of the status. */
	public final int status;
}
