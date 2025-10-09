package com.ccp.json.validations.fields.interfaces;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.enums.CcpJsonFieldError;
import com.ccp.json.validations.fields.enums.CcpJsonFieldsValidationContext;

public interface CcpJsonFieldType {
	abstract Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName);

	
	default boolean hasErrors(CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {
		java.lang.String fieldName = field.getName();
		
		boolean thisFieldIsAbsent = false == json.getDynamicVersion().containsAllFields(fieldName);
		
		if(thisFieldIsAbsent) {
			return false;
		}
		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		for (CcpJsonFieldValidatorInterface validation : validations) {
			
			boolean isNotValidValidationContext = false == validation.isValidValidationContext(context);
			
			if(isNotValidValidationContext) {
				continue;
			}
			
			boolean hasNoRules = false == validation.hasRuleExplanation(field, this);
			
			if(hasNoRules) {
				continue;
			}
			
			boolean hasError = validation.hasError(json, field, this);
			if(hasError) {
				return true;
			}
		}
		return false;
	}
	
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {

		java.lang.String fieldName = field.getName();
		
		boolean fieldIsNotPresent = false == json.getDynamicVersion().containsAllFields(fieldName);
		if(fieldIsNotPresent) {
			return errors;
		}

		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		
		for (CcpJsonFieldValidatorInterface errorType : validations) {
			boolean isInvalidContextValidation = false == errorType.isValidValidationContext(context);
			
			if(isInvalidContextValidation) {
				continue;
			}
			
			boolean hasNoRules = false == this.hasRuleExplanation(field);
			if(hasNoRules) {
				continue;
			}
			
			boolean hasNoErrors = false == errorType.hasError(json, field, this);
			if(hasNoErrors) {
				continue;
			}
			errors = errorType.getErrors(errors, json, field, this);
		}
		
		return errors;
	}

	default CcpJsonRepresentation updateRuleExplanation(CcpJsonRepresentation ruleExplanation, Field field) {
		boolean hasNoRulesExplanations = false == this.hasRuleExplanation(field);
		if(hasNoRulesExplanations) {
			return ruleExplanation;
		}
		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		
		for (CcpJsonFieldValidatorInterface errorType : validations) {
			ruleExplanation = errorType.updateRuleExplanation(ruleExplanation, field, this);
		}
		return ruleExplanation;
	}

	private List<CcpJsonFieldValidatorInterface> getAllValidations(Field field) {
		
		List<CcpJsonFieldValidatorInterface> validations = this.getDefaultValidations();
		List<CcpJsonFieldValidatorInterface> errorTypes = Arrays.asList(this.getErrorTypes());
		validations.addAll(errorTypes);
		
		return validations;
	}

	default List<CcpJsonFieldValidatorInterface> getDefaultValidations(){
		List<CcpJsonFieldValidatorInterface> asList = new ArrayList<>(Arrays.asList(CcpJsonFieldError.incompatibleType, CcpJsonFieldError.validateCollectionOrSigleValue));
		return asList;
	}
	CcpJsonFieldValidatorInterface[] getErrorTypes();
	
	abstract boolean hasRuleExplanation(Field field);


	String name();

}
