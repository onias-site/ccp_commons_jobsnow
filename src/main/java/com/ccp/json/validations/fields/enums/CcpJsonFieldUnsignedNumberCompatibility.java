package com.ccp.json.validations.fields.enums;

import java.util.function.Predicate;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

/**
 * Compatibility check of {@code CcpJsonFieldDefaultTypes.NumberUnsigned}: the value of the field is an integer that is
 * not negative.
 */
public class CcpJsonFieldUnsignedNumberCompatibility implements Predicate<CcpJsonRepresentation> {

	/** The name of the field checked. */
	private final String fieldName;

	/**
	 * Keeps the field to check.
	 * @param fieldName the field name
	 */
	public CcpJsonFieldUnsignedNumberCompatibility(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Tells whether the value of the field is a non-negative integer.
	 * @param json the JSON being validated
	 * @return {@code true} when the value is compatible
	 */
	public boolean test(CcpJsonRepresentation json) {
		CcpFieldName ccpFieldName = new CcpFieldName(this.fieldName);
		CcpStringDecorator asStringDecorator = json.getAsStringDecorator(ccpFieldName);
		var longNumber = asStringDecorator.isLongNumber();
		boolean isNotLongNumber = false == longNumber;

		if(isNotLongNumber) {
			return false;
		}

		Long asLongNumber = json.getAsLongNumber(ccpFieldName);
		boolean isNotNegative = asLongNumber >= 0;
		return isNotNegative;
	}
}
