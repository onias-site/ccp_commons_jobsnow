package com.ccp.json.fields.validations.enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.fields.validations.annotations.CcpJsonField;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldArrayType;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldNested;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldNumberType;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldTextType;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldTimeType;

public enum CcpJsonFieldTypes {
	Bool(CcpJsonField.class){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isBoolean();
		}
	}, 
	Array(CcpJsonFieldArrayType.class, CcpJsonFieldErrorTypes.arrayMinSize, CcpJsonFieldErrorTypes.arrayMaxSize, CcpJsonFieldErrorTypes.arrayNonReapeted){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isList();
		}
	},
	Json(CcpJsonFieldNested.class){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isInnerJson();
		}
	}, 
	NumberType(CcpJsonFieldNumberType.class, CcpJsonFieldErrorTypes.numberMaxValue, CcpJsonFieldErrorTypes.numberMinValue, CcpJsonFieldErrorTypes.numberAllowed){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isDoubleNumber() ;
		}
	}, 
	Text(CcpJsonFieldTextType.class, CcpJsonFieldErrorTypes.textRegex, CcpJsonFieldErrorTypes.textAllowedValues, CcpJsonFieldErrorTypes.textMaxLength, CcpJsonFieldErrorTypes.textMinLength){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}
		
	}, 
	Time(CcpJsonFieldTimeType.class, CcpJsonFieldErrorTypes.arrayMinSize, CcpJsonFieldErrorTypes.arrayMaxSize, CcpJsonFieldErrorTypes.arrayNonReapeted){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}
	}
	;
	public final Class<? extends Annotation> annotation;
	private final CcpJsonFieldErrorTypes[] errorTypes;
	
	private CcpJsonFieldTypes(Class<? extends Annotation> annotation, CcpJsonFieldErrorTypes... errorTypes) {
		this.annotation = annotation;
		this.errorTypes = errorTypes;
	}
	
	abstract Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName);

	public boolean hasErrors(CcpJsonRepresentation json, Field field) {
		List<CcpJsonFieldErrorTypes> validations = this.getValidations();
		for (CcpJsonFieldErrorTypes validation : validations) {
			boolean hasError = validation.hasError(json, field, this);
			if(hasError) {
				return true;
			}
		}
		return false;
	}
	
	public CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Field field) {
		List<CcpJsonFieldErrorTypes> validations = this.getValidations();
		
		for (CcpJsonFieldErrorTypes errorType : validations) {
			errors = errorType.getErrors(errors, json,field, this);
		}
		return errors;
	}

	private List<CcpJsonFieldErrorTypes> getValidations() {
		List<CcpJsonFieldErrorTypes> validations = this.getDefaultValidations();
		List<CcpJsonFieldErrorTypes> asList = Arrays.asList(this.errorTypes);
		validations.addAll(asList);
		return validations;
	}
	
	private List<CcpJsonFieldErrorTypes> getDefaultValidations(){
		List<CcpJsonFieldErrorTypes> asList = Arrays.asList(
				CcpJsonFieldErrorTypes.annotationIsMissing,  
				CcpJsonFieldErrorTypes.incompatibleType,  
				CcpJsonFieldErrorTypes.requiredFieldIsMissing
				);
		return new ArrayList<CcpJsonFieldErrorTypes>(asList);
	}
}
