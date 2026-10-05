package com.ccp.decorators;


/**
 * Raised when a path-navigation method of {@code CcpJsonRepresentation} receives an empty array of fields, which is a
 * programming error (path not informed).
 */
@SuppressWarnings("serial")
public class CcpErrorJsonPathIsMissing extends RuntimeException {
	/**
	 * Builds the message asking for the path to be filled, including the JSON that received the call.
	 * @param json the JSON at the moment of the error
	 */
	CcpErrorJsonPathIsMissing(CcpJsonRepresentation json) {
		super("The path is empty, please fill the missing path in the json: " + json);
	}
}
