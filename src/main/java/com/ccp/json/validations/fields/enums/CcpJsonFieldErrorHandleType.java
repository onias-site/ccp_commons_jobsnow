package com.ccp.json.validations.fields.enums;

import com.ccp.decorators.CcpJsonRepresentation;

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

	/**
	 * Exceção de controle de fluxo que interrompe as demais validações do campo atual ao ser lançada
	 * por um validador do tipo {@code breakFieldValidation}.
	 */
	@SuppressWarnings("serial")
	public static class CcpJsonFieldErrorSkipOthersValidationsToTheField extends RuntimeException {

		public final CcpJsonRepresentation validationResultFromField;

		/**
		 * Armazena o JSON de erros acumulado em {@code validationResultFromField}.
		 * @param error JSON com os erros acumulados até o momento
		 */
		private CcpJsonFieldErrorSkipOthersValidationsToTheField(CcpJsonRepresentation error) {
			this.validationResultFromField = error;
		}
	}
}
