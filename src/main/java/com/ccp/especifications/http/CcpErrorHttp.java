package com.ccp.especifications.http;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

/**
 * Exceção base para erros HTTP. Carrega um JSON com todos os detalhes da requisição que falhou
 * (URL, método, headers, corpo, status, resposta, status esperados).
 */
@SuppressWarnings("serial")
public class CcpErrorHttp extends RuntimeException {

	public final CcpJsonRepresentation entity;

	/**
	 * Monta a mensagem de erro formatada a partir do JSON de detalhes.
	 * @param entity JSON com os detalhes da requisição que falhou
	 */
	public CcpErrorHttp(CcpJsonRepresentation entity) {
		super(getMessage(entity));
		this.entity = entity;
	}
	private static String getMessage(CcpJsonRepresentation entity) {
		String string = "\n\n\nTrace:{trace}\nDetails: {details}\n. All expected status: {expectedStatusList}";
		String message = new CcpStringDecorator(string).text().resolveTemplate(entity).content;
		return message;
	}
	
	
	
}