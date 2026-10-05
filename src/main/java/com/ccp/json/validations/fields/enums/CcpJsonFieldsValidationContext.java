package com.ccp.json.validations.fields.enums;

/** The validation context of a value: the field value itself or an item of a collection. */
public enum CcpJsonFieldsValidationContext {
	/** An item of a collection. */
	collection,
	/** The value of the field itself. */
	single
}
