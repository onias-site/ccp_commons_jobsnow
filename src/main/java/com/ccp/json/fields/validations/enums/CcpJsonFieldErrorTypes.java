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
import com.ccp.json.fields.validations.annotations.CcpJsonFieldNested;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldNumberType;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldTextType;
import com.ccp.json.fields.validations.engine.CcpJsonFieldsValidator;

public enum CcpJsonFieldErrorTypes implements CcpJsonFieldName {
	incompatibleType(CcpJsonFieldErrorHandleType.breakFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {

		    String fieldName = field.getName();
			Object value = json.getDynamicVersion().getAsObject(fieldName);

			if (value == null) {
				return false;
			}

			Predicate<CcpJsonRepresentation> evaluateCorrectType = type.evaluateCompatibleType(fieldName);
			boolean incompatibleType = false == evaluateCorrectType.test(json);
			return incompatibleType;
		}

		@SuppressWarnings("unchecked")
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			String typeName = type.name();
			return (T)typeName;
		}
		
		protected Object getProvidedValue(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type) {
			String fieldName = field.getName();
			Object object = json.getDynamicVersion().get(fieldName).getClass().getName();
			return object;
		}

	},
	annotationIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {

			boolean annotationIsMissing = false == field.isAnnotationPresent(type.annotation);

			return annotationIsMissing;
		}


		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			String name = type.annotation.getClass().getName();
			String string = "This field has to be annoted by " + name;
			return (T)string;
		}

		
		protected Object getProvidedValue(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			return "";
		}
	},
	requiredFieldIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			String fieldName = field.getName();
			boolean thisFieldIsPresent = json.getDynamicVersion().containsAllFields(fieldName);
			return thisFieldIsPresent;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			String fieldName = field.getName();
			String result = "The field '" + fieldName + "' is required";
			return (T)result;
		}

		protected Object getProvidedValue(CcpJsonRepresentation json,  Field field,  CcpJsonFieldTypes type) {
			return "";
		}
	},
	numberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    
		    CcpJsonFieldNumberType annotation = field.getAnnotation(CcpJsonFieldNumberType.class);
		    double number = annotation.maxValue();
		    String fieldName = field.getName();
			Double value = json.getDynamicVersion().getAsObject(fieldName);
		    return value > number;
		}


		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    CcpJsonFieldNumberType annotation = field.getAnnotation(CcpJsonFieldNumberType.class);
		    Double value = annotation.maxValue();

		    return (T) value;
		}
	},
	numberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    Integer number = this.getValidationParameter(json,  field, type);
		    String fieldName = field.getName();
			Double value = json.getDynamicVersion().getAsObject(fieldName);
		    return value < number;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    CcpJsonFieldNumberType annotation = field.getAnnotation(CcpJsonFieldNumberType.class);
		    Double value = annotation.minValue();
 
		    return (T) value;
		}
	},
	numberAllowed(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			List<Double> allowedValues = this.getValidationParameter(json,  field, type);
			
			boolean doNotValidate = allowedValues.isEmpty();
			
			if(doNotValidate) {
				return false;
			}
			
		    String fieldName = field.getName();
			Double value = json.getDynamicVersion().getAsObject(fieldName);
			boolean isAllowed = allowedValues.contains(value);
			return isAllowed;
		}

		
		@SuppressWarnings("unchecked")
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    CcpJsonFieldNumberType annotation = field.getAnnotation(CcpJsonFieldNumberType.class);
		    double[] allowedValues = annotation.allowedValues();
			List<Double> list = new ArrayList<>();
			for (double value : allowedValues) {
				list.add(value);
			}
			return (T) list;
		}
	},
	arrayMinSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    String fieldName = field.getName();
			Collection<?> value = json.getDynamicVersion().getAsObject(fieldName);
			Integer validationParameter = this.getValidationParameter(json,  field, type);
			int size = value.size();
			return validationParameter > size;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			CcpJsonFieldArrayType annotation = field.getAnnotation(CcpJsonFieldArrayType.class);
			Integer value = annotation.minSize();
			return (T)value;
		}
	},
	arrayMaxSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    String fieldName = field.getName();
			Collection<?> value = json.getDynamicVersion().getAsObject(fieldName);
			Integer validationParameter = this.getValidationParameter(json,  field, type);
			int size = value.size();
			return validationParameter < size;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			CcpJsonFieldArrayType annotation = field.getAnnotation(CcpJsonFieldArrayType.class);
			Integer value = annotation.maxSize();
			return (T)value;
		}
	},
	arrayNonReapeted(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    String fieldName = field.getName();
			Collection<?> value = json.getDynamicVersion().getAsObject(fieldName);
			Set<?> set = new HashSet<>(value);
			int size = set.size();
			int size2 = value.size();
			return size == size2;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			return (T)"";
		}
	},
	textMinLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsObject(fieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(json,  field, type);
			return length < validationParameter;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			CcpJsonFieldTextType annotation = field.getAnnotation(CcpJsonFieldTextType.class);
			Integer value = annotation.minLength();
			return (T)value;
		}
	},
	textMaxLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsObject(fieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(json,  field, type);
			return length > validationParameter;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			CcpJsonFieldTextType annotation = field.getAnnotation(CcpJsonFieldTextType.class);
			Integer value = annotation.maxLength();
			return (T)value;
		}
	},
	textAllowedValues(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json,   Field field, CcpJsonFieldTypes type) {
			List<String> validationParameter = this.getValidationParameter(json,  field, type);
			boolean doNotValidate = validationParameter.isEmpty();
			
			if(doNotValidate) {
				return false;
			}
			
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsObject(fieldName);
			
			boolean contains = validationParameter.contains(value);
			
			return contains;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			CcpJsonFieldTextType annotation = field.getAnnotation(CcpJsonFieldTextType.class);
			String[] allowedValues = annotation.allowedValues();
			List<String> value = Arrays.asList(allowedValues);
			return (T)value;
		}
	},
	textRegex(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			String validationParameter = this.getValidationParameter(json,  field, type);
			boolean doNotValidate = validationParameter.trim().isEmpty();
			if(doNotValidate) {
				return false;
			}
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsObject(fieldName);
			boolean matches = value.matches(validationParameter);
			return matches;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {
			CcpJsonFieldTextType annotation = field.getAnnotation(CcpJsonFieldTextType.class);
			String value = annotation.regexValidation();
			return (T)value;
		}
	},
	timeMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type) {
			// FIXME Auto-generated method stub
			return false;
		}

		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	timeMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type) {
			// FIXME Auto-generated method stub
			return false;
		}

		
		<T extends Object> T getValidationParameter(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	nested(CcpJsonFieldErrorHandleType.continueFieldValidation){

		@SuppressWarnings("unchecked")
		
		<T> T getValidationParameter(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type) {
			String fieldName = field.getName();
			CcpJsonRepresentation innerJson = json.getDynamicVersion().getInnerJson(fieldName);
			CcpJsonFieldNested annotation = field.getAnnotation(CcpJsonFieldNested.class);
			Class<?> validationClass = annotation.validationClass();
			CcpJsonRepresentation errors = CcpJsonFieldsValidator.INSTANCE.getErrors(validationClass, innerJson);
			return (T) errors;
		}

		
		boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type) {
			CcpJsonRepresentation errors = this.getValidationParameter(json, field, type);
			boolean hasNoErrors = false == errors.isEmpty();
			return hasNoErrors;
		}
		
		
		protected CcpJsonRepresentation getError(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type) {
			CcpJsonRepresentation errors = this.getValidationParameter(json, field, type);
			return errors;
		}
		
	}
	;

	private CcpJsonFieldErrorTypes(CcpJsonFieldErrorHandleType handleType) {
		this.errorHandleType = handleType;
	}

	private final CcpJsonFieldErrorHandleType errorHandleType;

	abstract <T extends Object> T getValidationParameter(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type);

	abstract boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type);

	protected Object getProvidedValue(CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {

		String fieldName = field.getName();
		Object value = json.getDynamicVersion().get(fieldName);

		return value;
	}

	protected CcpJsonRepresentation getError(CcpJsonRepresentation json, Field field, CcpJsonFieldTypes type) {
	
		String validationParameter = this.getValidationParameter(json,  field, type);
		String message = this.name();

		CcpJsonRepresentation error = CcpOtherConstants.EMPTY_JSON
				.put(JsonFieldNames.errorMessage, message)
				.put(JsonFieldNames.validationParameter, validationParameter)
				;

		Object providedValue = this.getProvidedValue(json,  field, type);

		boolean hasNoRealValue = providedValue.toString().trim().isEmpty();

		if (hasNoRealValue) {
			return error;
		}

		CcpJsonRepresentation put = error.put(JsonFieldNames.providedValue, providedValue);
		return put;
	}

	public final CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json,  Field field, CcpJsonFieldTypes type) {

		boolean hasNoError = false == this.hasError(json,  field, type);

		if (hasNoError) {
			return CcpOtherConstants.EMPTY_JSON;
		}

		String ruleName = this.name();
		String fieldName = field.getName();

		CcpJsonRepresentation relatedErrorsToThisField = errors.getDynamicVersion().getInnerJson(fieldName);
		CcpJsonRepresentation rule = relatedErrorsToThisField.getDynamicVersion().getInnerJson(ruleName);

		Object providedValue = json.getDynamicVersion().get(fieldName);
		CcpJsonRepresentation withProvidedValue = rule.put(JsonFieldNames.providedValue, providedValue);

		CcpJsonRepresentation error = this.getError(json, field, type);
		CcpJsonRepresentation withError = withProvidedValue.put(JsonFieldNames.error, error);

		CcpJsonRepresentation updatedField = relatedErrorsToThisField.put(this, withError);
		CcpJsonRepresentation updatedErrors = errors.getDynamicVersion().put(fieldName, updatedField);

		this.errorHandleType.maybeBreakValidation(updatedErrors);
		
		return updatedErrors;
	}

	enum JsonFieldNames implements CcpJsonFieldName {
		error, ruleExplanation, providedValue, errorMessage, validationParameter
	}
}
