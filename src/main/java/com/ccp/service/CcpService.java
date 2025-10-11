package com.ccp.service;

import java.util.Map;

import com.ccp.decorators.CcpErrorJsonInvalid;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.mensageria.receiver.CcpBusiness;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

public interface CcpService extends CcpBusiness {

	Class<?> getJsonValidationClass();
	
	String name();
	
	default Map<String, Object> execute(Map<String, Object> map){
		CcpJsonRepresentation json = new CcpJsonRepresentation(map);
		Class<?> jsonValidationClass = this.getJsonValidationClass();
		String name = this.name();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, name);
		try {
			CcpJsonRepresentation apply = this.apply(json);
			return apply.content;
		} catch (CcpErrorJsonInvalid e) {
			throw new CcpServiceJsonValidationError(e);
		}
	}
}
