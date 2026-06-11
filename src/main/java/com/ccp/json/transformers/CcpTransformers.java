package com.ccp.json.transformers;

import java.util.Arrays;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

public interface CcpTransformers extends CcpBusiness {

	
	default CcpJsonRepresentation substring(CcpJsonRepresentation json, String field, int limit) {
		String value = json.getAsString(new CcpFieldName(field));
		boolean isValid = value.trim().length() <= limit;

		if (isValid) {
			return json;
		}

		String substring = value.substring(0, limit);
		CcpJsonRepresentation put = json.put(new CcpFieldName(field), substring);
		return put;
	}

	default CcpJsonRepresentation putMinValue(CcpJsonRepresentation json, String field, int minValue) {
		boolean isNotPresent = false == json.containsAllFields(new CcpFieldName(field));
		if(isNotPresent) {
			return json;
		}

		Double value = json.getAsDoubleNumber(new CcpFieldName(field));

		if(value >= minValue) {
			return json;
		}

		CcpJsonRepresentation put = json.put(new CcpFieldName(field), minValue);
		return put;
	}

	default CcpJsonRepresentation addLongValue(CcpJsonRepresentation json, String field, Long longValue) {
		String value = json.getAsString(new CcpFieldName(field));

		boolean isLongNumber = new CcpStringDecorator(value).isLongNumber();

		if(isLongNumber) {
			return json;
		}
		CcpJsonRepresentation put = json.put(new CcpFieldName(field), longValue);
		return put;

	}

	default CcpJsonRepresentation addRequiredAtLeastOne(CcpJsonRepresentation json, String field, Object value, String... fields) {
		boolean containsAnyFields = json.containsAnyFields(Arrays.asList(fields));
		if(containsAnyFields) {
			return json;
		}

		CcpJsonRepresentation put = json.put(new CcpFieldName(field), value);
		return put;
	}

}
