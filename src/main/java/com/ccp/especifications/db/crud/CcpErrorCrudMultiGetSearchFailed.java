package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * Exception thrown when a multi-get operation on the database returns an explicit error.
 */
@SuppressWarnings("serial")
public class CcpErrorCrudMultiGetSearchFailed extends RuntimeException {


	public CcpErrorCrudMultiGetSearchFailed(CcpJsonRepresentation error) {
		super(getMessage(error));
	}

	private static String getMessage(CcpJsonRepresentation error) {
		String errorType = error.getAsString(CcpJsonCommonsFields.type);
		String errorTypeWithReasonLabel = errorType + ". Reason: ";
		String reason = error.getAsString(CcpJsonCommonsFields.reason);
		String errorMessage = errorTypeWithReasonLabel + reason;
		return errorMessage;
	}
}
