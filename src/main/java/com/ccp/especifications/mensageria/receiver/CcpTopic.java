package com.ccp.especifications.mensageria.receiver;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpTopic extends Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	default boolean canSave() {
		return true;
	}
	
	default Class<?> getJsonValidationClass(){
		Class<? extends CcpTopic> class1 = this.getClass();
		return class1;
	}
	
}
