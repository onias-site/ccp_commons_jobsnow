package com.ccp.especifications.http;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import java.util.stream.Stream;


/**
 * Contract for raw HTTP requests, plus the check of the returned status and the factory of the HTTP error by status
 * range.
 */
public interface CcpHttpRequester {
	/** Fields of the details of a failed call. */
	enum JsonFieldNames implements CcpJsonFieldName{
		/** The statuses that were expected. */
		expectedStatusList,
		/** The call details (url, method, headers, request, status, response). */
		details,
		/** Identifier of the request. */
		trace,
		/** The response body. */
		response,
		/** The returned status. */
		status,
		/** The request body. */
		request,
		/** The request headers. */
		headers,
		/** The HTTP method. */
		method,
		/** The target URL. */
		url,
	}
	/**
	 * Runs a simple HTTP request.
	 * @param url the target URL
	 * @param method the HTTP method
	 * @param headers the request headers
	 * @param body the request body
	 * @return the response
	 */
	CcpHttpResponse executeHttpRequest(String url, CcpHttpMethods method, CcpJsonRepresentation headers, String body);
	
	/**
	 * Runs a multipart HTTP request.
	 * @param url the target URL
	 * @param method the HTTP method
	 * @param headers the request headers
	 * @param bodyTexts the text parts
	 * @param bodyBinaries the binary parts
	 * @return the response
	 */
	CcpHttpResponse executeMultiPartHttpRequest(String url, CcpHttpMethods method, CcpJsonRepresentation headers, List<CcpHttpBodyText> bodyTexts, List<CcpHttpBodyBinary> bodyBinaries);

	/**
	 * Runs the request and checks that the status is one of the expected ones.
	 * @param url the target URL
	 * @param method the HTTP method
	 * @param headers the request headers
	 * @param request the request body
	 * @param numbers the expected statuses
	 * @return the response
	 * @throws CcpErrorHttp when the status is not expected (see {@link #getHttpError})
	 */
	default CcpHttpResponse executeHttpRequest(String url, CcpHttpMethods method, CcpJsonRepresentation headers, String request, Integer... numbers) {
		CcpHttpResponse res = this.executeHttpRequest(url, method, headers, request);

		for (int expectedStatus : numbers) {
			boolean hasExpectedStatus = expectedStatus == res.httpStatus;
			if (hasExpectedStatus) {
				return res;
			}
		}
		Stream<Integer> stream = Arrays.asList(numbers).stream();
		var streamMap = stream.map(x -> "" + x);
		Set<String> expectedStatusList = streamMap.collect(Collectors.toSet());
		
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

	/**
	 * Builds the error of an unexpected status: {@link CcpErrorHttpClient} for 4xx, {@link CcpErrorHttpServer} for 5xx
	 * and a plain {@link CcpErrorHttp} for any other status.
	 * @param trace identifier of the request
	 * @param url the target URL
	 * @param method the HTTP method
	 * @param headers the request headers
	 * @param request the request body
	 * @param status the returned status
	 * @param response the response body
	 * @param expectedStatusList the expected statuses
	 * @return the error (not thrown)
	 */
	default CcpErrorHttp getHttpError(String trace, String url, CcpHttpMethods method, CcpJsonRepresentation headers,
			String request, Integer status, String response, Set<String> expectedStatusList) {
				CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON
				.put(JsonFieldNames.url, url);
				CcpJsonRepresentation put3 = put2
				.put(JsonFieldNames.method, method);
				CcpJsonRepresentation put4 = put3
				.put(JsonFieldNames.headers, headers);
				CcpJsonRepresentation put5 = put4
				.put(JsonFieldNames.request, request);
				CcpJsonRepresentation put6 = put5
				.put(JsonFieldNames.status, status);

				CcpJsonRepresentation put = put6
				.put(JsonFieldNames.response, response);
				CcpJsonRepresentation put7 = put
				.put(JsonFieldNames.trace, trace);
				CcpJsonRepresentation put8 = put7
				.put(JsonFieldNames.details, put.content);
				CcpJsonRepresentation entity = put8
				.put(JsonFieldNames.expectedStatusList, expectedStatusList);
				boolean statusOutOfRange = status >= 600;

				if (statusOutOfRange) {
			CcpErrorHttp ccpHttpError = new CcpErrorHttp(entity);
			return ccpHttpError;
		}
		boolean statusBelowClientError = status < 400;

		if (statusBelowClientError) {
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
