package com.ccp.json.validations.global.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.ccp.json.validations.global.annotations.CcpJsonValidationFieldList;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.global.annotations.CcpJsonGlobalValidations;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;

/**
 * Validações globais padrão do framework. {@code requiredAtLeastOne} garante que ao menos um campo
 * de cada grupo esteja presente; {@code requiresAllOrNone} garante consistência total (ou nenhum
 * ou todos os campos do grupo devem estar presentes).
 */
public enum CcpJsonValidatorDefaults implements CcpJsonValidator{

	requiredAtLeastOne{

		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz) {
			
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] validations = annotation.requiresAtLeastOne();
			
			for (CcpJsonValidationFieldList validation : validations) {
				
				String[] oneOfThem = validation.value();
				boolean containsAnyFields = json.containsAnyFields(Arrays.asList(oneOfThem));

				boolean hasError = false == containsAnyFields;
				
				if(hasError) {
					return true;
				}
			}
			return false;
		}

		public List<String> getErrorMessage(CcpJsonRepresentation json, Class<?> clazz) {
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] requiredAtLeastOne = annotation.requiresAtLeastOne();
			List<String> errors = new ArrayList<>();
			for (CcpJsonValidationFieldList validation : requiredAtLeastOne) {
				
				String[] oneOfThem = validation.value();
				
				boolean hasNoError = json.containsAnyFields(Arrays.asList(oneOfThem));
				
				if(hasNoError) {
					continue;
				}
				String toString = Arrays.asList(oneOfThem).toString();
				String error = "It is missing one of them fields in the current json: " + toString;
				errors.add(error);
			}
			return errors;
		}

		public List<String> getRuleExplanation(Class<?> clazz) {
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] requiredAtLeastOne = annotation.requiresAtLeastOne();
			List<String> rules = new ArrayList<>();
			for (CcpJsonValidationFieldList validation : requiredAtLeastOne) {
				
				String[] oneOfThem = validation.value();
				String toString2 = Arrays.asList(oneOfThem).toString();
				String rule = "The provided json must has one of this following fields: " + toString2;
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
	
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] validations = annotation.requiresAllOrNone();
			
			for (CcpJsonValidationFieldList validation : validations) {
				
				String[] array = validation.value();
				boolean containsAnyFields2 = json.containsAnyFields(Arrays.asList(array));

				boolean containsNeitherOfThisFields = false == containsAnyFields2;
				
				if(containsNeitherOfThisFields) {
					continue;
				}
				boolean containsAllFields = json.containsAllFields(Arrays.asList(array));

				boolean isMissingAnyField = false == containsAllFields;
				
				if(isMissingAnyField) {
					return true;
				}
			}
			return false;
		}

		public List<String> getErrorMessage(CcpJsonRepresentation json, Class<?> clazz) {
			
			List<String> errors = new ArrayList<>();
			
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] validations = annotation.requiresAllOrNone();
			
			for (CcpJsonValidationFieldList validation : validations) {
				
				String[] array = validation.value();
				boolean containsAnyFields3 = json.containsAnyFields(Arrays.asList(array));

				boolean containsNeitherOfThisFields = false == containsAnyFields3;
				
				if(containsNeitherOfThisFields) {
					continue;
				}
				boolean containsAllFields2 = json.containsAllFields(Arrays.asList(array));

				boolean isMissingAnyField = false == containsAllFields2;
				
				if(isMissingAnyField) {
					CcpJsonRepresentation jsonPiece = json.getJsonPiece(Arrays.asList(array));
					Set<String> presentFields = jsonPiece.fieldSet();
					List<String> asList = Arrays.asList(array);
					List<String> missingFields = new ArrayList<String>(asList);
					missingFields.removeAll(presentFields);
					String valorMais = "This provided json contains the following fields: " + presentFields;
					String valorMaisMais = valorMais + ", but not contains the following fields: ";
					String valorMaisMaisMais = valorMaisMais + missingFields;
					errors.add(valorMaisMaisMais);
				}
			}
			return errors;
		}

		public boolean isCriticalValidation(CcpJsonRepresentation json, Class<?> clazz) {
			return false;
		}

		public Object getRuleExplanation(Class<?> clazz) {
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] list = annotation.requiresAtLeastOne();
			List<String> rules = new ArrayList<>();
			for (CcpJsonValidationFieldList validation : list) {
				
				String[] oneOfThem = validation.value();
				String toString3 = Arrays.asList(oneOfThem).toString();
				String valorMais2 = "The provided json must has all (or none) of this following fields: " + toString3;
				String rule = valorMais2 + ". If provide one of them, so must provide all of them";
				rules.add(rule);
			}
			return rules;
		}}
}
