package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * Exceção lançada quando uma operação de multi-get no banco de dados retorna um erro explícito.
 */
@SuppressWarnings("serial")
public class CcpErrorCrudMultiGetSearchFailed extends RuntimeException {


	public CcpErrorCrudMultiGetSearchFailed(CcpJsonRepresentation error) {
		super(getMessage(error));
	}

	private static String getMessage(CcpJsonRepresentation error) {
		String asString = error.getAsString(CcpJsonCommonsFields.type);
		String asStringMais = asString + ". Reason: ";
		String asString2 = error.getAsString(CcpJsonCommonsFields.reason);
		String asStringMaisMais = asStringMais + asString2;
		return asStringMaisMais;
	}
}
