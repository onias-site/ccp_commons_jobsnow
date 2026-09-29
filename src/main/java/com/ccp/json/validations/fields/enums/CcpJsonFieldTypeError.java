package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberInteger;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberUnsigned;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;
import com.ccp.json.validations.global.engine.CcpJsonValidationError;
import com.ccp.json.validations.global.engine.CcpJsonValidationRulesEngine;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

/**
 * Extensive catalog of type-specific constraint validators (numbers, strings, arrays,
 * timestamps, nested JSON). Each constant validates one specific constraint by reading the parameters
 * of the corresponding annotation.
 */
public enum CcpJsonFieldTypeError implements CcpJsonFieldName, CcpJsonFieldValidatorInterface {
	unsignedNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberUnsigned annotation = field.getAnnotation(CcpJsonFieldTypeNumberUnsigned.class);
		    Long number = annotation.maxValue();
		    String fieldName = field.getName();
		    CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		    Long value = json.getAsLongNumber(ccpFieldName);
		    boolean isGreaterThanMax = value > number;
		    return isGreaterThanMax;
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
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is greater than specified value ";
			String messageWithBound = messageBeforeBound + boundValue;
			String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values greater than ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue < Long.MAX_VALUE;
			return isBoundConfigured;
		}
	},
	unsignedNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Long value = json.getAsLongNumber(ccpFieldName);
		    boolean isLessThanMin = value < number;
		    return isLessThanMin;
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
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is less than specified value ";
			String messageWithBound = messageBeforeBound + boundValue;
			String errorMessage = messageWithBound  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values less than ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue >= 0;
			return isBoundConfigured;
		}
	},
	unsignedNumberExactValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = this.hasRuleExplanation(field, type);
			boolean hasNoRuleExplanation = false == hasRuleExplanation;
			
			if(hasNoRuleExplanation) {
				return false;
			}
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
		    CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		    Long value = json.getAsLongNumber(ccpFieldName);
		    boolean matchesExactValue = value.equals(number);
		    boolean differsFromExactValue = false == matchesExactValue;
		    return differsFromExactValue;
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
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is different to specified value ";
			String messageWithBound = messageBeforeBound + boundValue;
			String errorMessage = messageWithBound  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values different to ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue > Long.MIN_VALUE;
			return isBoundConfigured;
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
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Long value = json.getAsLongNumber(ccpFieldName);
			   boolean contains = allowedValues.contains(value);
			   boolean isNotAllowed = false == contains;
			return isNotAllowed;
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
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is not present in the allowed list ";
			String errorMessage = messageBeforeBound + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values that are not present in the following list: ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> allowedValues = this.getValidationParameter(field, type);
			boolean allowedValuesEmpty = allowedValues.isEmpty();
			boolean hasRuleExplanation = false == allowedValuesEmpty;
			return hasRuleExplanation;
		}
	},
	longNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberInteger annotation = field.getAnnotation(CcpJsonFieldTypeNumberInteger.class);
		    Long number = annotation.maxValue();
		    String fieldName = field.getName();
		    CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		    Long value = json.getAsLongNumber(ccpFieldName);
		    boolean isGreaterThanMax = value > number;
		    return isGreaterThanMax;
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
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is greater than specified value ";
			String messageWithBound = messageBeforeBound + boundValue;
			String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values greater than ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue < Long.MAX_VALUE;
			return isBoundConfigured;
		}
	},
	longNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Long value = json.getAsLongNumber(ccpFieldName);
		    boolean isLessThanMin = value < number;
		    return isLessThanMin;
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
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is less than specified value ";
			String messageWithBound = messageBeforeBound + boundValue;
			String errorMessage = messageWithBound  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values less than ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue > Long.MIN_VALUE;
			return isBoundConfigured;
		}
	},
	longNumberExactValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = this.hasRuleExplanation(field, type);
			boolean hasNoRuleExplanation = false == hasRuleExplanation;

			if(hasNoRuleExplanation) {
				return false;
			}
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
		    CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		    Long value = json.getAsLongNumber(ccpFieldName);
		    boolean matchesExactValue = value.equals(number);
		    boolean differsFromExactValue = false == matchesExactValue;
		    return differsFromExactValue;
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
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is different to specified value ";
			String messageWithBound = messageBeforeBound + boundValue;
			String errorMessage = messageWithBound  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values different to ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue > Long.MIN_VALUE;
			return isBoundConfigured;
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
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Long value = json.getAsLongNumber(ccpFieldName);
			boolean contains = allowedValues.contains(value);
			boolean isNotAllowed = false == contains;
			return isNotAllowed;
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
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is not present in the allowed list ";
			String errorMessage = messageBeforeBound + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values that are not present in the following list: ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> allowedValues = this.getValidationParameter(field, type);
			boolean allowedValuesEmpty = allowedValues.isEmpty();
			boolean hasRuleExplanation = false == allowedValuesEmpty;
			return hasRuleExplanation;
		}
	},

	doubleNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    double number = annotation.maxValue();
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Double value = json.getAsDoubleNumber(ccpFieldName);
		    boolean isGreaterThanMax = value > number;
		    return isGreaterThanMax;
		}

		@SuppressWarnings("unchecked")
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    Double value = annotation.maxValue();
		    T typedValue = (T) value;
		    return typedValue;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is greater than specified value ";
			String messageWithBound = messageBeforeBound + boundValue;
			String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values greater than ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue < Double.MAX_VALUE;
			return isBoundConfigured;
		}
	},
	doubleNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    Double number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Double value = json.getAsDoubleNumber(ccpFieldName);
		    boolean isLessThanMin = value < number;
		    return isLessThanMin;
		}

		Double getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    Double value = annotation.minValue();
 
		    return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is less than specified value ";
			String messageWithBound = messageBeforeBound + boundValue;
			String errorMessage = messageWithBound  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values less than ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue > Double.MIN_VALUE;
			return isBoundConfigured;
		}
	},
	doubleNumberExactValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = this.hasRuleExplanation(field, type);
			boolean hasNoRuleExplanation = false == hasRuleExplanation;
			if(hasNoRuleExplanation) {
				return false;
			}
			
			Double number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Double value = json.getAsDoubleNumber(ccpFieldName);
		    boolean matchesExactValue = value.equals(number);
		    boolean differsFromExactValue = false == matchesExactValue;
		    return differsFromExactValue;
		}

		Double getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    Double value = annotation.exactValue();
 
		    return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is different to specified value ";
			String messageWithBound = messageBeforeBound + boundValue;
			String errorMessage = messageWithBound  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values different to ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue > Double.MIN_VALUE;
			return isBoundConfigured;
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
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Double value = json.getAsDoubleNumber(ccpFieldName);
			boolean contains = allowedValues.contains(value);
			boolean isNotAllowed = false == contains;
			return isNotAllowed;
		}

		@SuppressWarnings("unchecked")
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    double[] allowedValues = annotation.allowedValues();
			List<Double> list = new ArrayList<>();
			for (double value : allowedValues) {
				list.add(value);
			}
			T typedValue = (T) list;
			return typedValue;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is not present in the allowed list ";
			String errorMessage = messageBeforeBound + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Double> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept numeric values that are not present in the following list: ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Object> allowedValues = this.getValidationParameter(field, type);
			boolean allowedValuesEmpty = allowedValues.isEmpty();
			boolean hasRuleExplanation = false == allowedValuesEmpty;
			return hasRuleExplanation;
		}
	},
	arrayMinSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Collection<?> value = json.getAsObjectList(ccpFieldName);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			boolean isSmallerThanMinSize = validationParameter > size;
			return isSmallerThanMinSize;
		}

		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			Integer value = annotation.minSize();
			return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			List<Object> providedValue = json.getAsObjectList(ccpFieldName);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeProvidedSize = messageWithProvidedValue 
					+ " that is a collection whith a size ";
					int providedValueSize = providedValue.size();
					String messageWithProvidedSize = messageBeforeProvidedSize
					+ providedValueSize;
					String messageBeforeBound = messageWithProvidedSize  + " that is less than specified value ";
					String messageWithBound = messageBeforeBound + bound;
					String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " has to be a collection values with size that can not be less than ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue > Integer.MIN_VALUE;
			return isBoundConfigured;
		}
	},
	
	arrayExactSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			Collection<?> value = json.getAsObjectList(ccpFieldName);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			boolean hasExactSize = validationParameter > Integer.MIN_VALUE;
			boolean differsFromExactSize = hasExactSize && validationParameter != size;
			return differsFromExactSize;
		}

		
		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			Integer value = annotation.exactSize();
			return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			List<Object> providedValue = json.getAsObjectList(ccpFieldName);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeProvidedSize = messageWithProvidedValue 
					+ " that is a collection whith a size ";
					int providedValueSize = providedValue.size();
					String messageWithProvidedSize = messageBeforeProvidedSize
					+ providedValueSize;
					String messageBeforeBound = messageWithProvidedSize  + " that is different to specified value ";
					String messageWithBound = messageBeforeBound + bound;
					String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " has to be a collection values with size that can not be different to ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue > Integer.MIN_VALUE;
			return isBoundConfigured;
		}
	},

	arrayMaxSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Collection<?> value = json.getAsObjectList(ccpFieldName);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			boolean isLargerThanMaxSize = validationParameter < size;
			return isLargerThanMaxSize;
		}

		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			Integer value = annotation.maxSize();
			return value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			List<Object> providedValue = json.getAsObjectList(ccpFieldName);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeProvidedSize = messageWithProvidedValue 
					+ " that is a collection whith a size ";
					int providedValueSize = providedValue.size();
					String messageWithProvidedSize = messageBeforeProvidedSize
					+ providedValueSize;
					String messageBeforeBound = messageWithProvidedSize  + " that is greater than specified value ";
					String messageWithBound = messageBeforeBound + bound;
					String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " has to be collection values with size that can not be greater than ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue < Integer.MAX_VALUE;
			return isBoundConfigured;
		}
	},
	arrayNonReapeted(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			var nonRepeatedItems = annotation.nonRepeatedItems();
			boolean repeatedItemsAllowed = false == nonRepeatedItems;
			if(repeatedItemsAllowed) {
				return false;
			}
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			Collection<?> value = json.getAsObjectList(ccpFieldName);
			Set<?> set = new HashSet<>(value);
			int distinctItemsCount = set.size();
			int itemsCount = value.size();
			boolean hasDuplicatedItems = distinctItemsCount != itemsCount;
			return hasDuplicatedItems;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			List<Object> providedValue = json.getAsObjectList(ccpFieldName);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String errorMessage = messageWithProvidedValue 
					+ " that is a collection that has duplicated items";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String ruleExplanation = fieldLabel + " has to be a collection that can not accept duplicated items";
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			return true;
		}
	},
	stringMinLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   String value = json.getAsString(ccpFieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			boolean isShorterThanMin = length < validationParameter;
			return isShorterThanMin;
		}

		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Integer value = annotation.minLength();
			return value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			String providedValue = json.getAsString(ccpFieldName);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeProvidedLength = messageWithProvidedValue 
					+ " that is a string whith a length ";
					int providedValueLength = providedValue.length();
					String messageWithProvidedLength = messageBeforeProvidedLength
					+ providedValueLength;
					String messageBeforeBound = messageWithProvidedLength  + " that is less than specified value ";
					String messageWithBound = messageBeforeBound + bound;
					String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " accepts string value whith a specified  minimum length ";
			String explanationWithBound = explanationBeforeBound + bound;
			String ruleExplanation = explanationWithBound + "";
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue > 0;
			return isBoundConfigured;
		}
	},
	stringExactLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = this.hasRuleExplanation(field, type);
    boolean noRules = false == hasRuleExplanation;
			if(noRules) {
		    	return false;
		    }
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			String value = json.getAsString(ccpFieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			boolean differsFromExactLength = length != validationParameter;
			return differsFromExactLength;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			boolean annotationIsMissing = annotation == null;
			if(annotationIsMissing) {
			}
			Integer value = annotation.exactLength();
			T typedValue = (T)value;
			return typedValue;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			int bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			String providedValue = json.getAsString(ccpFieldName);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeProvidedLength = messageWithProvidedValue 
					+ " that is a string whith a length ";
					int providedValueLength = providedValue.length();
					String messageWithProvidedLength = messageBeforeProvidedLength
					+ providedValueLength;
					String messageBeforeBound = messageWithProvidedLength  + " that is different to specified value ";
					String messageWithBound = messageBeforeBound + bound;
					String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " accepts string value whith a specified exact length ";
			String explanationWithBound = explanationBeforeBound + bound;
			String ruleExplanation = explanationWithBound + "";
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue > Integer.MIN_VALUE;
			return isBoundConfigured;
		}
	},
	stringMaxLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   String value = json.getAsString(ccpFieldName);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			boolean isLongerThanMax = length > validationParameter;
			return isLongerThanMax;
		}

		
		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Integer value = annotation.maxLength();
			return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			String providedValue = json.getAsString(ccpFieldName);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeProvidedLength = messageWithProvidedValue 
					+ " that is a string whith a length ";
					int providedValueLength = providedValue.length();
					String messageWithProvidedLength = messageBeforeProvidedLength
					+ providedValueLength;
					String messageBeforeBound = messageWithProvidedLength  + " that is greater than specified value ";
					String messageWithBound = messageBeforeBound + bound;
					String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " accepts string value whith a specified  maximum length ";
			String explanationWithBound = explanationBeforeBound + bound;
			String ruleExplanation = explanationWithBound + "";
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean isBoundConfigured = boundValue < Integer.MAX_VALUE;
			return isBoundConfigured;
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
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   String value = json.getAsString(ccpFieldName);
			   boolean isAllowedValue = validationParameter.contains(value);

			   boolean isNotAllowedValue = false == isAllowedValue;
			
			return isNotAllowedValue;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Class[] allowedValuesEnum = annotation.allowedValuesEnum();
			LinkedHashSet<String> set = new LinkedHashSet<String>();
			for (Class enumClass : allowedValuesEnum) {
				try {
					Method method = enumClass.getDeclaredMethod("values");
					var enumValues = method.invoke(null);
					Enum<?>[] enums = (Enum<?>[])enumValues;
					for (Enum<?> enumConstant : enums) {
						String name = enumConstant.name();
						set.add(name);
					}
				} catch (Exception e) {

				}
			}
			List<String> list = new ArrayList<>(set);
			T typedValue = (T)list;
			return typedValue;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + " that is not present in the allowed list ";
			String errorMessage = messageBeforeBound + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Double> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " can not accept values that are not present in the following list: ";
			String ruleExplanation =  explanationBeforeBound + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Object> allowedValues = this.getValidationParameter(field, type);
			boolean allowedValuesEmpty = allowedValues.isEmpty();
			boolean hasRuleExplanation = false == allowedValuesEmpty;
			return hasRuleExplanation;
		}

	},
	stringRegex(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			String validationParameter = this.getValidationParameter(field, type);
			String trimmedRegex = validationParameter.trim();
			boolean doNotValidate = trimmedRegex.isEmpty();
			if(doNotValidate) {
				return false;
			}
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   String value = json.getAsString(ccpFieldName);
			   boolean matchesRegex = value.matches(validationParameter);
			   boolean doesNotMatchRegex = false == matchesRegex;
			return doesNotMatchRegex;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			boolean annotationIsMissing = annotation == null;
			if(annotationIsMissing) {
				String fieldName = field.getName();
				String fieldNameWithSeparator = fieldName + " =  ";
				String missingAnnotationDescription = fieldNameWithSeparator + type;
				CcpErrorJsonFieldTypeMissingStringAnnotation missingAnnotationError = new CcpErrorJsonFieldTypeMissingStringAnnotation(missingAnnotationDescription);
				throw missingAnnotationError;
			}
			String value = annotation.regexValidation();
			T typedValue = (T)value;
			return typedValue;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageBeforeBound = messageWithProvidedValue + 
					" that is incompatible whith the specified regular expression ";
					String messageWithBound = messageBeforeBound + validationParameter;
					String errorMessage = messageWithBound + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeBound = fieldLabel + " accepts text value that matches with a specified regular expression ";
			String explanationWithBound = explanationBeforeBound + validationParameter;
			String ruleExplanation = explanationWithBound + "";
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			String validationParameter = this.getValidationParameter(field, type);
			String trimmedRegex = validationParameter.trim();
			boolean regexIsEmpty = trimmedRegex.isEmpty();
			boolean hasRuleExplanation = false == regexIsEmpty;
			return hasRuleExplanation;
		}
	},
	
	stringNotEmpty(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String errorMessage = fieldLabel + " must contain a not empty string";
			return errorMessage;
		}

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			boolean allowsEmptyString = annotation.allowsEmptyString();
			if(allowsEmptyString) {
				return false;
			}
			boolean violatesMinLength = stringMinLength.hasError(json, field, type);
			if(violatesMinLength) {
				return false;
			}
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			String asString = json.getAsString(ccpFieldName);
			boolean empty = asString.isEmpty();
			return empty;
		}

		public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String ruleExplanation = fieldLabel + " must contain a not empty string";
			return ruleExplanation;
		}
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean hasMinLengthRule = stringMinLength.hasRuleExplanation(field, type);
			if(hasMinLengthRule) {
				return false;
			}
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			boolean allowsEmptyString = annotation.allowsEmptyString();
			boolean emptyStringForbidden = false == allowsEmptyString;
			return emptyStringForbidden;
		}
		
	},
	stringJavaClass(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Boolean isJavaClass = this.getValidationParameter(field, type);
			boolean doNotValidate = false == isJavaClass;
			if(doNotValidate) {
				return false;
			}
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			String value = json.getAsString(ccpFieldName);
			boolean classWasFound = CcpJsonFieldTypeError.existsInClassLoader(value);
			boolean classWasNotFound = false == classWasFound;
			return classWasNotFound;
		}

		Boolean getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Boolean value = annotation.isJavaClass();
			return value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String messageWithReason = messageWithProvidedValue + " that is not the complete name of a java class that the class loader is able to find";
			String errorMessage = messageWithReason + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String explanationUpToClassName = fieldLabel + " does not accept free text: it accepts only the complete name of a java class, ";
			String explanationUpToPackage = explanationUpToClassName + "package included, exactly as it is returned by Class.getName(). ";
			String explanationUpToClassLoader = explanationUpToPackage + "The value is accepted only if the class loader of the running application is able to find a class with that name, ";
			String explanationUpToRefusal = explanationUpToClassLoader + "so names that are misspelled, that belong to a class that was renamed or removed, or that are not in the classpath are refused";
			String ruleExplanation = explanationUpToRefusal + "";
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Boolean isJavaClass = this.getValidationParameter(field, type);
			return isJavaClass;
		}
	},
	timeMaxValueBeforeCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.max.hasError(json, field, TimeOptions._before);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.max.getErrorMessage(json, field, TimeOptions._before);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String ruleExplanation = TimeValueExtractorFromAnnotation.max.getRuleExplanation(field, TimeOptions._before);
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = TimeValueExtractorFromAnnotation.max.hasRuleExplanation(field, TimeOptions._before);
			return hasRuleExplanation;
		}
	},
	timeExactValueBeforeCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = this.hasRuleExplanation(field, type);
			boolean hasNoRuleExplanation = false == hasRuleExplanation;
			if(hasNoRuleExplanation) {
				return false;
			}
			boolean hasError = TimeValueExtractorFromAnnotation.exact.hasError(json, field, TimeOptions._before);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.exact.getErrorMessage(json, field, TimeOptions._before);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String ruleExplanation = TimeValueExtractorFromAnnotation.exact.getRuleExplanation(field, TimeOptions._before);
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = TimeValueExtractorFromAnnotation.exact.hasRuleExplanation(field, TimeOptions._before);
			return hasRuleExplanation;
		}
	},
	timeMinValueBeforeCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.min.hasError(json, field, TimeOptions._before);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.min.getErrorMessage(json, field, TimeOptions._before);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String ruleExplanation = TimeValueExtractorFromAnnotation.min.getRuleExplanation(field, TimeOptions._before);
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = TimeValueExtractorFromAnnotation.min.hasRuleExplanation(field, TimeOptions._before);
			return hasRuleExplanation;
		}
	},
	timeMaxValueAfterCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.max.hasError(json, field, TimeOptions._after);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.max.getErrorMessage(json, field, TimeOptions._after);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String ruleExplanation = TimeValueExtractorFromAnnotation.max.getRuleExplanation(field, TimeOptions._after);
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = TimeValueExtractorFromAnnotation.max.hasRuleExplanation(field, TimeOptions._after);
			return hasRuleExplanation;
		}
	},
	timeExactValueAfterCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.exact.hasError(json, field, TimeOptions._after);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.exact.getErrorMessage(json, field, TimeOptions._after);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String ruleExplanation = TimeValueExtractorFromAnnotation.exact.getRuleExplanation(field, TimeOptions._after);
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = TimeValueExtractorFromAnnotation.exact.hasRuleExplanation(field, TimeOptions._after);
			return hasRuleExplanation;
		}
	},
	timeMinValueAfterCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			boolean hasError = TimeValueExtractorFromAnnotation.min.hasError(json, field, TimeOptions._after);
			
			return hasError;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String errorMessage = TimeValueExtractorFromAnnotation.min.getErrorMessage(json, field, TimeOptions._after);
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String ruleExplanation = TimeValueExtractorFromAnnotation.min.getRuleExplanation(field, TimeOptions._after);
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean hasRuleExplanation = TimeValueExtractorFromAnnotation.min.hasRuleExplanation(field, TimeOptions._after);
			return hasRuleExplanation;
		}
	},
	nestedJson(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Map<String, Object> errors = this.getError(json, field, type);
			boolean errorsEmpty = errors.isEmpty();
			boolean hasErrors = false == errorsEmpty;
			return hasErrors;
		}
		
		public Map<String, Object> getError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			CcpJsonRepresentation innerJson = json.getInnerJson(ccpFieldName);
			CcpJsonFieldTypeNestedJson annotation = field.getAnnotation(CcpJsonFieldTypeNestedJson.class);
			Class<?> validationClass = annotation.jsonValidation();
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
			Class<?> validationClass = annotation.jsonValidation();
			CcpJsonRepresentation rulesExplanation = CcpJsonValidationRulesEngine.INSTANCE.getRulesExplanation(validationClass);
			boolean rulesExplanationEmpty = rulesExplanation.isEmpty();
			if(rulesExplanationEmpty) {
				return CcpOtherConstants.EMPTY_JSON.content;
			}
			
			CcpJsonRepresentation jsonWithFields = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.fields, rulesExplanation);
			return jsonWithFields.content;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			var ruleExplanation = this.getRuleExplanation(field, type);
			var ruleExplanationEmpty = ruleExplanation.isEmpty();
			boolean hasRuleExplanation = false == ruleExplanationEmpty;
			return hasRuleExplanation;
		}
	}, 
	emptyJson(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String errorMessage = fieldLabel + " has to be a not empty json";
			return errorMessage;
		}

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNestedJson annotation = field.getAnnotation(CcpJsonFieldTypeNestedJson.class);
			
			boolean allowsEmptyJson = annotation.allowsEmptyJson();
			
			if(allowsEmptyJson) {
				return false;
			}
			
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			CcpJsonRepresentation innerJson = json.getInnerJson(ccpFieldName);
			boolean innerJsonEmpty = innerJson.isEmpty();

			boolean notEmptyJson = false == innerJsonEmpty;
			
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
	
	private static boolean existsInClassLoader(String className) {
		ClassLoader classLoader = CcpJsonFieldTypeError.class.getClassLoader();
		boolean doNotInitializeTheClass = false;
		try {
			Class.forName(className, doNotInitializeTheClass, classLoader);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	protected final Object getProvidedValue(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {

		String fieldName = field.getName();
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		Object value = json.get(ccpFieldName);

		return value;
	}


	enum JsonFieldNames implements CcpJsonFieldName {
		fields,
	}

	@SuppressWarnings("serial")
	private static class CcpErrorJsonFieldTypeMissingStringAnnotation extends RuntimeException {
		private CcpErrorJsonFieldTypeMissingStringAnnotation(String message) {
			super(message);
		}
	}
}
