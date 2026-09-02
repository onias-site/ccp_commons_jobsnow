package com.ccp.json.validations.global.engine;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;

/**
 * Exceção lançada por {@code CcpJsonValidatorEngine} quando a validação de um JSON falha. Carrega
 * todos os dados diagnósticos: JSON fornecido, erros encontrados, explicação das regras, nome da
 * funcionalidade e classe portadora das regras.
 */
@SuppressWarnings("serial")
public class CcpJsonValidationError extends RuntimeException {

	public final CcpJsonRepresentation json;

	/** Monta o JSON de diagnóstico completo como mensagem da exceção. */
	CcpJsonValidationError(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
		super(getErrorMessage(clazz, givenJson, errors, rulesExplanation, featureName).asPrettyJson());
		this.json = getErrorMessage(clazz, givenJson, errors, rulesExplanation, featureName);
	}

	private static CcpJsonRepresentation getErrorMessage(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
		CcpJsonRepresentation put3 = CcpOtherConstants.EMPTY_JSON
		.put(ValidationErrorFields.errors, errors);
		CcpJsonRepresentation put4 = put3
		.put(ValidationErrorFields.featureName, featureName);
		String clazzName = clazz.getName();
		CcpJsonRepresentation put5 = put4
		.put(ValidationErrorFields.classWithRules, clazzName);
		CcpJsonRepresentation put6 = put5
		.put(ValidationErrorFields.rulesExplanation, rulesExplanation);
		CcpJsonRepresentation body = put6
		.put(ValidationErrorFields.givenJson, givenJson);
		return body;
	}

	private enum ValidationErrorFields implements CcpJsonFieldName {
		classWithRules, givenJson, errors, rulesExplanation, featureName
	}
}
