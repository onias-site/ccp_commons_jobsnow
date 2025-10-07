package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpDynamicJsonRepresentation;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;

public enum CcpJsonFieldType {
	Required{
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}
		protected List<CcpJsonFieldValidatorInterface> getDefaultValidations() {
			List<CcpJsonFieldValidatorInterface> asList = Arrays.asList(CcpJsonFieldError.requiredFieldIsMissing);
			return asList;
		}
	},
	
	Boolean{
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isBoolean();
		}
	}, 
	Array(CcpJsonFieldTypeError.arrayMinSize, CcpJsonFieldTypeError.arrayExactSize, CcpJsonFieldTypeError.arrayMaxSize, CcpJsonFieldTypeError.arrayNonReapeted){
		
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isList();
		}
	},
	NestedJson(CcpJsonFieldTypeError.nestedJson, CcpJsonFieldTypeError.emptyJson){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isInnerJson();
		}
	}, 
	Number(CcpJsonFieldTypeError.doubleNumberMaxValue, CcpJsonFieldTypeError.doubleNumberMinValue, CcpJsonFieldTypeError.doubleNumberExactValue, CcpJsonFieldTypeError.doubleNumberAllowed){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isDoubleNumber() ;
		}
	}, 
	NumberInteger(CcpJsonFieldTypeError.longNumberMaxValue, CcpJsonFieldTypeError.longNumberMinValue, CcpJsonFieldTypeError.longNumberExactValue, CcpJsonFieldTypeError.longNumberAllowed){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber() ;
		}
	}, 

	NumberNatural(CcpJsonFieldTypeError.naturalNumberMaxValue, CcpJsonFieldTypeError.naturalNumberMinValue, CcpJsonFieldTypeError.naturalNumberExactValue, CcpJsonFieldTypeError.naturalNumberAllowed){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> {
				CcpDynamicJsonRepresentation dynamicVersion = json.getDynamicVersion();
				boolean isNotLongNumber = false == dynamicVersion.getAsStringDecorator(fieldName).isLongNumber();
				
				if(isNotLongNumber) {
					return false;
				}
				
				Long asLongNumber = dynamicVersion.getAsLongNumber(fieldName);
				return asLongNumber >= 0;
			} ;
		}
	}, 
	String(CcpJsonFieldTypeError.stringNotEmpty, CcpJsonFieldTypeError.stringExactLength, CcpJsonFieldTypeError.stringRegex, CcpJsonFieldTypeError.stringAllowedValues, CcpJsonFieldTypeError.stringMaxLength, CcpJsonFieldTypeError.stringMinLength){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}
		
	}, 
	TimeBeforeCurrentDate(CcpJsonFieldTypeError.timeMaxValueBeforeCurrentTime, CcpJsonFieldTypeError.timeExactValueBeforeCurrentTime, CcpJsonFieldTypeError.timeMinValueBeforeCurrentTime){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}
	},
	TimeAfterCurrentDate(CcpJsonFieldTypeError.timeMaxValueAfterCurrentTime, CcpJsonFieldTypeError.timeExactValueAfterCurrentTime, CcpJsonFieldTypeError.timeMinValueAfterCurrentTime){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}
	}
	;
	private final CcpJsonFieldTypeError[] errorTypes;
	
	private CcpJsonFieldType(CcpJsonFieldTypeError... errorTypes) {
		this.errorTypes = errorTypes;
	}
	
	abstract Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName);

	public boolean hasErrors(CcpJsonRepresentation json, Field field) {
		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		for (CcpJsonFieldValidatorInterface validation : validations) {
			boolean hasError = validation.hasError(json, field, this);
			if(hasError) {
				return true;
			}
		}
		return false;
	}
	
	public CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Field field) {
		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		
		for (CcpJsonFieldValidatorInterface errorType : validations) {
			errors = errorType.getErrors(errors, json, field, this);
		}
		
		return errors;
	}

	public CcpJsonRepresentation updateRuleExplanation(CcpJsonRepresentation ruleExplanation, Field field) {
		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		
		for (CcpJsonFieldValidatorInterface errorType : validations) {
			ruleExplanation = errorType.updateRuleExplanation(ruleExplanation, field, this);
		}
		
		return ruleExplanation;
	}

	private List<CcpJsonFieldValidatorInterface> getAllValidations(Field field) {
		
		List<CcpJsonFieldValidatorInterface> validations = this.getDefaultValidations();
		List<CcpJsonFieldTypeError> errorTypes = Arrays.asList(this.errorTypes);
		validations.addAll(errorTypes);
		
		return validations;
	}

	protected List<CcpJsonFieldValidatorInterface> getDefaultValidations(){
		List<CcpJsonFieldValidatorInterface> asList = Arrays.asList(CcpJsonFieldError.incompatibleType, CcpJsonFieldError.collectionOrNotCollection);
		return asList;
	}

	public CcpJsonFieldTypeError[] getErrorTypes() {
		return errorTypes;
	}
}
