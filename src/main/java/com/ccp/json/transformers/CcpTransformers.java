package com.ccp.json.transformers;

import java.util.Arrays;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

public interface CcpTransformers extends CcpBusiness {

	
	default CcpJsonRepresentation substring(CcpJsonRepresentation json, String field, int limit) {
		String value = json.getAsString(() -> field);
		boolean isValid = value.trim().length() <= limit;

		if (isValid) {
			return json;
		}

		String substring = value.substring(0, limit);
		CcpJsonRepresentation put = json.put(() -> field, substring);
		return put;
	}

	default CcpJsonRepresentation putMinValue(CcpJsonRepresentation json, String field, int minValue) {
		boolean isNotPresent = false == json.containsAllFields(() -> field);
		if(isNotPresent) {
			return json;
		}

		Double value = json.getAsDoubleNumber(() -> field);

		if(value >= minValue) {
			return json;
		}

		CcpJsonRepresentation put = json.put(() -> field, minValue);
		return put;
	}

	default CcpJsonRepresentation addLongValue(CcpJsonRepresentation json, String field, Long longValue) {
		String value = json.getAsString(() -> field);

		boolean isLongNumber = new CcpStringDecorator(value).isLongNumber();

		if(isLongNumber) {
			return json;
		}
		CcpJsonRepresentation put = json.put(() -> field, longValue);
		return put;

	}

	default CcpJsonRepresentation addRequiredAtLeastOne(CcpJsonRepresentation json, String field, Object value, String... fields) {
		boolean containsAnyFields = json.containsAnyFields(Arrays.asList(fields));
		if(containsAnyFields) {
			return json;
		}

		CcpJsonRepresentation put = json.put(() -> field, value);
		return put;
	}

}
