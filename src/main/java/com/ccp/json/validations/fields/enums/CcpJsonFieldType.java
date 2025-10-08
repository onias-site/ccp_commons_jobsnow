package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpDynamicJsonRepresentation;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorRequired;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeBoolean;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberInteger;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberNatural;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeAfter;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;

public enum CcpJsonFieldType {
	Required{
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}

		public boolean hasErrors(CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {
			boolean hasError = CcpJsonFieldError.requiredFieldIsMissing.hasError(json, field, this);
			return hasError;
		}
		
		protected List<CcpJsonFieldValidatorInterface> getDefaultValidations() {
			return Arrays.asList(CcpJsonFieldError.requiredFieldIsMissing);
		}

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldValidatorRequired.class);
			return annotationPresent;
		}
	},
	
	Boolean{
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isBoolean();
		}

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeBoolean.class);
			return annotationPresent;
		}
	}, 
	Array(CcpJsonFieldTypeError.arrayMinSize, CcpJsonFieldTypeError.arrayExactSize, CcpJsonFieldTypeError.arrayMaxSize, CcpJsonFieldTypeError.arrayNonReapeted){
		
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isList();
		}

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			return annotationPresent;
		}
		
	},
	NestedJson(CcpJsonFieldTypeError.nestedJson, CcpJsonFieldTypeError.emptyJson){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isInnerJson();
		}

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNestedJson.class);
			return annotationPresent;
		}
	}, 
	Number(CcpJsonFieldTypeError.doubleNumberMaxValue, CcpJsonFieldTypeError.doubleNumberMinValue, CcpJsonFieldTypeError.doubleNumberExactValue, CcpJsonFieldTypeError.doubleNumberAllowed){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isDoubleNumber() ;
		}

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNumber.class);
			return annotationPresent;
		}
	}, 
	NumberInteger(CcpJsonFieldTypeError.longNumberMaxValue, CcpJsonFieldTypeError.longNumberMinValue, CcpJsonFieldTypeError.longNumberExactValue, CcpJsonFieldTypeError.longNumberAllowed){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber() ;
		}

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNumberInteger.class);
			return annotationPresent;
		}
	}, 

	NumberUnsigned(CcpJsonFieldTypeError.unsignedNumberMaxValue, CcpJsonFieldTypeError.unsignedNumberMinValue, CcpJsonFieldTypeError.unsignedNumberExactValue, CcpJsonFieldTypeError.unsignedNumberAllowed){
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

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNumberNatural.class);
			return annotationPresent;
		}

	}, 
	String(CcpJsonFieldTypeError.stringNotEmpty, CcpJsonFieldTypeError.stringExactLength, CcpJsonFieldTypeError.stringRegex, CcpJsonFieldTypeError.stringAllowedValues, CcpJsonFieldTypeError.stringMaxLength, CcpJsonFieldTypeError.stringMinLength){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}
		

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeString.class);
			return annotationPresent;
		}
	}, 
	TimeBeforeCurrentDate(CcpJsonFieldTypeError.timeMaxValueBeforeCurrentTime, CcpJsonFieldTypeError.timeExactValueBeforeCurrentTime, CcpJsonFieldTypeError.timeMinValueBeforeCurrentTime){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeTimeBefore.class);
			return annotationPresent;
		}
	},
	TimeAfterCurrentDate(CcpJsonFieldTypeError.timeMaxValueAfterCurrentTime, CcpJsonFieldTypeError.timeExactValueAfterCurrentTime, CcpJsonFieldTypeError.timeMinValueAfterCurrentTime){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}

		boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeTimeAfter.class);
			return annotationPresent;
		}
	},
	
	;
	private final CcpJsonFieldValidatorInterface[] errorTypes;
	
	private CcpJsonFieldType(CcpJsonFieldValidatorInterface... errorTypes) {
		this.errorTypes = errorTypes;
	}
	
	abstract Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName);

	
	public boolean hasErrors(CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {
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
	
	public CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {

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

	public CcpJsonRepresentation updateRuleExplanation(CcpJsonRepresentation ruleExplanation, Field field) {
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
		List<CcpJsonFieldValidatorInterface> errorTypes = Arrays.asList(this.errorTypes);
		validations.addAll(errorTypes);
		
		return validations;
	}

	protected List<CcpJsonFieldValidatorInterface> getDefaultValidations(){
		List<CcpJsonFieldValidatorInterface> asList = new ArrayList<>(Arrays.asList(CcpJsonFieldError.incompatibleType, CcpJsonFieldError.validateCollectionOrSigleValue));
		return asList;
	}

	public CcpJsonFieldValidatorInterface[] getErrorTypes() {
		return errorTypes;
	}
	
	abstract boolean hasRuleExplanation(Field field);
}
