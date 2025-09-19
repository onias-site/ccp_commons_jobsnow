package com.ccp.json.fields.validations.enums;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldArrayType;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldNumberType;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldTextType;

public enum CcpJsonFieldErrorTypes implements CcpJsonFieldName {
	incompatibleType(CcpJsonFieldErrorHandleType.breakFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {

			Object value = valueExtractor.getValue(json, field);

			if (value == null) {
				return false;
			}

			String fieldName = field.getName();
			Predicate<CcpJsonRepresentation> evaluateCorrectType = type.evaluateCompatibleType(fieldName);
			boolean test = evaluateCorrectType.test(json);
			return test;
		}

		@SuppressWarnings("unchecked")
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			String typeName = type.name();
			return (T)typeName;
		}

	},
	annotationIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {

			boolean annotationIsMissing = field.isAnnotationPresent(type.annotation) == false;

			return annotationIsMissing;
		}


		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			String name = type.annotation.getClass().getName();
			String string = "This field has to be annoted by " + name;
			return (T)string;
		}

		
		protected Object getRealValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			return "";
		}
	},
	requiredFieldIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			String fieldName = field.getName();
			boolean thisFieldIsPresent = json.getDynamicVersion().containsAllFields(fieldName);
			return thisFieldIsPresent;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			String fieldName = field.getName();
			String result = "The field '" + fieldName + "' is required";
			return (T)result;
		}

		protected Object getRealValue(CcpJsonRepresentation json, Class<?> clazz, Field field,  CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			return "";
		}
	},
	objectNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
		    
		    CcpJsonFieldNumberType annotation = field.getAnnotation(CcpJsonFieldNumberType.class);
		    double number = annotation.maxValue();
		    Double value = valueExtractor.getValue(json, field);
		    return value > number;
		}


		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
		    CcpJsonFieldNumberType annotation = field.getAnnotation(CcpJsonFieldNumberType.class);
		    Double value = annotation.maxValue();

		    return (T) value;
		}
	},
	objectNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
		    Integer number = this.getExpectedValue(json, clazz, field, type, valueExtractor);
		    Double value = valueExtractor.getValue(json, field);
		    return value < number;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, 	CcpJsonFieldValueExtractor valueExtractor) {
		    CcpJsonFieldNumberType annotation = field.getAnnotation(CcpJsonFieldNumberType.class);
		    Double value = annotation.minValue();
 
		    return (T) value;
		}
	},
	objectNumberAllowed(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			List<Double> allowedValues = this.getExpectedValue(json, clazz, field, type, valueExtractor);
			
			boolean doNotValidate = allowedValues.isEmpty();
			
			if(doNotValidate) {
				return false;
			}
			
			Double value = valueExtractor.getValue(json, field);
			boolean isAllowed = allowedValues.contains(value);
			return isAllowed;
		}

		
		@SuppressWarnings("unchecked")
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
		    CcpJsonFieldNumberType annotation = field.getAnnotation(CcpJsonFieldNumberType.class);
		    double[] allowedValues = annotation.allowedValues();
			List<Double> list = new ArrayList<>();
			for (double value : allowedValues) {
				list.add(value);
			}
			return (T) list;
		}
	},
	objectArrayMinSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			Collection<?> value = valueExtractor.getValue(json, field);
			Integer expectedValue = this.getExpectedValue(json, clazz, field, type, valueExtractor);
			int size = value.size();
			return expectedValue > size;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			CcpJsonFieldArrayType annotation = field.getAnnotation(CcpJsonFieldArrayType.class);
			Integer value = annotation.minSize();
			return (T)value;
		}
	},
	objectArrayMaxSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			Collection<?> value = valueExtractor.getValue(json, field);
			Integer expectedValue = this.getExpectedValue(json, clazz, field, type, valueExtractor);
			int size = value.size();
			return expectedValue < size;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			CcpJsonFieldArrayType annotation = field.getAnnotation(CcpJsonFieldArrayType.class);
			Integer value = annotation.maxSize();
			return (T)value;
		}
	},
	objectArrayNonReapeted(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			Collection<?> value = valueExtractor.getValue(json, field);
			Set<?> set = new HashSet<>(value);
			int size = set.size();
			int size2 = value.size();
			return size == size2;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			return (T)"";
		}
	},
	objectTextMinLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			String value = valueExtractor.getValue(json, field);
			int length = value.length();
			Integer expectedValue = this.getExpectedValue(json, clazz, field, type, valueExtractor);
			return length < expectedValue;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, 	CcpJsonFieldValueExtractor valueExtractor) {
			CcpJsonFieldTextType annotation = field.getAnnotation(CcpJsonFieldTextType.class);
			Integer value = annotation.minLength();
			return (T)value;
		}
	},
	objectTextMaxLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			String value = valueExtractor.getValue(json, field);
			int length = value.length();
			Integer expectedValue = this.getExpectedValue(json, clazz, field, type, valueExtractor);
			return length > expectedValue;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, 	CcpJsonFieldValueExtractor valueExtractor) {
			CcpJsonFieldTextType annotation = field.getAnnotation(CcpJsonFieldTextType.class);
			Integer value = annotation.maxLength();
			return (T)value;
		}
	},
	objectTextAllowedValues(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz,  Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			List<String> expectedValue = this.getExpectedValue(json, clazz, field, type, valueExtractor);
			boolean doNotValidate = expectedValue.isEmpty();
			
			if(doNotValidate) {
				return false;
			}
			
			String value = valueExtractor.getValue(json, field);
			
			boolean contains = expectedValue.contains(value);
			
			return contains;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			CcpJsonFieldTextType annotation = field.getAnnotation(CcpJsonFieldTextType.class);
			String[] allowedValues = annotation.allowedValues();
			List<String> value = Arrays.asList(allowedValues);
			return (T)value;
		}
	},
	objectTextRegex(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			String expectedValue = this.getExpectedValue(json, clazz, field, type, valueExtractor);
			boolean doNotValidate = expectedValue.trim().isEmpty();
			if(doNotValidate) {
				return false;
			}
			String value = valueExtractor.getValue(json, field);
			boolean matches = value.matches(expectedValue);
			return matches;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
			CcpJsonFieldTextType annotation = field.getAnnotation(CcpJsonFieldTextType.class);
			String value = annotation.regexValidation();
			return (T)value;
		}
	},
	objectTimeMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz,
				Field field, CcpJsonFieldTypes type,
				CcpJsonFieldValueExtractor valueExtractor) {
			// FIXME Auto-generated method stub
			return false;
		}

		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz,
				Field field, CcpJsonFieldTypes type,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	objectTimeMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz,
				Field field, CcpJsonFieldTypes type,
				CcpJsonFieldValueExtractor valueExtractor) {
			// FIXME Auto-generated method stub
			return false;
		}

		
		<T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz,
				Field field, CcpJsonFieldTypes type,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	;

	private CcpJsonFieldErrorTypes(CcpJsonFieldErrorHandleType handleType) {
		this.errorHandleType = handleType;
	}

	private final CcpJsonFieldErrorHandleType errorHandleType;

	abstract <T extends Object> T getExpectedValue(CcpJsonRepresentation json, Class<?> clazz,
			Field field, CcpJsonFieldTypes type,
			CcpJsonFieldValueExtractor valueExtractor);

	abstract boolean hasError(CcpJsonRepresentation json, Class<?> clazz,
			Field field, CcpJsonFieldTypes type,
			CcpJsonFieldValueExtractor valueExtractor);

	protected Object getRealValue(CcpJsonRepresentation json, Class<?> clazz,
			Field field, CcpJsonFieldTypes type,
			CcpJsonFieldValueExtractor valueExtractor) {

		Object value = valueExtractor.getValue(json, field);

		return value;
	}

	final CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field, CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {
	
		String expectedValue = this.getExpectedValue(json, clazz, field, type, valueExtractor);
		String message = this.name();

		CcpJsonRepresentation error = CcpOtherConstants.EMPTY_JSON
				.put(JsonFieldNames.errorMessage, message)
				.put(JsonFieldNames.valueToCompare, expectedValue)
				;

		Object providedValue = this.getRealValue(json, clazz, field, type, valueExtractor);

		boolean hasNoRealValue = providedValue.toString().trim().isEmpty();

		if (hasNoRealValue) {
			return error;
		}

		CcpJsonRepresentation put = error.put(JsonFieldNames.providedValue, providedValue);
		return put;
	}

	public final CcpJsonRepresentation evaluate(CcpJsonRepresentation errors,
			CcpJsonRepresentation json, Class<?> clazz, Field field,
			CcpJsonFieldTypes type, CcpJsonFieldValueExtractor valueExtractor) {

		boolean hasNoError = this.hasError(json, clazz, field, type,
				valueExtractor) == false;

		if (hasNoError) {
			return CcpOtherConstants.EMPTY_JSON;
		}

		String ruleName = this.name();
		String fieldName = field.getName();

		CcpJsonRepresentation relatedErrorsToThisField = errors.getDynamicVersion().getInnerJson(fieldName);
		CcpJsonRepresentation rule = relatedErrorsToThisField.getDynamicVersion().getInnerJson(ruleName);

		Object providedValue = valueExtractor.getValue(json, field);
		CcpJsonRepresentation withProvidedValue = rule.put(JsonFieldNames.providedValue, providedValue);

		CcpJsonRepresentation error = this.getError(json, clazz, field, type, valueExtractor);
		CcpJsonRepresentation withError = withProvidedValue.put(JsonFieldNames.error, error);

		CcpJsonRepresentation updatedField = relatedErrorsToThisField.put(this, withError);
		CcpJsonRepresentation updatedErrors = errors.getDynamicVersion().put(fieldName, updatedField);

		this.errorHandleType.maybeBreakValidation(updatedErrors);
		
		return updatedErrors;
	}

	enum JsonFieldNames implements CcpJsonFieldName {
		error, ruleExplanation, providedValue, errorMessage, valueToCompare
	}
}
