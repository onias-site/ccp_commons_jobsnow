package com.ccp.json.validations.global.engine;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

@SuppressWarnings("serial")
public class CcpJsonValidationError extends RuntimeException{


	public CcpJsonValidationError(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
		super(getErrorMessage(clazz, givenJson, errors, rulesExplanation, featureName));
	}

	private static String getErrorMessage(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
		CcpJsonRepresentation body = CcpOtherConstants.EMPTY_JSON
		.put(JsonFieldNames.errors, errors)
		.put(JsonFieldNames.featureName, featureName)
		.put(JsonFieldNames.classWithRules, clazz.getName())
		.put(JsonFieldNames.rulesExplanation, rulesExplanation)
		.put(JsonFieldNames.givenJson, givenJson);
		
		String asPrettyJson = body.asPrettyJson();
		return asPrettyJson;
	}
}
enum JsonFieldNames implements CcpJsonFieldName{
	classWithRules, givenJson, errors, rulesExplanation, featureName
}