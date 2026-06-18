package com.ccp.especifications.http;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpRequester.CcpErrorHttp;

/**
 * Especialização de {@link CcpErrorHttp} para erros de servidor HTTP (status 5xx).
 */
@SuppressWarnings("serial")
public class CcpErrorHttpServer extends CcpErrorHttp{

	/**
	 * Delega ao construtor pai com os detalhes da requisição.
	 * @param entity JSON com os detalhes da requisição que falhou
	 */
	public CcpErrorHttpServer(CcpJsonRepresentation entity) {
		super(entity);
	}


}
