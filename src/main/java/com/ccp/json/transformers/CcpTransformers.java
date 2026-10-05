package com.ccp.json.transformers;

import java.util.Arrays;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

/**
 * Helper transformations of JSON fields, as default methods of a {@code CcpBusiness}: truncate texts, ensure minimum
 * values, fill invalid long values and require at least one field of a group. Base of entity field transformers.
 */
public interface CcpTransformers extends CcpBusiness {

	
	/**
	 * Truncates the text of the field to {@code limit} characters when its trimmed length exceeds the limit.
	 * @param json the JSON
	 * @param field the field name
	 * @param limit the maximum length
	 * @return the JSON, possibly with the truncated text
	 */
	default CcpJsonRepresentation substring(CcpJsonRepresentation json, String field, int limit) {
		CcpFieldName ccpFieldName = new CcpFieldName(field);
		String value = json.getAsString(ccpFieldName);
		String valueTrim = value.trim();
		int valueTrimLength = valueTrim.length();
		boolean isValid = valueTrimLength <= limit;

		if (isValid) {
			return json;
		}

		String substring = value.substring(0, limit);
		CcpFieldName ccpFieldName2 = new CcpFieldName(field);
		CcpJsonRepresentation put = json.put(ccpFieldName2, substring);
		return put;
	}

	/**
	 * Raises the numeric field to {@code minValue} when it is below it; an absent field is left absent.
	 * @param json the JSON
	 * @param field the field name
	 * @param minValue the minimum value
	 * @return the JSON, possibly with the raised value
	 */
	default CcpJsonRepresentation putMinValue(CcpJsonRepresentation json, String field, int minValue) {
		CcpFieldName ccpFieldName3 = new CcpFieldName(field);
		boolean containsAllFields = json.containsAllFields(ccpFieldName3);
		boolean isNotPresent = false == containsAllFields;
		if(isNotPresent) {
			return json;
		}
		CcpFieldName ccpFieldName4 = new CcpFieldName(field);

		Double value = json.getAsDoubleNumber(ccpFieldName4);
		boolean valueAtLeastMinimum = value >= minValue;

		if(valueAtLeastMinimum) {
			return json;
		}
		CcpFieldName ccpFieldName5 = new CcpFieldName(field);

		CcpJsonRepresentation put = json.put(ccpFieldName5, minValue);
		return put;
	}

	/**
	 * Sets the field to {@code longValue} when its current value is not an integer number (or is absent).
	 * @param json the JSON
	 * @param field the field name
	 * @param longValue the value to set
	 * @return the JSON, possibly with the field set
	 */
	default CcpJsonRepresentation addLongValue(CcpJsonRepresentation json, String field, Long longValue) {
		CcpFieldName ccpFieldName6 = new CcpFieldName(field);
		String value = json.getAsString(ccpFieldName6);
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(value);

		boolean isLongNumber = ccpStringDecorator.isLongNumber();

		if(isLongNumber) {
			return json;
		}
		CcpFieldName ccpFieldName7 = new CcpFieldName(field);
		CcpJsonRepresentation put = json.put(ccpFieldName7, longValue);
		return put;

	}

	/**
	 * Sets {@code field} to {@code value} when none of {@code fields} is present.
	 * @param json the JSON
	 * @param field the field to set
	 * @param value the value to set
	 * @param fields the fields of which at least one must be present
	 * @return the JSON, possibly with the field set
	 */
	default CcpJsonRepresentation addRequiredAtLeastOne(CcpJsonRepresentation json, String field, Object value, String... fields) {
		boolean containsAnyFields = json.containsAnyFields(Arrays.asList(fields));
		if(containsAnyFields) {
			return json;
		}
		CcpFieldName ccpFieldName8 = new CcpFieldName(field);

		CcpJsonRepresentation put = json.put(ccpFieldName8, value);
		return put;
	}

}
