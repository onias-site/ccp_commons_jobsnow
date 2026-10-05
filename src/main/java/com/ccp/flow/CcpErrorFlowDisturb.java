package com.ccp.flow;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;
import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * Business flow-control exception. It does not represent a technical error but an alternative outcome of a process
 * (similar to an HTTP error status). It carries the status ({@code CcpProcessStatus}), the context JSON and the
 * relevant fields, so that the {@code CcpTreeFlow} chain or the API layer can catch it and handle the situation
 * declaratively.
 */
@SuppressWarnings("serial")
public class CcpErrorFlowDisturb extends RuntimeException{
	
	/** Context JSON of the outcome. */
	public final CcpJsonRepresentation json;
	
	/** Status that identifies the outcome and selects its handler. */
	public final CcpProcessStatus status;
	
	/** Fields of the context that are relevant to the outcome (for instance, the ones that failed a check). */
	public final CcpJsonFieldName[] fields;

	/**
	 * Creates the exception with an empty context JSON.
	 * @param status the process status
	 * @param fields relevant fields of the context
	 */
	public CcpErrorFlowDisturb(CcpProcessStatus status, CcpJsonFieldName... fields) {
		this(CcpOtherConstants.EMPTY_JSON, status, fields);
	}

	/**
	 * Creates the exception with a context JSON; the message comes from {@code getErrorMessage}.
	 * @param json the context JSON
	 * @param status the process status
	 * @param fields relevant fields of the context
	 */
	public CcpErrorFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, CcpJsonFieldName... fields) {
		super(getErrorMessage(json, status));
		this.json = json;
		this.status = status;
		this.fields = fields;
	}

	/**
	 * Builds the exception message: the {@code reason} field of the JSON when present; otherwise the pretty-printed JSON
	 * plus {@code statusNumber} and {@code statusName}.
	 * @param json the context JSON
	 * @param status the process status
	 * @return the message
	 */
	private static String getErrorMessage(CcpJsonRepresentation json, CcpProcessStatus status) {
		return json.getOrDefault(CcpJsonCommonsFields.reason, () -> json.put(CcpJsonCommonsFields.statusNumber, status.asNumber()).put(CcpJsonCommonsFields.statusName, status.name()).asPrettyJson());
	}

	/**
	 * Creates the exception with a context JSON and a custom message.
	 * @param json the context JSON
	 * @param status the process status
	 * @param message the exception message
	 * @param fields relevant fields of the context
	 */
	public CcpErrorFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, String message, CcpJsonFieldName... fields) {
		super(message);
		this.json = json;
		this.status = status;
		this.fields = fields;
	}
	
	
}
