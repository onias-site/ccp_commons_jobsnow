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
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.db.utils.decorators.engine.CcpEntityExpurgableOptions;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidator;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeArray;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
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
			return "The field " + fieldName + " must be " + expectedType + " type, but this field is " + providedType + " type";
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			return "";
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			return false;
		}
	},
	annotationIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {

			boolean annotationIsMissing = false == field.isAnnotationPresent(type.requiredAnnotation);
			
			return annotationIsMissing;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			String fieldName = field.getName();
			String name = type.requiredAnnotation.getClass().getName();
			String errorMessage = "The field " + fieldName
					+ " must be annoted by " + name + " annotation";
			
			throw new RuntimeException(errorMessage);
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			return "";
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			return false;
		}
		
		public boolean skipValidationIfFieldIsMissing(CcpJsonRepresentation json, Field field) {
			return false;
		}
	},
	requiredFieldIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			
			CcpJsonFieldValidator annotation = field.getAnnotation(CcpJsonFieldValidator.class);
			boolean optional = false == annotation.required();
			
			if(optional) {
				return false;
			}
			
			String fieldName = field.getName();
			boolean thisFieldIsPresent = json.getDynamicVersion().containsAllFields(fieldName);
			return thisFieldIsPresent;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " is missing";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String ruleExplanation = "The field " + fieldName + " is required";
			return ruleExplanation;
		}
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			return true;
		}
		
		public boolean skipValidationIfFieldIsMissing(CcpJsonRepresentation json, Field field) {
			return false;
		}
	},
	
	collectionOrNotCollection(CcpJsonFieldErrorHandleType.breakFieldValidation){

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			boolean hasNoError = this.hasError(json, field, type) == false;
			
			if(hasNoError) {
				return "";
			}
			
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldTypeArray.class);
			String fieldName = field.getName();
		
			if(mustBeCollection) {
				Object providedValue = this.getProvidedValue(json, field, type);
				String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is not a collection";
				return errorMessage;
			}
			
			List<Object> providedValue = json.getDynamicVersion().getAsObjectList(fieldName);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that can not be a collection";
			return errorMessage;
		}

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			boolean isCollection = json.getDynamicVersion().getAsStringDecorator(fieldName).isList();
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldTypeArray.class);
			
			boolean hasError = (isCollection ^ mustBeCollection);
			return hasError;
		}

		public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldTypeArray.class);
			String fieldName = field.getName();
			String expectedType = type.name();

			if(mustBeCollection) {
				String errorMessage = "The field " + fieldName + " accepts only " + expectedType + " collection values";
				return errorMessage;
				
			}
			String errorMessage = "The field " + fieldName + " accepts " + expectedType + " value";
			return errorMessage;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			return true;
		}
	},
	
	numberInteger(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is not a integer number";
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
			String ruleExplanation = "The field " + fieldName + " accepts only integer number";
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
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
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is greater than specified value " + boundValue + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values greater than " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			return boundValue < Integer.MAX_VALUE;
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
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is less than specified value " + boundValue  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values less than " + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			return boundValue > Integer.MIN_VALUE;
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

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is not present in the allowed list " + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Double> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values that are not present in the following list: " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Object> allowedValues = this.getValidationParameter(field, type);
			boolean hasRuleExplanation = false == allowedValues.isEmpty();
			return hasRuleExplanation;
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
			String errorMessage = "The field " + fieldName + " has a value " + providedValue 
					+ " that is a collection whith a size "
			+ providedValue.size()  + " that is less than specified value " + bound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " has to be a collection values with size that can not be less than " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			return boundValue > Integer.MIN_VALUE;
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
			String errorMessage = "The field " + fieldName + " has a value " + providedValue 
					+ " that is a collection whith a size "
			+ providedValue.size()  + " that is greater than specified value " + bound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " has to be collection values with size that can not be greater than " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			return boundValue < Integer.MAX_VALUE;
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
			String errorMessage = "The field " + fieldName + " has a value " + providedValue 
					+ " that is a collection that has duplicated items";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has to be a collection that can not accept duplicated items";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			return true;
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
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Integer value = annotation.minLength();
			return (T)value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String providedValue = json.getDynamicVersion().getAsString(fieldName);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue 
					+ " that is a string whith a length "
			+ providedValue.length()  + " that is less than specified value " + bound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " accepts string value whith a specified  minimum length " + bound + "";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			return boundValue > Integer.MIN_VALUE;
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
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Integer value = annotation.maxLength();
			return (T)value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String providedValue = json.getDynamicVersion().getAsString(fieldName);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue 
					+ " that is a string whith a length "
			+ providedValue.length()  + " that is greater than specified value " + bound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " accepts string value whith a specified  maximum length " + bound + "";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			return boundValue < Integer.MAX_VALUE;
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
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			String[] allowedValues = annotation.allowedValues();
			List<String> value = Arrays.asList(allowedValues);
			return (T)value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is not present in the allowed list " + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Double> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept values that are not present in the following list: " + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Object> allowedValues = this.getValidationParameter(field, type);
			boolean hasRuleExplanation = false == allowedValues.isEmpty();
			return hasRuleExplanation;
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
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			if(annotation == null) {
				throw new RuntimeException(field.getName() + " =  " + type);
			}
			String value = annotation.regexValidation();
			return (T)value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + 
					" that is incompatible whith the specified regular expression " + validationParameter + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			String errorMessage = "The field " + fieldName + " accepts text value that matches with a specified regular expression " + validationParameter + "";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			String validationParameter = this.getValidationParameter(field, type);
			boolean hasRuleExplanation = false == validationParameter.trim().isEmpty();
			return hasRuleExplanation;
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

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTime annotation = field.getAnnotation(CcpJsonFieldTypeTime.class);
			Integer boundValue = annotation.maxValue();
			return boundValue < Integer.MAX_VALUE;
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

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTime annotation = field.getAnnotation(CcpJsonFieldTypeTime.class);
			Integer boundValue = annotation.maxValue();
			return boundValue > Integer.MIN_VALUE;
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

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTime annotation = field.getAnnotation(CcpJsonFieldTypeTime.class);
			Integer boundValue = annotation.maxValue();
			return boundValue < Integer.MAX_VALUE;
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

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTime annotation = field.getAnnotation(CcpJsonFieldTypeTime.class);
			Integer boundValue = annotation.maxValue();
			return boundValue > Integer.MIN_VALUE;
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
			CcpJsonFieldTypeNestedJson annotation = field.getAnnotation(CcpJsonFieldTypeNestedJson.class);
			Class<?> validationClass = annotation.validationClass();
			CcpJsonRepresentation errors = CcpJsonValidatorEngine.INSTANCE.getErrors(validationClass, innerJson);
			return errors.content;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			return "";
		}

		public  Map<String, Object> getRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNestedJson annotation = field.getAnnotation(CcpJsonFieldTypeNestedJson.class);
			Class<?> validationClass = annotation.validationClass();
			CcpJsonRepresentation rulesExplanation = CcpJsonValidationRulesEngine.INSTANCE.getRulesExplanations(validationClass);
			if(rulesExplanation.isEmpty()) {
				return CcpOtherConstants.EMPTY_JSON.content;
			}
			
			CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.fields, rulesExplanation);
			return put.content;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean b = false == this.getRuleExplanation(field, type).isEmpty();
			return b;
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
		fields,
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
		String errorMessage = "The field " + fieldName + " has a value " + formattedDateTime
				+ " and this value has to be in the " + this.word + " " 
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
		String errorMessage = "The field " + fieldName + " accepts timestamp values that are at " + this.word + " " 
				+ valueFromAnnotation + " " + intervalTypeWord + " " + timeOptionsName + " the current time. "
						;
		return errorMessage;
	}
	
	abstract boolean hasError(Long enlapsedTime, Long validationParameter);
}
