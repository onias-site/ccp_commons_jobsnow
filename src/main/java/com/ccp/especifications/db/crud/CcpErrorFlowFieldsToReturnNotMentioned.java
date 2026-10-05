package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonFieldName;

/**
 * Exception thrown at the end of a search flow ({@link CcpSelectFinally}) when no
 * return field was specified.
 */
@SuppressWarnings("serial")
public class CcpErrorFlowFieldsToReturnNotMentioned extends RuntimeException {

	/**
	 * Builds the error naming the origin of the search flow.
	 * @param origin the context that started the search flow
	 */
	public CcpErrorFlowFieldsToReturnNotMentioned(CcpJsonFieldName origin) {
		super(getMessage(origin));
	}

	/**
	 * Builds the message naming the origin of the search flow.
	 * @param origin the context that started the search flow
	 * @return the message
	 */
	private static String getMessage(CcpJsonFieldName origin) {
		String originName = origin.name();
		String errorMessage = "at least one field must be mentioned. Origin: " + originName;
		return errorMessage;
	}
}
