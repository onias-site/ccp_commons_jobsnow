package com.ccp.json.validations.global.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.global.annotations.CcpJsonValidatorFieldList;
import com.ccp.json.validations.global.annotations.CcpJsonValidatorGlobal;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;

public enum CcpJsonValidatorDefaults implements CcpJsonValidator{

	requiredAtLeastOne{

		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz) {
			
			CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
			CcpJsonValidatorFieldList[] validations = annotation.requiresAtLeastOne();
			
			for (CcpJsonValidatorFieldList validation : validations) {
				
				String[] oneOfThem = validation.value();
				
				boolean hasError = false == json.getDynamicVersion().containsAnyFields(oneOfThem);
				
				if(hasError) {
					return true;
				}
			}
			return false;
		}

		public List<String> getErrorMessage(CcpJsonRepresentation json, Class<?> clazz) {
			CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
			CcpJsonValidatorFieldList[] requiredAtLeastOne = annotation.requiresAtLeastOne();
			List<String> errors = new ArrayList<>();
			for (CcpJsonValidatorFieldList validation : requiredAtLeastOne) {
				
				String[] oneOfThem = validation.value();
				
				boolean hasNoError = json.getDynamicVersion().containsAnyFields(oneOfThem);
				
				if(hasNoError) {
					continue;
				}
				String error = "It is missing one of them fields in the current json: " + Arrays.asList(oneOfThem).toString();
				errors.add(error);
			}
			return errors;
		}

		public List<String> getRuleExplanation(Class<?> clazz) {
			CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
			CcpJsonValidatorFieldList[] requiredAtLeastOne = annotation.requiresAtLeastOne();
			List<String> rules = new ArrayList<>();
			for (CcpJsonValidatorFieldList validation : requiredAtLeastOne) {
				
				String[] oneOfThem = validation.value();
				String rule = "The provided json must has one of this following fields: " + Arrays.asList(oneOfThem).toString();
				rules.add(rule);
			}
			return rules;
		}

		public boolean isCriticalValidation(CcpJsonRepresentation json, Class<?> clazz) {
			return false;
		}
	},
	
	requiresAllOrNone{

		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz) {
	
			CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
			CcpJsonValidatorFieldList[] validations = annotation.requiresAllOrNone();
			
			for (CcpJsonValidatorFieldList validation : validations) {
				
				String[] array = validation.value();

				boolean containsNeitherOfThisFields = false == json.getDynamicVersion().containsAnyFields(array);
				
				if(containsNeitherOfThisFields) {
					continue;
				}
				
				boolean isMissingAnyField = false == json.getDynamicVersion().containsAllFields(array);
				
				if(isMissingAnyField) {
					return true;
				}
			}
			return false;
		}

		public List<String> getErrorMessage(CcpJsonRepresentation json, Class<?> clazz) {
			
			List<String> errors = new ArrayList<>();
			
			CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
			CcpJsonValidatorFieldList[] validations = annotation.requiresAllOrNone();
			
			for (CcpJsonValidatorFieldList validation : validations) {
				
				String[] array = validation.value();

				boolean containsNeitherOfThisFields = false == json.getDynamicVersion().containsAnyFields(array);
				
				if(containsNeitherOfThisFields) {
					continue;
				}
				
				boolean isMissingAnyField = false == json.getDynamicVersion().containsAllFields(array);
				
				if(isMissingAnyField) {
					Set<String> presentFields = json.getDynamicVersion().getJsonPiece(array).fieldSet();
					List<String> asList = Arrays.asList(array);
					List<String> missingFields = new ArrayList<String>(asList);
					missingFields.removeAll(presentFields);
					errors.add("This provided json contains the following fields: " + presentFields + ", but not contains the following fields: " + missingFields);
				}
			}
			return errors;
		}

		public boolean isCriticalValidation(CcpJsonRepresentation json, Class<?> clazz) {
			return false;
		}

		public Object getRuleExplanation(Class<?> clazz) {
			CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
			CcpJsonValidatorFieldList[] list = annotation.requiresAtLeastOne();
			List<String> rules = new ArrayList<>();
			for (CcpJsonValidatorFieldList validation : list) {
				
				String[] oneOfThem = validation.value();
				String rule = "The provided json must has all (or none) of this following fields: " + Arrays.asList(oneOfThem).toString() + ". If provide one of them, so must provide all of them";
				rules.add(rule);
			}
			return rules;
		}}
}
