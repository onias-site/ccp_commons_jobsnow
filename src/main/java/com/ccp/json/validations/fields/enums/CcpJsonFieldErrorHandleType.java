package com.ccp.json.validations.fields.enums;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.engine.CcpJsonFieldErrorSkipOthersValidationsToTheField;

/**
 * Define a estratégia ao detectar erro num campo: {@code breakFieldValidation} (lança exceção para
 * interromper as demais validações) ou {@code continueFieldValidation} (continua acumulando erros).
 */
public enum CcpJsonFieldErrorHandleType {

	breakFieldValidation {
		public void maybeBreakValidation(CcpJsonRepresentation error) {
			throw new CcpJsonFieldErrorSkipOthersValidationsToTheField(error);
		}
	},
	continueFieldValidation {
		public void maybeBreakValidation(CcpJsonRepresentation error) {
		}
	}
	;
	
	/**
	 * Executa a ação da estratégia: interrompe ou continua acumulando erros.
	 * @param error JSON de erros acumulado
	 */
	public abstract void maybeBreakValidation(CcpJsonRepresentation error);
	

	
	
}
