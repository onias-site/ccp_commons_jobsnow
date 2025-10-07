package com.ccp.especifications.db.utils;

public interface CcpDbUtilJsonValidation {

	default Class<?> getJsonValidationClass(CcpEntity entity){
		return NoOp.class;
	}
}
class NoOp{
	
}