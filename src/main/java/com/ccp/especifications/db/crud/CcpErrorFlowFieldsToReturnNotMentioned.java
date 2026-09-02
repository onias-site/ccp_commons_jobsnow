package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonFieldName;

/**
 * Exceção lançada no encerramento de um fluxo de busca ({@link CcpSelectFinally}) quando nenhum
 * campo de retorno foi especificado.
 */
@SuppressWarnings("serial")
public class CcpErrorFlowFieldsToReturnNotMentioned extends RuntimeException {

	public CcpErrorFlowFieldsToReturnNotMentioned(CcpJsonFieldName origin) {
		super(getMessage(origin));
	}

	private static String getMessage(CcpJsonFieldName origin) {
		String originName = origin.name();
		String valorMais2 = "at least one field must be mentioned. Origin: " + originName;
		return valorMais2;
	}
}
