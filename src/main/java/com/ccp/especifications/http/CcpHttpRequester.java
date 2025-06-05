package com.ccp.especifications.http;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.exceptions.http.CcpErrorHttpClient;
import com.ccp.exceptions.http.CcpErrorHttp;
import com.ccp.exceptions.http.CcpErrorHttpServer;
import com.ccp.http.CcpHttpMethods;

public interface CcpHttpRequester {
	CcpHttpResponse executeHttpRequest(String url, CcpHttpMethods method, CcpJsonRepresentation headers, String body);

	default CcpHttpResponse executeHttpRequest(String url, CcpHttpMethods method, CcpJsonRepresentation headers, String request,
			Integer... numbers) {
		CcpHttpResponse res = this.executeHttpRequest(url, method, headers, request);

		for (int expectedStatus : numbers) {
			if (expectedStatus == res.httpStatus) {
				return res;
			}
		}
		Set<String> expectedStatusList = Arrays.asList(numbers).stream().map(x -> "" + x).collect(Collectors.toSet());
		
		CcpErrorHttp httpError = this.getHttpError(
				"", 
				url, 
				method, 
				headers, 
				request, 
				res.httpStatus, 
				res.httpResponse, 
				expectedStatusList
				);
		
		throw httpError;
	}

	default CcpErrorHttp getHttpError(String trace, String url, CcpHttpMethods method, CcpJsonRepresentation headers,
			String request, Integer status, String response, Set<String> expectedStatusList) {

		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put("url", url).put("method", method)
				.put("headers", headers).put("request", request).put("status", status).put("response", response);
		CcpJsonRepresentation entity = put.put("trace", trace).put("details", put.content).put("expectedStatusList",
				expectedStatusList);

		if (status >= 600) {
			CcpErrorHttp ccpHttpError = new CcpErrorHttp(entity);
			return ccpHttpError;
		}

		if (status < 400) {
			CcpErrorHttp ccpHttpError = new CcpErrorHttp(entity);
			return ccpHttpError;
		}

		boolean isClientError = status < 500;

		if (isClientError) {
			CcpErrorHttpClient ccpHttpClientError = new CcpErrorHttpClient(entity);
			return ccpHttpClientError;
		}

		CcpErrorHttpServer ccpHttpServerError = new CcpErrorHttpServer(entity);
		return ccpHttpServerError;
	}
	


}
