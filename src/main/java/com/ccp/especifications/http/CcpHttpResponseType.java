package com.ccp.especifications.http;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;

/** Ready-made transformations for the most common result types of HTTP responses. */
public interface CcpHttpResponseType {
	/** The body as a list of records (see {@link CcpHttpResponse#asListRecord()}). */
	CcpHttpResponseTransform<List<CcpJsonRepresentation>> listRecord = response -> response.asListRecord();
	/** The body as one JSON (empty when the body is not a JSON object). */
	CcpHttpResponseTransform<CcpJsonRepresentation> singleRecord = response -> response.asSingleJson();
	/** The bytes of the body (platform default charset). */
	CcpHttpResponseTransform<byte[]> byteArray = response -> response.httpResponse.getBytes();
	/** The body as a list of objects. */
	CcpHttpResponseTransform<List<Object>> listObject = response -> response.asListObject();
	/** The raw body. */
	CcpHttpResponseTransform<String> string = response -> response.httpResponse;
	/** The body encoded in Base64. */
	CcpHttpResponseTransform<String> base64 = response -> response.asBase64();
	

}
