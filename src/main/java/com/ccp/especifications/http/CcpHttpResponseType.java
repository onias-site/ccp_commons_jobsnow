package com.ccp.especifications.http;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpHttpResponseType {
	CcpHttpResponseTransform<List<CcpJsonRepresentation>> listRecord = response -> response.asListRecord();
	CcpHttpResponseTransform<CcpJsonRepresentation> singleRecord = response -> response.asSingleJson();
	CcpHttpResponseTransform<byte[]> byteArray = response -> response.httpResponse.getBytes();
	CcpHttpResponseTransform<List<Object>> listObject = response -> response.asListObject();
	CcpHttpResponseTransform<String> string = response -> response.httpResponse;
	CcpHttpResponseTransform<String> base64 = response -> response.asBase64();
	

}
