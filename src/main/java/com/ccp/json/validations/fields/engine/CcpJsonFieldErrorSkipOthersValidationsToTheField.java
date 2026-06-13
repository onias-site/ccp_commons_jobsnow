package com.ccp.json.validations.fields.engine;

import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Exceção de controle de fluxo que interrompe as demais validações do campo atual ao ser lançada
 * por um validador do tipo {@code breakFieldValidation}.
 */
@SuppressWarnings("serial")
public class CcpJsonFieldErrorSkipOthersValidationsToTheField extends RuntimeException{

	public final CcpJsonRepresentation validationResultFromField;

	/**
	 * Armazena o JSON de erros acumulado em {@code validationResultFromField}.
	 * @param error JSON com os erros acumulados até o momento
	 */
	public CcpJsonFieldErrorSkipOthersValidationsToTheField(CcpJsonRepresentation error) {
		this.validationResultFromField = error;
	}
	
	
	
}
