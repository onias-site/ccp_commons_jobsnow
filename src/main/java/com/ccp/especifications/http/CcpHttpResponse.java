package com.ccp.especifications.http;

import java.util.List;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;

public class CcpHttpResponse {

	public final String httpResponse;
	public final int httpStatus;
	
	
	public CcpHttpResponse(String httpResponse, int httpStatus) {
		this.httpResponse = httpResponse;
		this.httpStatus = httpStatus;
	}
	
	public boolean isValidSingleJson() {
		if(this.httpResponse.trim().isEmpty()) {
			return true;
		}
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(this.httpResponse);
		CcpTextDecorator text = ccpStringDecorator.text();
		boolean validSingleJson = text.isValidSingleJson();
		return validSingleJson;
	}
	
	public CcpJsonRepresentation asSingleJson() {
		try {
			return new CcpStringDecorator(this.httpResponse).map();
		} catch (Exception e) {
			return CcpOtherConstants.EMPTY_JSON;
		}
	}
	
	public List<CcpJsonRepresentation> asListRecord(){
		try {
			CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			List<CcpJsonRepresentation> fromJson = json.fromJson(this.httpResponse);
			return fromJson; 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Object> asListObject(){
		try {
			CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			List<Object> fromJson = json.fromJson(this.httpResponse);
			return fromJson; 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String asBase64() {
		byte[] bytes = this.httpResponse.getBytes();
		String encodeToString = new CcpStringDecorator(bytes).text().asBase64().content;
		return encodeToString;
	}
	
	
	public String toString() {
		return CcpOtherConstants.EMPTY_JSON
				.put("httpStatus", this.httpStatus)
				.put("httpResponse", this.httpResponse)
				.toString();
	}
	

	private boolean isInRange(int range) {
		if(this.httpStatus < range) {
			return false;
		}
		if(this.httpStatus > (range + 99)) {
			return false;
		}
		return true;
		
	}
	
	public boolean isClientError() {
		return this.isInRange(400);
	}
	
	public boolean isServerError() {
		return this.isInRange(500);
	}

	public boolean isSuccess() {
		return this.isInRange(200);
	}
}
