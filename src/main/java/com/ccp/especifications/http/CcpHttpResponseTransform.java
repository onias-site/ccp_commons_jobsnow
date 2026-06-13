package com.ccp.especifications.http;

/**
 * Interface funcional para transformar um {@link CcpHttpResponse} no tipo de retorno desejado.
 * @param <V> tipo do resultado da transformação
 */
public interface CcpHttpResponseTransform<V> {

	/**
	 * Transforma a resposta HTTP no tipo parametrizado.
	 * @param response resposta HTTP a transformar
	 * @return resultado transformado
	 */
	V transform(CcpHttpResponse response);
}
