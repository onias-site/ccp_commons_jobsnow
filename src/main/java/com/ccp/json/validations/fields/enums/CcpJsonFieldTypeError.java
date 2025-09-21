package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.decorators.engine.CcpEntityExpurgableOptions;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeArray;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNested;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeText;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTime;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;
import com.ccp.json.validations.global.engine.CcpJsonValidationRulesEngine;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

public enum CcpJsonFieldTypeError implements CcpJsonFieldName, CcpJsonFieldValidatorInterface {
	
	incompatibleType(CcpJsonFieldErrorHandleType.breakFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {

		    String fieldName = field.getName();
			Object value = json.getDynamicVersion().getAsObject(fieldName);

			if (value == null) {
				return false;
			}

			Predicate<CcpJsonRepresentation> evaluateCorrectType = type.evaluateCompatibleType(fieldName);
			boolean incompatibleType = false == evaluateCorrectType.test(json);
			return incompatibleType;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String providedType = json.getDynamicVersion().get(fieldName).getClass().getName();
			String expectedType = type.name();
			return "The field '" + fieldName + "' must be '" + expectedType + "' type, but this field is '" + providedType + "' type";
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String expectedType = type.name();
			String ruleExplanation = "The field '" + fieldName + "' must be '" + expectedType + "' type";
			return ruleExplanation;
		}

	},
	annotationIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {

			boolean annotationIsMissing = false == field.isAnnotationPresent(type.annotation);

			return annotationIsMissing;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String name = type.annotation.getClass().getName();
			String errorMessage = "The field '" + fieldName
					+ "' must be annoted by '" + name + "' annotation" ;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String ruleExplanation = this.getErrorMessage(CcpOtherConstants.EMPTY_JSON, field, type);
			return ruleExplanation;
		}
	},
	requiredFieldIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			boolean thisFieldIsPresent = json.getDynamicVersion().containsAllFields(fieldName);
			return thisFieldIsPresent;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String errorMessage = "The field '" + fieldName + "' is missing";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String ruleExplanation = "The field '" + fieldName + "' is required";
			return ruleExplanation;
		}
	},
	
	integerNumber(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field '" + fieldName + "' has a value '" + providedValue + "' that is not a integer number";
			return errorMessage;
		}

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
		    
			CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    boolean allowsDoubleValue = annotation.integerNumber();
			
		    if(allowsDoubleValue) {
		    	return false;
		    }
		    String fieldName = field.getName();
		    boolean isInvalid = false == json.getDynamicVersion().getAsStringDecorator(fieldName).isLongNumber();
		    return isInvalid;
		}

		public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String ruleExplanation = "The field '" + fieldName + "' accepts only integer number";
			return ruleExplanation;
		}

		public boolean hasRules(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
			boolean integerNumber = annotation.integerNumber();
			return integerNumber;
		}
		
	},
	
	numberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    double number = annotation.maxValue();
		    String fieldName = field.getName();
			Double value = json.getDynamicVersion().getAsObject(fieldName);
		    return value > number;
		}

		@SuppressWarnings("unchecked")
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    Double value = annotation.maxValue();
		    return (T) value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field '" + fieldName + "' has a value '" + providedValue + "' that is greater than specified value '" + boundValue + "'";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field '" + fieldName + "' can not accept numeric values greater than " + boundValue;
			return ruleExplanation;
		}
	},
	numberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    Integer number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			Double value = json.getDynamicVersion().getAsObject(fieldName);
		    return value < number;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    Double value = annotation.minValue();
 
		    return (T) value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field '" + fieldName + "' has a value '" + providedValue + "' that is less than specified value '" + boundValue  + "'";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field '" + fieldName + "' can not accept numeric values less than " + boundValue;
			return ruleExplanation;
		}
	},
	numberAllowed(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			List<Double> allowedValues = this.getValidationParameter(field, type);
			
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
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    double[] allowedValues = annotation.allowedValues();
			List<Double> list = new ArrayList<>();
			for (double value : allowedValues) {
				list.add(value);
			}
			return (T) list;
		}

		@Override
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String errorMessage = "The field '" + fieldName + "' has a value '" + providedValue + "' that is not present in the allowed list " + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Double> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field '" + fieldName + "' can not accept numeric values that are not present in the following list: " + boundValue;
			return ruleExplanation;
		}
	},
	arrayMinSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			Collection<?> value = json.getDynamicVersion().getAsObject(fieldName);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			return validationParameter > size;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeArray annotation = field.getAnnotation(CcpJsonFieldTypeArray.class);
			Integer value = annotation.minSize();
			return (T)value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			List<Object> providedValue = json.getDynamicVersion().getAsObjectList(fieldName);
			String errorMessage = "The field '" + fieldName + "' has a value " + providedValue 
					+ " that is a collection whith a size '"
			+ providedValue.size()  + "' that is less than specified value '" + bound + "'";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field '" + fieldName + "' has to be a collection values with size that can not be less than " + boundValue;
			return ruleExplanation;
		}
	},
	arrayMaxSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			Collection<?> value = json.getDynamicVersion().getAsObject(fieldName);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			return validationParameter < size;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeArray annotation = field.getAnnotation(CcpJsonFieldTypeArray.class);
			Integer value = annotation.maxSize();
			return (T)value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			List<Object> providedValue = json.getDynamicVersion().getAsObjectList(fieldName);
			String errorMessage = "The field '" + fieldName + "' has a value " + providedValue 
					+ " that is a collection whith a size '"
			+ providedValue.size()  + "' that is greater than specified value '" + bound + "'";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field '" + fieldName + "' has to be collection values with size that can not be greater than " + boundValue;
			return ruleExplanation;
		}
	},
	arrayNonReapeted(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			Collection<?> value = json.getDynamicVersion().getAsObject(fieldName);
			Set<?> set = new HashSet<>(value);
			int size = set.size();
			int size2 = value.size();
			return size == size2;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			List<Object> providedValue = json.getDynamicVersion().getAsObjectList(fieldName);
			String errorMessage = "The field '" + fieldName + "' has a value " + providedValue 
					+ " that is a collection that has duplicated items";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String errorMessage = "The field '" + fieldName + "' has to be a collection that can not accept duplicated items";
			return errorMessage;
		}
	},
	textMinLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsObject(fieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			return length < validationParameter;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeText annotation = field.getAnnotation(CcpJsonFieldTypeText.class);
			Integer value = annotation.minLength();
			return (T)value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String providedValue = json.getDynamicVersion().getAsString(fieldName);
			String errorMessage = "The field '" + fieldName + "' has a value " + providedValue 
					+ " that is a string whith a length '"
			+ providedValue.length()  + "' that is less than specified value '" + bound + "'";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String errorMessage = "The field '" + fieldName + "' accepts string value whith a specified  minimum length '" + bound + "'";
			return errorMessage;
		}
	},
	textMaxLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsObject(fieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			return length > validationParameter;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeText annotation = field.getAnnotation(CcpJsonFieldTypeText.class);
			Integer value = annotation.maxLength();
			return (T)value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String providedValue = json.getDynamicVersion().getAsString(fieldName);
			String errorMessage = "The field '" + fieldName + "' has a value " + providedValue 
					+ " that is a string whith a length '"
			+ providedValue.length()  + "' that is greater than specified value '" + bound + "'";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String errorMessage = "The field '" + fieldName + "' accepts string value whith a specified  maximum length '" + bound + "'";
			return errorMessage;
		}
	},
	textAllowedValues(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,   Field field, CcpJsonFieldType type) {
			List<String> validationParameter = this.getValidationParameter(field, type);
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
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeText annotation = field.getAnnotation(CcpJsonFieldTypeText.class);
			String[] allowedValues = annotation.allowedValues();
			List<String> value = Arrays.asList(allowedValues);
			return (T)value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String errorMessage = "The field '" + fieldName + "' has a value '" + providedValue + "' that is not present in the allowed list " + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Double> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field '" + fieldName + "' can not accept values that are not present in the following list: " + boundValue;
			return ruleExplanation;
		}
	},
	textRegex(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			String validationParameter = this.getValidationParameter(field, type);
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
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeText annotation = field.getAnnotation(CcpJsonFieldTypeText.class);
			String value = annotation.regexValidation();
			return (T)value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String errorMessage = "The field '" + fieldName + "' has a value '" + providedValue + 
					"' that is incompatible whith the specified regular expression '" + validationParameter + "'";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			String errorMessage = "The field '" + fieldName + "' accepts text value that matches with a specified regular expression '" + validationParameter + "'";
			return errorMessage;
		}
	},
	timeMaxValueBeforeCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.max.hasError(json, field, TimeOptions.before);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.max.getErrorMessage(json, field, TimeOptions.before);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.max.getRuleExplanation(field, TimeOptions.before);
			return errorMessage;
		}
	},
	timeMinValueBeforeCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.min.hasError(json, field, TimeOptions.before);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.min.getErrorMessage(json, field, TimeOptions.before);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.min.getRuleExplanation(field, TimeOptions.before);
			return errorMessage;
		}
	},
	timeMaxValueAfterCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.max.hasError(json, field, TimeOptions.after);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.max.getErrorMessage(json, field, TimeOptions.after);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.max.getRuleExplanation(field, TimeOptions.after);
			return errorMessage;
		}
	},
	timeMinValueAfterCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.min.hasError(json, field, TimeOptions.after);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.min.getErrorMessage(json, field, TimeOptions.after);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.min.getRuleExplanation(field, TimeOptions.after);
			return errorMessage;
		}
	},
	nestedJson(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Map<String, Object> errors = this.getError(json, field, type);
			boolean hasNoErrors = false == errors.isEmpty();
			return hasNoErrors;
		}
		
		public Map<String, Object> getError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			CcpJsonRepresentation innerJson = json.getDynamicVersion().getInnerJson(fieldName);
			CcpJsonFieldTypeNested annotation = field.getAnnotation(CcpJsonFieldTypeNested.class);
			Class<?> validationClass = annotation.validationClass();
			CcpJsonRepresentation errors = CcpJsonValidatorEngine.INSTANCE.getErrors(validationClass, innerJson);
			return errors.content;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			return "";
		}

		public  Map<String, Object> getRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNested annotation = field.getAnnotation(CcpJsonFieldTypeNested.class);
			Class<?> validationClass = annotation.validationClass();
			CcpJsonRepresentation errors = CcpJsonValidationRulesEngine.INSTANCE.getRulesExplanations(validationClass);
			return errors.content;
		}
	}
	
	
	;
	
	private CcpJsonFieldTypeError(CcpJsonFieldErrorHandleType handleType) {
		this.errorHandleType = handleType;
	}

	private final CcpJsonFieldErrorHandleType errorHandleType;

	public CcpJsonFieldErrorHandleType getErrorHandleType() {
		return this.errorHandleType;
	}
	
	protected final Object getProvidedValue(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {

		String fieldName = field.getName();
		Object value = json.getDynamicVersion().get(fieldName);

		return value;
	}


	enum JsonFieldNames implements CcpJsonFieldName {
		explanation,
	}
}
enum TimeOptions{
	before {
		long subtractNumber(long time) {
			long currentTimeMillis = System.currentTimeMillis();
			return currentTimeMillis - time;
		}
	},
	after {
		long subtractNumber(long time) {
			long currentTimeMillis = System.currentTimeMillis();
			return time - currentTimeMillis;
		}
	}
	;
	abstract long subtractNumber(long time);

	public Long getEnlapsedTime(CcpJsonRepresentation json, Field field) {
		String fieldName = field.getName();
		
		Long time = json.getDynamicVersion().getAsLongNumber(fieldName);
		;
		long providedValue = this.subtractNumber(time);
		
		return providedValue;
	}
	
	public Long getEnlapsedInterval(CcpJsonRepresentation json, Field field) {
		Long enlapsedTime = this.getEnlapsedTime(json, field);
		CcpJsonFieldTypeTime annotation = field.getAnnotation(CcpJsonFieldTypeTime.class);
		long enlapsedInterval = (enlapsedTime / annotation.intervalType().milliseconds) + 1;
		return enlapsedInterval;
	}
}

enum TimeValueExtractorFromAnnotation{
	max("maximum") {
		int getValueFromAnnotation(CcpJsonFieldTypeTime annotation) {
			int value = annotation.maxValue();
			return value;
		}

		boolean hasError(Long enlapsedTime, Long validationParameter) {
			return enlapsedTime > validationParameter;
		}

	},
	min("minimum") {
		int getValueFromAnnotation(CcpJsonFieldTypeTime annotation) {
			int value = annotation.minValue();
			return value;
		}

		boolean hasError(Long enlapsedTime, Long validationParameter) {
			return enlapsedTime < validationParameter;
		}
	}
	;
	
	private final String word;
	
	
	private TimeValueExtractorFromAnnotation(String word) {
		this.word = word;
	}

	abstract int getValueFromAnnotation(CcpJsonFieldTypeTime annotation); 
	
	protected Long getValueFromAnnotationInMilliseconds(CcpJsonRepresentation json, Field field) {
		CcpJsonFieldTypeTime annotation = field.getAnnotation(CcpJsonFieldTypeTime.class);
		CcpEntityExpurgableOptions intervalType = annotation.intervalType();
		Long milliseconds = intervalType.milliseconds;
		Integer value = this.getValueFromAnnotation(annotation);
		return Long.valueOf(milliseconds * value);
	}
	
	public final boolean hasError(CcpJsonRepresentation json, Field field, TimeOptions timeOptions) {
		Long enlapsedTime = timeOptions.getEnlapsedTime(json, field);
		
		Long validationParameter = this.getValueFromAnnotationInMilliseconds(json, field);
		
		return enlapsedTime > validationParameter;
	}
	
	public final String getErrorMessage(CcpJsonRepresentation json, Field field, TimeOptions timeOptions) {
		
		String fieldName = field.getName();
		Long valueFromAnnotationInMilliseconds = this.getValueFromAnnotationInMilliseconds(json, field);
		
		CcpJsonFieldTypeTime annotation = field.getAnnotation(CcpJsonFieldTypeTime.class);
		CcpEntityExpurgableOptions intervalType = annotation.intervalType();
		
		Long providedValue = json.getDynamicVersion().getAsLongNumber(fieldName);
		CcpTimeDecorator ctd = new CcpTimeDecorator(providedValue);
		String formattedDateTime = ctd.getFormattedDateTime(intervalType.format);
		
		int valueFromAnnotation = this.getValueFromAnnotation(annotation);
		String intervalTypeWord = intervalType.word.toLowerCase();
		String timeOptionsName = timeOptions.name();
		String errorMessage = "The field '" + fieldName + "' has a value '" + formattedDateTime
				+ "' and this value has to be in the " + this.word + " " 
				+ valueFromAnnotation + " " + intervalTypeWord + " " + timeOptionsName + " this current time. "
						+ "But it is " + valueFromAnnotationInMilliseconds + " " + intervalTypeWord + " "
				+ timeOptionsName + " this current time. ";
		return errorMessage;
	}

	public final String getRuleExplanation(Field field, TimeOptions timeOptions) {
		
		String fieldName = field.getName();
		
		CcpJsonFieldTypeTime annotation = field.getAnnotation(CcpJsonFieldTypeTime.class);
		CcpEntityExpurgableOptions intervalType = annotation.intervalType();
		
		int valueFromAnnotation = this.getValueFromAnnotation(annotation);
		String intervalTypeWord = intervalType.word.toLowerCase();
		String timeOptionsName = timeOptions.name();
		String errorMessage = "The field '" + fieldName + "' accepts timestamp values that are at " + this.word + " " 
				+ valueFromAnnotation + " " + intervalTypeWord + " " + timeOptionsName + " the current time. "
						;
		return errorMessage;
	}
	
	abstract boolean hasError(Long enlapsedTime, Long validationParameter);
}
