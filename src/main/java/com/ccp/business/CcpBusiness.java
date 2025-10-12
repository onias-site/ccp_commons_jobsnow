package com.ccp.business;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpBusiness extends Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	default boolean canBeSavedAsAsyncTask() {
		return true;
	}
	
	default Class<?> getJsonValidationClass(){
		Class<? extends CcpBusiness> class1 = this.getClass();
		return class1;
	}
	
}
