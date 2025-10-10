package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.db.utils.decorators.engine.CcpEntityExpurgableOptions;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberInteger;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberUnsigned;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;
import com.ccp.json.validations.global.engine.CcpJsonValidationError;
import com.ccp.json.validations.global.engine.CcpJsonValidationRulesEngine;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

public enum CcpJsonFieldTypeError implements CcpJsonFieldName, CcpJsonFieldValidatorInterface {
	unsignedNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberUnsigned annotation = field.getAnnotation(CcpJsonFieldTypeNumberUnsigned.class);
		    Long number = annotation.maxValue();
		    String fieldName = field.getName();
		    Long value = json.getDynamicVersion().getAsLongNumber(fieldName);
		    return value > number;
		}

		Long getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberUnsigned annotation = field.getAnnotation(CcpJsonFieldTypeNumberUnsigned.class);
			Long value = annotation.maxValue();
		    return  value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is greater than specified value " + boundValue + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values greater than " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			return boundValue < Long.MAX_VALUE;
		}
	},
	unsignedNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			Long value = json.getDynamicVersion().getAsLongNumber(fieldName);
		    return value < number;
		}

		
		Long getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberUnsigned annotation = field.getAnnotation(CcpJsonFieldTypeNumberUnsigned.class);
		    Long value = annotation.minValue();
 
		    return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is less than specified value " + boundValue  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values less than " + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			return boundValue >= 0;
		}
	},
	unsignedNumberExactValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			boolean hasNoRuleExplanation = false == this.hasRuleExplanation(field, type);
			
			if(hasNoRuleExplanation) {
				return false;
			}
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
		    Long value = json.getDynamicVersion().getAsLongNumber(fieldName);
		    return value != number;
		}

		Long getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumberUnsigned annotation = field.getAnnotation(CcpJsonFieldTypeNumberUnsigned.class);
		    Long value = annotation.exactValue();
		    return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is different to specified value " + boundValue  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values different to " + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			return boundValue > Long.MIN_VALUE;
		}
	},
	unsignedNumberAllowed(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			List<Long> allowedValues = this.getValidationParameter(field, type);
			
			boolean doNotValidate = allowedValues.isEmpty();
			
			if(doNotValidate) {
				return false;
			}
			
		    String fieldName = field.getName();
			Long value = json.getDynamicVersion().getAsLongNumber(fieldName);
			boolean isAllowed = allowedValues.contains(value);
			return isAllowed;
		}

		List<Long> getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberUnsigned annotation = field.getAnnotation(CcpJsonFieldTypeNumberUnsigned.class);
		    long[] allowedValues = annotation.allowedValues();
			List<Long> list = new ArrayList<>();
			for (long value : allowedValues) {
				list.add(value);
			}
			return list;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is not present in the allowed list " + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values that are not present in the following list: " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> allowedValues = this.getValidationParameter(field, type);
			boolean hasRuleExplanation = false == allowedValues.isEmpty();
			return hasRuleExplanation;
		}
	},
	longNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberInteger annotation = field.getAnnotation(CcpJsonFieldTypeNumberInteger.class);
		    Long number = annotation.maxValue();
		    String fieldName = field.getName();
		    Long value = json.getDynamicVersion().getAsLongNumber(fieldName);
		    return value > number;
		}

		Long getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberInteger annotation = field.getAnnotation(CcpJsonFieldTypeNumberInteger.class);
			Long value = annotation.maxValue();
		    return  value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is greater than specified value " + boundValue + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values greater than " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			return boundValue < Long.MAX_VALUE;
		}
	},
	longNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			Long value = json.getDynamicVersion().getAsLongNumber(fieldName);
		    return value < number;
		}

		
		Long getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberInteger annotation = field.getAnnotation(CcpJsonFieldTypeNumberInteger.class);
		    Long value = annotation.minValue();
 
		    return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is less than specified value " + boundValue  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values less than " + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			return boundValue > Long.MIN_VALUE;
		}
	},
	longNumberExactValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
		    Long value = json.getDynamicVersion().getAsLongNumber(fieldName);
		    return value != number;
		}

		
		Long getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumberInteger annotation = field.getAnnotation(CcpJsonFieldTypeNumberInteger.class);
		    Long value = annotation.exactValue();
 
		    return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is different to specified value " + boundValue  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values different to " + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			return boundValue > Long.MIN_VALUE;
		}
	},
	longNumberAllowed(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			List<Long> allowedValues = this.getValidationParameter(field, type);
			
			boolean doNotValidate = allowedValues.isEmpty();
			
			if(doNotValidate) {
				return false;
			}
			
		    String fieldName = field.getName();
			Long value = json.getDynamicVersion().getAsLongNumber(fieldName);
			boolean isAllowed = allowedValues.contains(value);
			return isAllowed;
		}

		List<Long> getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberInteger annotation = field.getAnnotation(CcpJsonFieldTypeNumberInteger.class);
		    long[] allowedValues = annotation.allowedValues();
			List<Long> list = new ArrayList<>();
			for (long value : allowedValues) {
				list.add(value);
			}
			return list;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is not present in the allowed list " + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values that are not present in the following list: " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> allowedValues = this.getValidationParameter(field, type);
			boolean hasRuleExplanation = false == allowedValues.isEmpty();
			return hasRuleExplanation;
		}
	},

	doubleNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    double number = annotation.maxValue();
		    String fieldName = field.getName();
			Double value = json.getDynamicVersion().getAsDoubleNumber(fieldName);
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
			return boundValue < Double.MAX_VALUE;
		}
	},
	doubleNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    Integer number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			Double value = json.getDynamicVersion().getAsDoubleNumber(fieldName);
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
			return boundValue > Double.MIN_VALUE;
		}
	},
	doubleNumberExactValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			Double number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			Double value = json.getDynamicVersion().getAsDoubleNumber(fieldName);
		    return value != number;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    Double value = annotation.exactValue();
 
		    return (T) value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has a value " + providedValue + " that is different to specified value " + boundValue  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " can not accept numeric values different to " + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			return boundValue > Double.MIN_VALUE;
		}
	},
	doubleNumberAllowed(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			List<Double> allowedValues = this.getValidationParameter(field, type);
			
			boolean doNotValidate = allowedValues.isEmpty();
			
			if(doNotValidate) {
				return false;
			}
			
		    String fieldName = field.getName();
			Double value = json.getDynamicVersion().getAsDoubleNumber(fieldName);
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
			Collection<?> value = json.getDynamicVersion().getAsObjectList(fieldName);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			return validationParameter > size;
		}

		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			Integer value = annotation.minSize();
			return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			List<Object> providedValue = json.getDynamicVersion().getAsObjectList(fieldName);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue 
					+ " that is a collection whith a size "
			+ providedValue.size()  + " that is less than specified value " + bound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " has to be a collection values with size that can not be less than " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			return boundValue > Integer.MIN_VALUE;
		}
	},
	
	arrayExactSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			Collection<?> value = json.getDynamicVersion().getAsObjectList(fieldName);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			return validationParameter != size;
		}

		
		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			Integer value = annotation.exactSize();
			return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			List<Object> providedValue = json.getDynamicVersion().getAsObjectList(fieldName);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue 
					+ " that is a collection whith a size "
			+ providedValue.size()  + " that is different to specified value " + bound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " has to be a collection values with size that can not be different to " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			return boundValue > Integer.MIN_VALUE;
		}
	},

	arrayMaxSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			Collection<?> value = json.getDynamicVersion().getAsObjectList(fieldName);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			return validationParameter < size;
		}

		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			Integer value = annotation.maxSize();
			return value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			List<Object> providedValue = json.getDynamicVersion().getAsObjectList(fieldName);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue 
					+ " that is a collection whith a size "
			+ providedValue.size()  + " that is greater than specified value " + bound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String ruleExplanation =  "The field " + fieldName + " has to be collection values with size that can not be greater than " + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			return boundValue < Integer.MAX_VALUE;
		}
	},
	arrayNonReapeted(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			Collection<?> value = json.getDynamicVersion().getAsObjectList(fieldName);
			Set<?> set = new HashSet<>(value);
			int size = set.size();
			int size2 = value.size();
			return size != size2;
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
	stringMinLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsString(fieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			return length < validationParameter;
		}

		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Integer value = annotation.minLength();
			return value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
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
			return boundValue > 0;
		}
	},
	stringExactLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    boolean noRules = false == this.hasRuleExplanation(field, type);
			if(noRules) {
		    	return false;
		    }
			String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsString(fieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			return length != validationParameter;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			if(annotation == null) {
				System.out.println();
			}
			Integer value = annotation.exactLength();
			return (T)value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			int bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String providedValue = json.getDynamicVersion().getAsString(fieldName);
			String errorMessage = "The field " + fieldName + " has a value " + providedValue 
					+ " that is a string whith a length "
			+ providedValue.length()  + " that is different to specified value " + bound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " accepts string value whith a specified exact length " + bound + "";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			return boundValue > Integer.MIN_VALUE;
		}
	},
	stringMaxLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsString(fieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			return length > validationParameter;
		}

		
		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Integer value = annotation.maxLength();
			return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
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
	stringAllowedValues(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,   Field field, CcpJsonFieldType type) {
			List<String> validationParameter = this.getValidationParameter(field, type);
			boolean doNotValidate = validationParameter.isEmpty();
			
			if(doNotValidate) {
				return false;
			}
			
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsString(fieldName);
			
			boolean notContains = false == validationParameter.contains(value);
			
			return notContains;
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
	stringRegex(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			String validationParameter = this.getValidationParameter(field, type);
			boolean doNotValidate = validationParameter.trim().isEmpty();
			if(doNotValidate) {
				return false;
			}
		    String fieldName = field.getName();
			String value = json.getDynamicVersion().getAsString(fieldName);
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
	
	stringNotEmpty(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " must contain a not empty string";
			return errorMessage;
		}

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			boolean allowsEmptyString = annotation.allowsEmptyString();
			if(allowsEmptyString) {
				return false;
			}
			boolean x = stringMinLength.hasError(json, field, type);
			if(x) {
				return false;
			}
			String fieldName = field.getName();
			boolean empty = json.getDynamicVersion().getAsString(fieldName).isEmpty();
			return empty;
		}

		public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String ruleExplanation = "The field " + fieldName + " must contain a not empty string";
			return ruleExplanation;
		}
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean x = stringMinLength.hasRuleExplanation(field, type);
			if(x) {
				return false;
			}
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			boolean allowsEmptyString = annotation.allowsEmptyString();
			return false == allowsEmptyString;
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
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.maxValue();
			return boundValue < Integer.MAX_VALUE;
		}
	},
	timeExactValueBeforeCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.exact.hasError(json, field, TimeOptions.before);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.exact.getErrorMessage(json, field, TimeOptions.before);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.exact.getRuleExplanation(field, TimeOptions.before);
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.maxValue();
			return boundValue != Integer.MAX_VALUE;
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
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
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
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.maxValue();
			return boundValue < Integer.MAX_VALUE;
		}
	},
	timeExactValueAfterCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.exact.hasError(json, field, TimeOptions.after);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.exact.getErrorMessage(json, field, TimeOptions.after);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.exact.getRuleExplanation(field, TimeOptions.after);
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.maxValue();
			return boundValue != Integer.MAX_VALUE;
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
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
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
			try {
				CcpJsonValidatorEngine.INSTANCE.validateJson(validationClass, innerJson, fieldName);
				return CcpOtherConstants.EMPTY_JSON.content;
			} catch (CcpJsonValidationError e) {
				return e.json.content;
			}
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			return "";
		}

		public  Map<String, Object> getRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNestedJson annotation = field.getAnnotation(CcpJsonFieldTypeNestedJson.class);
			Class<?> validationClass = annotation.validationClass();
			CcpJsonRepresentation rulesExplanation = CcpJsonValidationRulesEngine.INSTANCE.getRulesExplanation(validationClass);
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
	}, 
	emptyJson(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String errorMessage = "The field " + fieldName + " has to be a not empty json";
			return errorMessage;
		}

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNestedJson annotation = field.getAnnotation(CcpJsonFieldTypeNestedJson.class);
			
			boolean allowsEmptyJson = annotation.allowsEmptyJson();
			
			if(allowsEmptyJson) {
				return false;
			}
			
			String fieldName = field.getName();
			
			boolean notEmptyJson = false == json.getDynamicVersion().getInnerJson(fieldName).isEmpty();
			
			if(notEmptyJson) {
				return false;
			}
			
			return true;
		}

		public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
			String errorMessage = this.getErrorMessage(CcpOtherConstants.EMPTY_JSON, field, type);
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNestedJson annotation = field.getAnnotation(CcpJsonFieldTypeNestedJson.class);
			boolean allowsEmptyJson = annotation.allowsEmptyJson();
			
			if(allowsEmptyJson) {
				return false;
			}
			return true;
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
		CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
		long enlapsedInterval = (enlapsedTime / annotation.intervalType().milliseconds) + 1;
		return enlapsedInterval;
	}
}

enum TimeValueExtractorFromAnnotation{
	max("maximum") {
		int getValueFromAnnotation(CcpJsonFieldTypeTimeBefore annotation) {
			int value = annotation.maxValue();
			return value;
		}

		boolean hasError(Long enlapsedTime, Long validationParameter) {
			return enlapsedTime > validationParameter;
		}

	},
	exact("exact") {
		int getValueFromAnnotation(CcpJsonFieldTypeTimeBefore annotation) {
			int value = annotation.exactValue();
			return value;
		}

		boolean hasError(Long enlapsedTime, Long validationParameter) {
			return enlapsedTime != validationParameter;
		}

	},
	min("minimum") {
		int getValueFromAnnotation(CcpJsonFieldTypeTimeBefore annotation) {
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

	abstract int getValueFromAnnotation(CcpJsonFieldTypeTimeBefore annotation); 
	
	protected Long getValueFromAnnotationInMilliseconds(CcpJsonRepresentation json, Field field) {
		CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
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
		
		CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
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
		
		CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
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
