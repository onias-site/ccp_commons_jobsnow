package com.ccp.json.validations.global.interfaces;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Contrato para validadores de nível de classe (globais). O método default {@code getErrors}
 * orquestra a coleta de erros e, se a validação for crítica, lança
 * {@code CcpJsonValidatorErrorBreakValidationsToTheClass} para interromper as demais validações.
 */
public interface CcpJsonValidator {

	/** Retorna {@code true} se a validação global falha para o JSON e a classe informados. */
	boolean hasError(CcpJsonRepresentation json, Class<?> clazz);

	/** Gera a mensagem de erro para a validação que falhou. */
	Object getErrorMessage(CcpJsonRepresentation json, Class<?> clazz);

	/** Indica se este validador deve interromper as demais validações ao encontrar erro. */
	boolean isCriticalValidation(CcpJsonRepresentation json, Class<?> clazz);

	/** Retorna a explicação da regra de validação em linguagem natural. */
	Object getRuleExplanation(Class<?> clazz);

	/**
	 * Acumula o erro no JSON de erros; lança {@code CcpJsonValidatorErrorBreakValidationsToTheClass}
	 * se a validação for crítica.
	 */
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Class<?> clazz) {
		boolean error2 = this.hasError(json, clazz);

		boolean hasNoError = false == error2;

		if (hasNoError) {
			return CcpOtherConstants.EMPTY_JSON;
		}

		String className = clazz.getName();

		Object error = this.getErrorMessage(json, clazz);
		CcpFieldName ccpFieldName = new CcpFieldName(className);

		CcpJsonRepresentation updatedErrors = errors.addToList(ccpFieldName, error);

		boolean criticalValidation = this.isCriticalValidation(json, clazz);
		
		if(criticalValidation) {
			CcpJsonValidatorErrorBreakValidationsToTheClass ccpJsonValidatorErrorBreakValidationsToTheClass = new CcpJsonValidatorErrorBreakValidationsToTheClass(updatedErrors);
			throw ccpJsonValidatorErrorBreakValidationsToTheClass;
		}

		return updatedErrors;
	}

}
