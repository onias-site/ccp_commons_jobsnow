package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

/**
 * Exceção lançada no encerramento de um fluxo de busca ({@link CcpSelectFinally}) quando nenhum
 * campo de retorno foi especificado, o que tornaria o resultado vazio e provavelmente indicaria um
 * erro de programação.
 */
@SuppressWarnings("serial")
public class CcpErrorFlowFieldsToReturnNotMentioned extends RuntimeException{

	/**
	 * Inicializa a exceção com mensagem indicando a origem do fluxo que não definiu campos de retorno.
	 *
	 * @param origin identificador da origem do fluxo
	 */
	public CcpErrorFlowFieldsToReturnNotMentioned(CcpJsonFieldName origin) {
		super("at least one field must be mentioned. Origin: " + origin.name());
	}
}
