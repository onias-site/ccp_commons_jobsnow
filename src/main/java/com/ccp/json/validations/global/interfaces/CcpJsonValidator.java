package com.ccp.json.validations.global.interfaces;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.global.engine.CcpJsonValidatorErrorBreakValidationsToTheClass;

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

		boolean hasNoError = false == this.hasError(json, clazz);

		if (hasNoError) {
			return CcpOtherConstants.EMPTY_JSON;
		}

		String className = clazz.getName();

		Object error = this.getErrorMessage(json, clazz);

		CcpJsonRepresentation updatedErrors = errors.addToList(new CcpFieldName(className), error);

		boolean criticalValidation = this.isCriticalValidation(json, clazz);
		
		if(criticalValidation) {
			throw new CcpJsonValidatorErrorBreakValidationsToTheClass(updatedErrors);
		}
		
		return updatedErrors;
	}

	
}
