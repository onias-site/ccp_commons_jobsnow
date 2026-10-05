package com.ccp.especifications.db.utils.entity.fields;

/**
 * Raised by an entity field transformer when the transformation cannot be applied to the value in the JSON.
 * {@code DecoratorFieldsTransformerEntity} catches it silently and keeps the value untransformed.
 */
@SuppressWarnings("serial")
public class CcpEntityJsonTransformerError extends RuntimeException{

	/**
	 * Builds the error with its message.
	 * @param message the description of the failure
	 */
	public CcpEntityJsonTransformerError(String message) {
		super(message);
	}
}
