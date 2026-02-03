package com.ccp.json.validations.global.interfaces;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.global.engine.CcpJsonValidatorErrorBreakValidationsToTheClass;

public interface CcpJsonValidator {
	
	boolean hasError(CcpJsonRepresentation json, Class<?> clazz);
	
	Object getErrorMessage(CcpJsonRepresentation json, Class<?> clazz);
	
	boolean isCriticalValidation(CcpJsonRepresentation json, Class<?> clazz);

	Object getRuleExplanation(Class<?> clazz);
	
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Class<?> clazz) {

		boolean hasNoError = false == this.hasError(json, clazz);

		if (hasNoError) {
			return CcpOtherConstants.EMPTY_JSON;
		}

		String className = clazz.getName();

		Object error = this.getErrorMessage(json, clazz);

		CcpJsonRepresentation updatedErrors = errors.getDynamicVersion().addToList(className, error);

		boolean criticalValidation = this.isCriticalValidation(json, clazz);
		
		if(criticalValidation) {
			throw new CcpJsonValidatorErrorBreakValidationsToTheClass(updatedErrors);
		}
		
		return updatedErrors;
	}

	
}
