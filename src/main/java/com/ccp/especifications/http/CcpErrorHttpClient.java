package com.ccp.especifications.http;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpRequester.CcpErrorHttp;

/**
 * Especialização de {@link CcpErrorHttp} para erros de cliente HTTP (status 4xx).
 */
@SuppressWarnings("serial")
public class CcpErrorHttpClient extends CcpErrorHttp{

	/**
	 * Delega ao construtor pai com os detalhes da requisição.
	 * @param entity JSON com os detalhes da requisição que falhou
	 */
	public CcpErrorHttpClient(CcpJsonRepresentation entity) {
		super(entity);
	}



}
