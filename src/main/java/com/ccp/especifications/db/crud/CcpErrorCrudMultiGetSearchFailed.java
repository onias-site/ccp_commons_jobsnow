package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * Exception thrown when a multi-get operation on the database returns an explicit error.
 */
@SuppressWarnings("serial")
public class CcpErrorCrudMultiGetSearchFailed extends RuntimeException {


	/**
	 * Builds the message from the {@code type} and {@code reason} of the database error.
	 * @param error the error returned by the database
	 */
	public CcpErrorCrudMultiGetSearchFailed(CcpJsonRepresentation error) {
		super(getMessage(error));
	}

	/**
	 * Formats the message as {@code "<type>. Reason: <reason>"}.
	 * @param error the error returned by the database
	 * @return the message
	 */
	private static String getMessage(CcpJsonRepresentation error) {
		String errorType = error.getAsString(CcpJsonCommonsFields.type);
		String errorTypeWithReasonLabel = errorType + ". Reason: ";
		String reason = error.getAsString(CcpJsonCommonsFields.reason);
		String errorMessage = errorTypeWithReasonLabel + reason;
		return errorMessage;
	}
}
