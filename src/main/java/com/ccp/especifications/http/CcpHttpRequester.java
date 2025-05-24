package com.ccp.especifications.http;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.exceptions.http.CcpHttpClientError;
import com.ccp.exceptions.http.CcpHttpError;
import com.ccp.exceptions.http.CcpHttpServerError;
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
		
		CcpHttpError httpError = this.getHttpError(
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

	default CcpHttpError getHttpError(String trace, String url, CcpHttpMethods method, CcpJsonRepresentation headers,
			String request, Integer status, String response, Set<String> expectedStatusList) {

		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put("url", url).put("method", method)
				.put("headers", headers).put("request", request).put("status", status).put("response", response);
		CcpJsonRepresentation entity = put.put("trace", trace).put("details", put.content).put("expectedStatusList",
				expectedStatusList);

		if (status >= 600) {
			CcpHttpError ccpHttpError = new CcpHttpError(entity);
			return ccpHttpError;
		}

		if (status < 400) {
			CcpHttpError ccpHttpError = new CcpHttpError(entity);
			return ccpHttpError;
		}

		boolean isClientError = status < 500;

		if (isClientError) {
			CcpHttpClientError ccpHttpClientError = new CcpHttpClientError(entity);
			return ccpHttpClientError;
		}

		CcpHttpServerError ccpHttpServerError = new CcpHttpServerError(entity);
		return ccpHttpServerError;
	}

}
