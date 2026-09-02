package com.ccp.json.validations.global.interfaces;

import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Exceção de controle de fluxo que interrompe as validações de nível de classe quando um validador
 * crítico encontra um erro. Carrega o JSON de erros acumulado até o momento da interrupção.
 */
@SuppressWarnings("serial")
public class CcpJsonValidatorErrorBreakValidationsToTheClass extends RuntimeException {

	public final CcpJsonRepresentation errors;

	/** Armazena o JSON de erros acumulado no momento da interrupção. */
	CcpJsonValidatorErrorBreakValidationsToTheClass(CcpJsonRepresentation errors) {
		this.errors = errors;
	}
}
