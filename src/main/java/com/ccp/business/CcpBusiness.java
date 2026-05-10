package com.ccp.business;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

public interface CcpBusiness extends Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	default boolean canBeSavedAsAsyncTask() {
		return true;
	}
	
	default Class<?> getJsonValidationClass(){
		Class<? extends CcpBusiness> class1 = this.getClass();
		return class1;
	}
	
	default CcpJsonRepresentation execute(CcpJsonRepresentation json) {
		String className = this.getClass().getName();
		Class<?> jsonValidationClass = this.getJsonValidationClass();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, className);

		CcpJsonRepresentation apply = this.apply(json);
		return apply;
	}
	
}
