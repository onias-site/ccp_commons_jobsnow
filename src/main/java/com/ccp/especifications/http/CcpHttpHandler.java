package com.ccp.especifications.http;

import java.util.List;
import java.util.Set;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.business.CcpBusiness;


public final class CcpHttpHandler {

	private final String url;
	private final CcpJsonRepresentation flows;
	private final CcpBusiness alternativeFlow;
	public final CcpHttpRequester ccpHttp = CcpDependencyInjection.getDependency(CcpHttpRequester.class);

	public CcpHttpHandler(CcpJsonRepresentation flows, String url) {
		this.alternativeFlow = null;
		this.flows = flows;
		this.url = url;
		
	}

	public CcpHttpHandler(Integer httpStatus, CcpBusiness alternativeFlow, String url) {
		this.flows = CcpOtherConstants.EMPTY_JSON.addJsonTransformer(httpStatus, CcpOtherConstants.DO_NOTHING);
		this.alternativeFlow = alternativeFlow;
		this.url = url;
	}
	
	public CcpHttpHandler(Integer httpStatus, String url) {
		this.flows = CcpOtherConstants.EMPTY_JSON.addJsonTransformer(httpStatus, CcpOtherConstants.DO_NOTHING);
		this.alternativeFlow = null;
		this.url = url;
	}
	
	
	public <V> V executeHttpSimplifiedGet(String trace, CcpHttpResponseTransform<V> transformer) {
		V executeHttpRequest = this.executeHttpRequest(trace, CcpHttpMethods.GET, CcpOtherConstants.EMPTY_JSON, CcpOtherConstants.EMPTY_JSON, transformer);
		return executeHttpRequest;
	}
	
	public <V> V executeHttpRequest(String trace, CcpHttpMethods method, CcpJsonRepresentation headers, CcpJsonRepresentation body, CcpHttpResponseTransform<V> transformer) {
		
		String asJson = body.asUgglyJson();
		V executeHttpRequest = this.executeHttpRequest(trace, method, headers, asJson, transformer);
		return executeHttpRequest;
	}

	public <V>V executeHttpRequest(String trace,  CcpHttpMethods method, CcpJsonRepresentation headers, String request, CcpHttpResponseTransform<V> transformer) {
		
		CcpHttpResponse response = this.ccpHttp.executeHttpRequest(this.url, method, headers, request);
	
		V executeHttpRequest = this.executeHttpRequest(trace, method, headers, request, transformer, response);
		
		return executeHttpRequest;
	}

	public <V>V executeMultiPartHttpRequest(String trace, CcpHttpMethods method, CcpJsonRepresentation headers, List<CcpHttpBodyText> texts, List<CcpHttpBodyBinary> binaries, CcpHttpResponseTransform<V> transformer) {
		
		CcpHttpResponse response = this.ccpHttp.executeMultiPartHttpRequest(url, method, headers, texts, binaries);
	
		V executeHttpRequest = this.executeHttpRequest(trace, method, headers, "", transformer, response);
		
		return executeHttpRequest;
	}
	
	@SuppressWarnings("unchecked")
	public <V> V executeHttpRequest(String trace, CcpHttpMethods method, CcpJsonRepresentation headers, String request, CcpHttpResponseTransform<V> transformer, CcpHttpResponse response) {
		
		int status = response.httpStatus;
		
		CcpBusiness flow = this.flows.getOrDefault(() -> "" + status, () -> this.alternativeFlow);
	
		if(flow == null) {
			Set<String> fieldSet = this.flows.fieldSet(); 
			CcpErrorHttp httpError = this.ccpHttp.getHttpError(trace, this.url, method, headers, request, status, response.httpResponse, fieldSet);
			throw httpError;
		}
	
		boolean invalidSingleJson = false == response.isValidSingleJson();
		
		V tranform = transformer.transform(response);

		if(invalidSingleJson) {
			return tranform;
		}
		
		if(false == (tranform instanceof CcpJsonRepresentation)) {
			return tranform;
		}

		CcpJsonRepresentation execute = flow.apply((CcpJsonRepresentation)tranform);
		return (V)execute;
	}
	
	
}
