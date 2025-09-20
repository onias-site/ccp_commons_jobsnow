package com.ccp.json.validations.global.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.global.annotations.CcpJsonValidatorGlobal;
import com.ccp.json.validations.global.annotations.CcpJsonValidatorRequiredAtLeastOne;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;

public enum CcpJsonValidatorDefaults implements CcpJsonValidator{

	requiredAtLeastOne{

		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz) {
			
			CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
			CcpJsonValidatorRequiredAtLeastOne[] requiredAtLeastOne = annotation.requiredAtLeastOne();
			
			for (CcpJsonValidatorRequiredAtLeastOne validation : requiredAtLeastOne) {
				
				String[] oneOfThem = validation.oneOfThem();
				
				boolean hasError = false == json.getDynamicVersion().containsAnyFields(oneOfThem);
				
				if(hasError) {
					return true;
				}
			}
			return false;
		}

		public List<String> getErrorMessage(CcpJsonRepresentation json, Class<?> clazz) {
			CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
			CcpJsonValidatorRequiredAtLeastOne[] requiredAtLeastOne = annotation.requiredAtLeastOne();
			List<String> errors = new ArrayList<>();
			for (CcpJsonValidatorRequiredAtLeastOne validation : requiredAtLeastOne) {
				
				String[] oneOfThem = validation.oneOfThem();
				
				boolean hasNoError = json.getDynamicVersion().containsAnyFields(oneOfThem);
				
				if(hasNoError) {
					continue;
				}
				String error = "It is missing one of them fields in the current json: " + Arrays.asList(oneOfThem).toString();
				errors.add(error);
			}
			return errors;
		}

		public CcpJsonRepresentation getRule(CcpJsonRepresentation json, Class<?> clazz) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isCriticalValidation(CcpJsonRepresentation json, Class<?> clazz) {
			return false;
		}
		
	},
	
	
	
}
