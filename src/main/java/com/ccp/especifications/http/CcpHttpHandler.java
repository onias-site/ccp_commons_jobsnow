package com.ccp.especifications.http;

import java.util.List;
import java.util.Set;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;


/**
 * Runs HTTP requests through {@link CcpHttpRequester} and routes the response by status: each mapped status has a
 * business run over the JSON response; an unmapped status goes to the alternative flow, when there is one, or raises
 * {@link CcpErrorHttp}.
 */
public final class CcpHttpHandler {

	/** The target URL. */
	private final String url;
	/** The flows by status (status as field name, business as value). */
	private final CcpJsonRepresentation flows;
	/** The flow of the unmapped statuses, when {@link #hasAlternativeFlow} is set. */
	private final CcpBusiness alternativeFlow;
	/**
	 * Tells whether an unmapped status has somewhere to go. Up to 2026-09-27 the absence of an alternative flow was
	 * represented by {@code alternativeFlow = null}; when the aspect that forbids {@code null} came in (commit cd97ba0,
	 * 2026-07-31), the value became {@code DO_NOTHING} and every unexpected status (400, 404, 500) started to be treated
	 * as success, with the error JSON of the server in place of the response.
	 */
	private final boolean hasAlternativeFlow;
	/** The HTTP requester registered in the dependency injection. */
	public final CcpHttpRequester ccpHttp = CcpDependencyInjection.getDependency(CcpHttpRequester.class);

	/**
	 * Creates a handler with an explicit map of flows; a status outside the map raises {@link CcpErrorHttp}.
	 * @param flows the flows by HTTP status
	 * @param url the target URL
	 */
	public CcpHttpHandler(CcpJsonRepresentation flows, String url) {
		this.alternativeFlow = CcpOtherConstants.DO_NOTHING;
		this.hasAlternativeFlow = false;
		this.flows = flows;
		this.url = url;

	}

	/**
	 * Creates a handler that accepts one status as is and sends every other status to the alternative flow.
	 * @param httpStatus the status accepted as is
	 * @param alternativeFlow the flow of every other status
	 * @param url the target URL
	 */
	public CcpHttpHandler(Integer httpStatus, CcpBusiness alternativeFlow, String url) {
		this.flows = CcpOtherConstants.EMPTY_JSON.addJsonTransformer(httpStatus, CcpOtherConstants.DO_NOTHING);
		this.alternativeFlow = alternativeFlow;
		this.hasAlternativeFlow = true;
		this.url = url;
	}

	/**
	 * Creates a handler that accepts a single status; any other status raises {@link CcpErrorHttp}.
	 * @param httpStatus the accepted status
	 * @param url the target URL
	 */
	public CcpHttpHandler(Integer httpStatus, String url) {
		this.flows = CcpOtherConstants.EMPTY_JSON.addJsonTransformer(httpStatus, CcpOtherConstants.DO_NOTHING);
		this.alternativeFlow = CcpOtherConstants.DO_NOTHING;
		this.hasAlternativeFlow = false;
		this.url = url;
	}
	
	
	/**
	 * Runs a GET without headers nor body.
	 * @param <V> the result type
	 * @param trace identifier of the request in error messages
	 * @param transformer turns the response into the result
	 * @return the result
	 */
	public <V> V executeHttpSimplifiedGet(String trace, CcpHttpResponseTransform<V> transformer) {
		V executeHttpRequest = this.executeHttpRequest(trace, CcpHttpMethods.GET, CcpOtherConstants.EMPTY_JSON, CcpOtherConstants.EMPTY_JSON, transformer);
		return executeHttpRequest;
	}
	
	/**
	 * Runs a request with a JSON body (sent as compact JSON).
	 * @param <V> the result type
	 * @param trace identifier of the request in error messages
	 * @param method the HTTP method
	 * @param headers the request headers
	 * @param body the request body
	 * @param transformer turns the response into the result
	 * @return the result
	 */
	public <V> V executeHttpRequest(String trace, CcpHttpMethods method, CcpJsonRepresentation headers, CcpJsonRepresentation body, CcpHttpResponseTransform<V> transformer) {
		
		String asJson = body.asUgglyJson();
		V executeHttpRequest = this.executeHttpRequest(trace, method, headers, asJson, transformer);
		return executeHttpRequest;
	}

	/**
	 * Runs a request with a text body.
	 * @param <V> the result type
	 * @param trace identifier of the request in error messages
	 * @param method the HTTP method
	 * @param headers the request headers
	 * @param request the request body
	 * @param transformer turns the response into the result
	 * @return the result
	 */
	public <V>V executeHttpRequest(String trace,  CcpHttpMethods method, CcpJsonRepresentation headers, String request, CcpHttpResponseTransform<V> transformer) {
		
		CcpHttpResponse response = this.ccpHttp.executeHttpRequest(this.url, method, headers, request);
	
		V executeHttpRequest = this.executeHttpRequest(trace, method, headers, request, transformer, response);
		
		return executeHttpRequest;
	}

	/**
	 * Runs a multipart request with text and binary parts.
	 * @param <V> the result type
	 * @param trace identifier of the request in error messages
	 * @param method the HTTP method
	 * @param headers the request headers
	 * @param texts the text parts
	 * @param binaries the binary parts
	 * @param transformer turns the response into the result
	 * @return the result
	 */
	public <V>V executeMultiPartHttpRequest(String trace, CcpHttpMethods method, CcpJsonRepresentation headers, List<CcpHttpBodyText> texts, List<CcpHttpBodyBinary> binaries, CcpHttpResponseTransform<V> transformer) {
		
		CcpHttpResponse response = this.ccpHttp.executeMultiPartHttpRequest(url, method, headers, texts, binaries);
	
		V executeHttpRequest = this.executeHttpRequest(trace, method, headers, "", transformer, response);
		
		return executeHttpRequest;
	}
	
	/**
	 * Routes an already obtained response: an unmapped status without alternative flow raises the error built by
	 * {@link CcpHttpRequester#getHttpError}; otherwise the response is transformed and, when the body is a JSON object (or
	 * empty) and the result is a {@code CcpJsonRepresentation}, the flow of the status (or the alternative flow) runs over
	 * it and its output is returned.
	 * @param <V> the result type
	 * @param trace identifier of the request in error messages
	 * @param method the HTTP method used
	 * @param headers the request headers
	 * @param request the request body
	 * @param transformer turns the response into the result
	 * @param response the obtained response
	 * @return the result
	 * @throws CcpErrorHttp when the status has no flow
	 */
	@SuppressWarnings("unchecked")
	public <V> V executeHttpRequest(String trace, CcpHttpMethods method, CcpJsonRepresentation headers, String request, CcpHttpResponseTransform<V> transformer, CcpHttpResponse response) {
		
		int status = response.httpStatus;
		CcpFieldName ccpFieldName = new CcpFieldName(status);

		boolean statusIsMapped = this.flows.containsAllFields(ccpFieldName);
		boolean thereIsNoFlowForThisStatus = false == statusIsMapped && false == this.hasAlternativeFlow;

		if(thereIsNoFlowForThisStatus) {
			Set<String> fieldSet = this.flows.fieldSet(); 
			CcpErrorHttp httpError = this.ccpHttp.getHttpError(trace, this.url, method, headers, request, status, response.httpResponse, fieldSet);
			throw httpError;
		}

		CcpBusiness flow = this.flows.getOrDefault(ccpFieldName, () -> this.alternativeFlow);
		boolean validSingleJson = response.isValidSingleJson();

		boolean invalidSingleJson = false == validSingleJson;
		
		V tranform = transformer.transform(response);

		if(invalidSingleJson) {
			return tranform;
		}
		boolean isCcpJsonRepresentation = tranform instanceof CcpJsonRepresentation;
		boolean isNotJsonRepresentation = false == (isCcpJsonRepresentation);

		if(isNotJsonRepresentation) {
			return tranform;
		}
		CcpJsonRepresentation ccpJsonRepresentation = (CcpJsonRepresentation)tranform;

		CcpJsonRepresentation execute = flow.execute(ccpJsonRepresentation);
		V v = (V)execute;
		return v;
	}
	
	
}
