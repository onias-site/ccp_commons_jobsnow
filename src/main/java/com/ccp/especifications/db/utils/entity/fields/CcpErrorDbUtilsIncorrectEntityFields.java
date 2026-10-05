package com.ccp.especifications.db.utils.entity.fields;

/** Raised by the database setup when the mapping of an entity in the database does not match its declared fields. */
@SuppressWarnings("serial")
public class CcpErrorDbUtilsIncorrectEntityFields extends RuntimeException {
	/**
	 * Builds the error with its message.
	 * @param messageError the description of the mismatch
	 */
	public CcpErrorDbUtilsIncorrectEntityFields(String messageError) {
		super(messageError);
	}
}
