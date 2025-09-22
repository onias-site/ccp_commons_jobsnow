package com.ccp.json.validations.fields.enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidator;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTime;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;

public enum CcpJsonFieldType {
	Boolean(CcpJsonFieldValidator.class){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isBoolean();
		}
	}, 
	Array(CcpJsonFieldValidator.class, CcpJsonFieldTypeError.arrayMinSize, CcpJsonFieldTypeError.arrayMaxSize, CcpJsonFieldTypeError.arrayNonReapeted){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isList();
		}
	},
	NestedJson(CcpJsonFieldTypeNestedJson.class, CcpJsonFieldTypeError.nestedJson){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isInnerJson();
		}
	}, 
	Number(CcpJsonFieldTypeNumber.class, CcpJsonFieldTypeError.numberMaxValue, CcpJsonFieldTypeError.numberMinValue, CcpJsonFieldTypeError.numberAllowed, CcpJsonFieldTypeError.numberInteger){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isDoubleNumber() ;
		}
	}, 
	String(CcpJsonFieldTypeString.class, CcpJsonFieldTypeError.textRegex, CcpJsonFieldTypeError.textAllowedValues, CcpJsonFieldTypeError.textMaxLength, CcpJsonFieldTypeError.textMinLength){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}
		
	}, 
	TimeBeforeCurrentDate(CcpJsonFieldTypeTime.class, CcpJsonFieldTypeError.timeMaxValueBeforeCurrentTime, CcpJsonFieldTypeError.timeMinValueBeforeCurrentTime){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}
	},
	TimeAfterCurrentDate(CcpJsonFieldTypeTime.class, CcpJsonFieldTypeError.timeMaxValueAfterCurrentTime, CcpJsonFieldTypeError.timeMinValueAfterCurrentTime){
		Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		}
	}

	;
	public final Class<? extends Annotation> requiredAnnotation;
	private final CcpJsonFieldTypeError[] errorTypes;
	
	private CcpJsonFieldType(Class<? extends Annotation> requiredAnnotation, CcpJsonFieldTypeError... errorTypes) {
		this.requiredAnnotation = requiredAnnotation;
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
		List<CcpJsonFieldValidatorInterface> customValidations = this.getCustomValidations(field);
		validations.addAll(customValidations);
		return validations;
	}
	
	private List<CcpJsonFieldValidatorInterface> getCustomValidations(Field field) {
		
		CcpJsonFieldValidator annotation = field.getAnnotation(CcpJsonFieldValidator.class);
		
		Class<?>[] customFieldValidations = annotation.customFieldValidations();
		
		List<CcpJsonFieldValidatorInterface> collect = Arrays.asList(customFieldValidations).stream()
		.map(clazz -> new CcpReflectionConstructorDecorator(clazz))
		.map(constructor -> (CcpJsonFieldValidatorInterface)constructor.newInstance())
		.collect(Collectors.toList());
		
		return collect;
	}
	
	private List<CcpJsonFieldValidatorInterface> getDefaultValidations(){
		List<CcpJsonFieldValidatorInterface> asList = new ArrayList<>();
		asList.add(CcpJsonFieldTypeError.annotationIsMissing);
		asList.add(CcpJsonFieldTypeError.requiredFieldIsMissing);
		asList.add(CcpJsonFieldTypeError.collectionOrNotCollection);
		asList.add(CcpJsonFieldTypeError.incompatibleType);
		
		return asList;
	}
}
