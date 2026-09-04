package com.ccp.json.validations.global.engine;

import java.util.List;
import java.util.Set;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface.CcpErrorFields;

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
		.put(CcpValidationErrorFields.errors, errors);
		CcpJsonRepresentation put4 = put3
		.put(CcpValidationErrorFields.featureName, featureName);
		String clazzName = clazz.getName();
		CcpJsonRepresentation put5 = put4
		.put(CcpValidationErrorFields.classWithRules, clazzName);
		CcpJsonRepresentation put6 = put5
		.put(CcpValidationErrorFields.rulesExplanation, rulesExplanation);
		CcpJsonRepresentation body = put6
		.put(CcpValidationErrorFields.givenJson, givenJson);
		return body;
	}

	public static enum CcpValidationErrorFields implements CcpJsonFieldName {
		classWithRules, givenJson, errors, rulesExplanation, featureName
	}

	public String getExplanedMessage() {
		CcpJsonRepresentation errors = this.json.getInnerJson(CcpValidationErrorFields.errors);
		Set<String> fieldSet = errors.fieldSet();
		String message = "";
		
		for (String field : fieldSet) {
			List<CcpJsonRepresentation> errorsFromField = errors.getAsJsonList(() -> field);
			for (CcpJsonRepresentation error : errorsFromField) {
				String errorDescription = error.getAsString(CcpErrorFields.errorDescription);
				message += (errorDescription + ". ");
			}
		}
		return message;
	}
}
