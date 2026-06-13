package com.ccp.json.validations.global.engine;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

/**
 * Exceção lançada por {@code CcpJsonValidatorEngine} quando a validação de um JSON falha. Carrega
 * todos os dados diagnósticos: JSON fornecido, erros encontrados, explicação das regras, nome da
 * funcionalidade e classe portadora das regras.
 */
@SuppressWarnings("serial")
public class CcpJsonValidationError extends RuntimeException{

	public final CcpJsonRepresentation json;

	/** Monta o JSON de diagnóstico completo como mensagem da exceção. */
	public CcpJsonValidationError(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
		super(getErrorMessage(clazz, givenJson, errors, rulesExplanation, featureName).asPrettyJson());
		this.json =getErrorMessage(clazz, givenJson, errors, rulesExplanation, featureName);
	}

	private static CcpJsonRepresentation getErrorMessage(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
		CcpJsonRepresentation body = CcpOtherConstants.EMPTY_JSON
		.put(JsonFieldNames.errors, errors)
		.put(JsonFieldNames.featureName, featureName)
		.put(JsonFieldNames.classWithRules, clazz.getName())
		.put(JsonFieldNames.rulesExplanation, rulesExplanation)
		.put(JsonFieldNames.givenJson, givenJson);
		
		return body;
	}
}
enum JsonFieldNames implements CcpJsonFieldName{
	classWithRules, givenJson, errors, rulesExplanation, featureName
}