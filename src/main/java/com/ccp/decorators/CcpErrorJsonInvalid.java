package com.ccp.decorators;


/** Raised when a text cannot be deserialized as a valid JSON object. */
@SuppressWarnings("serial")
public class CcpErrorJsonInvalid extends RuntimeException {
	/**
	 * Builds the message showing the invalid text.
	 * @param json the text that failed to be deserialized
	 */
	CcpErrorJsonInvalid(String json) {
		super("The following json is an invalid json: " + json);
	}
}
