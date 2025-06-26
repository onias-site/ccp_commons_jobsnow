package com.ccp.json.fields.validations.enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Predicate;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("rawtypes")
public enum CcpJsonFieldErrorTypes {
	incompatibleType(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		public CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, 
				Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			
			String fieldName = field.getName();
			
			Object value = valueExtractor.getValue(json, fieldName);
			
			if(value == null) {
				return false;
			}

			boolean test = predicate.test(json);
			
			return test;
 		}
	},
	annotationIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		public CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, 
				Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			
			boolean annotationIsMissing = field.isAnnotationPresent(annotation) == false;
			
			
			return annotationIsMissing;
		}
	},
	requiredAtLeastOneFieldIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation),
	requiredFieldIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation),
	objectNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectNumberAllowed(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectArrayMinSize(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectArrayMaxSize(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectArrayNonReapeted(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectTextMinLength(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectTextMaxLength(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectTextAllowedValues(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectTextRegex(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectTimeMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation),
	objectTimeMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation),
	

	;
	
	private CcpJsonFieldErrorTypes(CcpJsonFieldErrorHandleType handleType) {
		this.errorHandleType = handleType;
	}


	private final CcpJsonFieldErrorHandleType errorHandleType;
	
	public abstract CcpJsonRepresentation getError(
			CcpJsonRepresentation json,
			Class<?> clazz, 
			Field field,
			Class<? extends Annotation> annotation,
			CcpJsonFieldValueExtractor valueExtractor 
			);

	public abstract boolean hasError(
			CcpJsonRepresentation json,
			Class<?> clazz, 
			Field field,
			Class<? extends Annotation> annotation,
			Predicate<CcpJsonRepresentation> predicate,
			CcpJsonFieldValueExtractor valueExtractor 
			);
	
	
	public CcpJsonRepresentation evaluate(
			CcpJsonRepresentation errors,
			CcpJsonRepresentation json,
			Class<?> clazz, 
			Field field,
			Class<? extends Annotation> annotation,
			Predicate<CcpJsonRepresentation> predicate,
			CcpJsonFieldValueExtractor valueExtractor 
			) {
		
		boolean hasNoError = this.hasError(json, clazz, field, annotation, predicate, valueExtractor) == false;
		
		if(hasNoError) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		
		CcpJsonRepresentation errorList = this.getError(json, clazz, field, annotation, valueExtractor);
		
		
		String fieldName = field.getName();
		String errorName = this.name();
		CcpJsonRepresentation addToItem = errors.addToItem(fieldName, errorName, errorList);
		this.errorHandleType.breakValidationIfNecessary(addToItem);
		return addToItem;
	}

}
