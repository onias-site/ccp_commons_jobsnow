package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Exceção lançada quando uma operação de multi-get no banco de dados retorna um erro explícito.
 */
@SuppressWarnings("serial")
public class CcpErrorCrudMultiGetSearchFailed extends RuntimeException {

	enum JsonFieldNames implements CcpJsonFieldName {
		type, reason
	}

	public CcpErrorCrudMultiGetSearchFailed(CcpJsonRepresentation error) {
		super(getMessage(error));
	}

	private static String getMessage(CcpJsonRepresentation error) {
		String asString = error.getAsString(JsonFieldNames.type);
		String asStringMais = asString + ". Reason: ";
		String asString2 = error.getAsString(JsonFieldNames.reason);
		String asStringMaisMais = asStringMais + asString2;
		return asStringMaisMais;
	}
}
