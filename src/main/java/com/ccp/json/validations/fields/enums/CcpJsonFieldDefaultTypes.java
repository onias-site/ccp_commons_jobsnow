package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpDynamicJsonRepresentation;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntityFieldPrimaryKey;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorRequired;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeBoolean;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberInteger;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberUnsigned;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeAfter;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;

public enum CcpJsonFieldDefaultTypes implements CcpJsonFieldType {
	Required{
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}

		public boolean hasErrors(CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {
			boolean hasError = CcpJsonFieldError.requiredFieldIsMissing.hasError(json, field, this);
			return hasError;
		}
		
		public  List<CcpJsonFieldValidatorInterface> getDefaultValidations() {
			return Arrays.asList(CcpJsonFieldError.requiredFieldIsMissing);
		}

		public boolean hasRuleExplanation(Field field) {
			
			boolean annotationRequiredIsPresent = field.isAnnotationPresent(CcpJsonFieldValidatorRequired.class);
			
			if(annotationRequiredIsPresent) {
				return true;
			}
			boolean hasAnnotationPrimaryKey = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);
			
			if(hasAnnotationPrimaryKey) {
				return true;
			}

			
			return false;
		}

	},
	
	Boolean{
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isBoolean();
		}

		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeBoolean.class);
			return annotationPresent;
		}
	}, 
	Array(CcpJsonFieldTypeError.arrayMinSize, CcpJsonFieldTypeError.arrayExactSize, CcpJsonFieldTypeError.arrayMaxSize, CcpJsonFieldTypeError.arrayNonReapeted){
		
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isList();
		}

		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			return annotationPresent;
		}
		
	},
	NestedJson(CcpJsonFieldTypeError.nestedJson, CcpJsonFieldTypeError.emptyJson){
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isInnerJson();
		}

		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNestedJson.class);
			return annotationPresent;
		}
	}, 
	Number(CcpJsonFieldTypeError.doubleNumberMaxValue, CcpJsonFieldTypeError.doubleNumberMinValue, CcpJsonFieldTypeError.doubleNumberExactValue, CcpJsonFieldTypeError.doubleNumberAllowed){
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isDoubleNumber() ;
		}

		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNumber.class);
			return annotationPresent;
		}
	}, 
	NumberInteger(CcpJsonFieldTypeError.longNumberMaxValue, CcpJsonFieldTypeError.longNumberMinValue, CcpJsonFieldTypeError.longNumberExactValue, CcpJsonFieldTypeError.longNumberAllowed){
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber() ;
		}

		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNumberInteger.class);
			return annotationPresent;
		}
	}, 

	NumberUnsigned(CcpJsonFieldTypeError.unsignedNumberMaxValue, CcpJsonFieldTypeError.unsignedNumberMinValue, CcpJsonFieldTypeError.unsignedNumberExactValue, CcpJsonFieldTypeError.unsignedNumberAllowed){
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
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

		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNumberUnsigned.class);
			return annotationPresent;
		}

	}, 
	String(CcpJsonFieldTypeError.stringNotEmpty, CcpJsonFieldTypeError.stringExactLength, CcpJsonFieldTypeError.stringRegex, CcpJsonFieldTypeError.stringAllowedValues, CcpJsonFieldTypeError.stringMaxLength, CcpJsonFieldTypeError.stringMinLength){
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}
		

		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeString.class);
			return annotationPresent;
		}
	}, 
	TimeBeforeCurrentDate(CcpJsonFieldTypeError.timeMaxValueBeforeCurrentTime, CcpJsonFieldTypeError.timeExactValueBeforeCurrentTime, CcpJsonFieldTypeError.timeMinValueBeforeCurrentTime){
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}

		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeTimeBefore.class);
			return annotationPresent;
		}
	},
	TimeAfterCurrentDate(CcpJsonFieldTypeError.timeMaxValueAfterCurrentTime, CcpJsonFieldTypeError.timeExactValueAfterCurrentTime, CcpJsonFieldTypeError.timeMinValueAfterCurrentTime){
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}

		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeTimeAfter.class);
			return annotationPresent;
		}
	},
	
	;
	private final CcpJsonFieldValidatorInterface[] errorTypes;
	
	private CcpJsonFieldDefaultTypes(CcpJsonFieldValidatorInterface... errorTypes) {
		this.errorTypes = errorTypes;
	}
	public CcpJsonFieldValidatorInterface[] getErrorTypes() {
		return errorTypes;
	}

}
