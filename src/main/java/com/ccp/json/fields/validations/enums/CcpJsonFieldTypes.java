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
import com.ccp.json.fields.validations.annotations.CcpJsonFieldNumberType;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldTextType;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldTimeType;

public enum CcpJsonFieldTypes {
	Bool(CcpJsonField.class){
		Predicate<CcpJsonRepresentation> getTypes(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isBoolean();
		}
	}, 
	Array(CcpJsonFieldArrayType.class, CcpJsonFieldErrorTypes.objectArrayMinSize, CcpJsonFieldErrorTypes.objectArrayMaxSize, CcpJsonFieldErrorTypes.objectArrayNonReapeted){
		Predicate<CcpJsonRepresentation> getTypes(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isList();
		}
	},
	Json(CcpJsonField.class){
		Predicate<CcpJsonRepresentation> getTypes(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isInnerJson();
		}
	}, 
	NumberType(CcpJsonFieldNumberType.class, CcpJsonFieldErrorTypes.objectNumberMaxValue, CcpJsonFieldErrorTypes.objectNumberMinValue, CcpJsonFieldErrorTypes.objectNumberAllowed){
		Predicate<CcpJsonRepresentation> getTypes(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isDoubleNumber() ;
		}
	}, 
	Text(CcpJsonFieldTextType.class, CcpJsonFieldErrorTypes.objectTextRegex, CcpJsonFieldErrorTypes.objectTextAllowedValues, CcpJsonFieldErrorTypes.objectTextMaxLength, CcpJsonFieldErrorTypes.objectTextMinLength){
		Predicate<CcpJsonRepresentation> getTypes(String fieldName) {
			return json -> true;
		}
		
	}, 
	Time(CcpJsonFieldTimeType.class, CcpJsonFieldErrorTypes.objectArrayMinSize, CcpJsonFieldErrorTypes.objectArrayMaxSize, CcpJsonFieldErrorTypes.objectArrayNonReapeted){
		Predicate<CcpJsonRepresentation> getTypes(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}
	}
	;
	private final Class<? extends Annotation> annotation;
	private final CcpJsonFieldErrorTypes[] errorTypes;
	
	private CcpJsonFieldTypes(Class<? extends Annotation> annotation, CcpJsonFieldErrorTypes... errorTypes) {
		this.annotation = annotation;
		this.errorTypes = errorTypes;
	}
	
	abstract Predicate<CcpJsonRepresentation> getTypes(String fieldName);

	public CcpJsonRepresentation validate(CcpJsonRepresentation errors, CcpJsonRepresentation json, Field field, Class<?> clazz, CcpJsonFieldValueExtractor fieldValueExtractor) {
		String fieldName = field.getName();
		List<CcpJsonFieldErrorTypes> validations = this.getDefaultValidations();
		List<CcpJsonFieldErrorTypes> asList = Arrays.asList(this.errorTypes);
		validations.addAll(asList);
		
		Predicate<CcpJsonRepresentation> types = this.getTypes(fieldName);
		
		for (CcpJsonFieldErrorTypes errorType : validations) {
			errors = errorType.evaluate(errors, json, clazz, field, this.annotation, types, fieldValueExtractor);
		}
		return errors;
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
