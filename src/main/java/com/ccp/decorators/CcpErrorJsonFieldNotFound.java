package com.ccp.decorators;


/**
 * Raised when the value of a mandatory field of a {@code CcpJsonRepresentation} is requested and the field is absent
 * (or {@code null}).
 */
@SuppressWarnings("serial")
public class CcpErrorJsonFieldNotFound extends RuntimeException {
	/**
	 * Builds the message naming the absent field and the full content of the JSON at the moment of the error.
	 * @param field the name of the absent field (or the list of names tried)
	 * @param json the JSON at the moment of the error
	 */
	CcpErrorJsonFieldNotFound(String field, CcpJsonRepresentation json) {
		super("The value is absent to the field " + field + " in json: " + json);
	}
}
