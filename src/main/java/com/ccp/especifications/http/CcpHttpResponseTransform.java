package com.ccp.especifications.http;

/**
 * Turns a {@link CcpHttpResponse} into the desired result type.
 * @param <V> the type of the result
 */
public interface CcpHttpResponseTransform<V> {

	/**
	 * Turns the response into the result type.
	 * @param response the HTTP response
	 * @return the result
	 */
	V transform(CcpHttpResponse response);
}
