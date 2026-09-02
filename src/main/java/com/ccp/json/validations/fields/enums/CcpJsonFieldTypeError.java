package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberInteger;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberUnsigned;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;
import com.ccp.json.validations.global.engine.CcpJsonValidationRulesEngine;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;
import com.ccp.json.validations.global.engine.CcpJsonValidationError;

/**
 * Catálogo extenso de validadores de restrições específicas por tipo (números, strings, arrays,
 * timestamps, JSON aninhado). Cada constante valida uma restrição específica lendo os parâmetros
 * da anotação correspondente.
 */
public enum CcpJsonFieldTypeError implements CcpJsonFieldName, CcpJsonFieldValidatorInterface {
	unsignedNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNumberUnsigned annotation = field.getAnnotation(CcpJsonFieldTypeNumberUnsigned.class);
		    Long number = annotation.maxValue();
		    String fieldName = field.getName();
		    CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		    Long value = json.getAsLongNumber(ccpFieldName);
		    boolean valueMaior = value > number;
		    return valueMaior;
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
			String valorMais = "The field " + fieldName;
			String valorMaisMais = valorMais + " has a value ";
			String valorMaisMaisMais = valorMaisMais + providedValue;
			String valorMaisMaisMaisMais = valorMaisMaisMais + " that is greater than specified value ";
			String valorMaisMaisMaisMaisMais = valorMaisMaisMaisMais + boundValue;
			String errorMessage = valorMaisMaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais2 = "The field " + fieldName;
			String valorMais2Mais = valorMais2 + " can not accept numeric values greater than ";
			String ruleExplanation =  valorMais2Mais + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean boundValueMenor = boundValue < Long.MAX_VALUE;
			return boundValueMenor;
		}
	},
	unsignedNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName2 = new CcpFieldName(fieldName);
			   Long value = json.getAsLongNumber(ccpFieldName2);
		    boolean valueMenor = value < number;
		    return valueMenor;
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
			String valorMais3 = "The field " + fieldName;
			String valorMais3Mais = valorMais3 + " has a value ";
			String valorMais3MaisMais = valorMais3Mais + providedValue;
			String valorMais3MaisMaisMais = valorMais3MaisMais + " that is less than specified value ";
			String valorMais3MaisMaisMaisMais = valorMais3MaisMaisMais + boundValue;
			String errorMessage = valorMais3MaisMaisMaisMais  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais4 = "The field " + fieldName;
			String valorMais4Mais = valorMais4 + " can not accept numeric values less than ";
			String ruleExplanation =  valorMais4Mais + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaiorOuIgual = boundValue >= 0;
			return boundValueMaiorOuIgual;
		}
	},
	unsignedNumberExactValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			boolean ruleExplanation2 = this.hasRuleExplanation(field, type);
			boolean hasNoRuleExplanation = false == ruleExplanation2;
			
			if(hasNoRuleExplanation) {
				return false;
			}
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
		    CcpFieldName ccpFieldName3 = new CcpFieldName(fieldName);
		    Long value = json.getAsLongNumber(ccpFieldName3);
		    boolean valueDiferente = value != number;
		    return valueDiferente;
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
			String valorMais5 = "The field " + fieldName;
			String valorMais5Mais = valorMais5 + " has a value ";
			String valorMais5MaisMais = valorMais5Mais + providedValue;
			String valorMais5MaisMaisMais = valorMais5MaisMais + " that is different to specified value ";
			String valorMais5MaisMaisMaisMais = valorMais5MaisMaisMais + boundValue;
			String errorMessage = valorMais5MaisMaisMaisMais  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais6 = "The field " + fieldName;
			String valorMais6Mais = valorMais6 + " can not accept numeric values different to ";
			String ruleExplanation =  valorMais6Mais + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaior = boundValue > Long.MIN_VALUE;
			return boundValueMaior;
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
			   CcpFieldName ccpFieldName4 = new CcpFieldName(fieldName);
			   Long value = json.getAsLongNumber(ccpFieldName4);
			   boolean contains = allowedValues.contains(value);
			   boolean isAllowed = false == contains;
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
			String valorMais7 = "The field " + fieldName;
			String valorMais7Mais = valorMais7 + " has a value ";
			String valorMais7MaisMais = valorMais7Mais + providedValue;
			String valorMais7MaisMaisMais = valorMais7MaisMais + " that is not present in the allowed list ";
			String errorMessage = valorMais7MaisMaisMais + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais8 = "The field " + fieldName;
			String valorMais8Mais = valorMais8 + " can not accept numeric values that are not present in the following list: ";
			String ruleExplanation =  valorMais8Mais + boundValue;
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
		    CcpFieldName ccpFieldName5 = new CcpFieldName(fieldName);
		    Long value = json.getAsLongNumber(ccpFieldName5);
		    boolean valueMaior2 = value > number;
		    return valueMaior2;
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
			String valorMais9 = "The field " + fieldName;
			String valorMais9Mais = valorMais9 + " has a value ";
			String valorMais9MaisMais = valorMais9Mais + providedValue;
			String valorMais9MaisMaisMais = valorMais9MaisMais + " that is greater than specified value ";
			String valorMais9MaisMaisMaisMais = valorMais9MaisMaisMais + boundValue;
			String errorMessage = valorMais9MaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais10 = "The field " + fieldName;
			String valorMais10Mais = valorMais10 + " can not accept numeric values greater than ";
			String ruleExplanation =  valorMais10Mais + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean boundValueMenor2 = boundValue < Long.MAX_VALUE;
			return boundValueMenor2;
		}
	},
	longNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName6 = new CcpFieldName(fieldName);
			   Long value = json.getAsLongNumber(ccpFieldName6);
		    boolean valueMenor2 = value < number;
		    return valueMenor2;
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
			String valorMais11 = "The field " + fieldName;
			String valorMais11Mais = valorMais11 + " has a value ";
			String valorMais11MaisMais = valorMais11Mais + providedValue;
			String valorMais11MaisMaisMais = valorMais11MaisMais + " that is less than specified value ";
			String valorMais11MaisMaisMaisMais = valorMais11MaisMaisMais + boundValue;
			String errorMessage = valorMais11MaisMaisMaisMais  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais12 = "The field " + fieldName;
			String valorMais12Mais = valorMais12 + " can not accept numeric values less than ";
			String ruleExplanation =  valorMais12Mais + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaior2 = boundValue > Long.MIN_VALUE;
			return boundValueMaior2;
		}
	},
	longNumberExactValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			Long number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
		    CcpFieldName ccpFieldName7 = new CcpFieldName(fieldName);
		    Long value = json.getAsLongNumber(ccpFieldName7);
		    boolean valueDiferente2 = value != number;
		    return valueDiferente2;
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
			String valorMais13 = "The field " + fieldName;
			String valorMais13Mais = valorMais13 + " has a value ";
			String valorMais13MaisMais = valorMais13Mais + providedValue;
			String valorMais13MaisMaisMais = valorMais13MaisMais + " that is different to specified value ";
			String valorMais13MaisMaisMaisMais = valorMais13MaisMaisMais + boundValue;
			String errorMessage = valorMais13MaisMaisMaisMais  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais14 = "The field " + fieldName;
			String valorMais14Mais = valorMais14 + " can not accept numeric values different to ";
			String ruleExplanation =  valorMais14Mais + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Long boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaior3 = boundValue > Long.MIN_VALUE;
			return boundValueMaior3;
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
			   CcpFieldName ccpFieldName8 = new CcpFieldName(fieldName);
			   Long value = json.getAsLongNumber(ccpFieldName8);
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
			String valorMais15 = "The field " + fieldName;
			String valorMais15Mais = valorMais15 + " has a value ";
			String valorMais15MaisMais = valorMais15Mais + providedValue;
			String valorMais15MaisMaisMais = valorMais15MaisMais + " that is not present in the allowed list ";
			String errorMessage = valorMais15MaisMaisMais + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais16 = "The field " + fieldName;
			String valorMais16Mais = valorMais16 + " can not accept numeric values that are not present in the following list: ";
			String ruleExplanation =  valorMais16Mais + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Long> allowedValues = this.getValidationParameter(field, type);
			boolean allowedValuesEmpty2 = allowedValues.isEmpty();
			boolean hasRuleExplanation = false == allowedValuesEmpty2;
			return hasRuleExplanation;
		}
	},

	doubleNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    double number = annotation.maxValue();
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName9 = new CcpFieldName(fieldName);
			   Double value = json.getAsDoubleNumber(ccpFieldName9);
		    boolean valueMaior3 = value > number;
		    return valueMaior3;
		}

		@SuppressWarnings("unchecked")
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
		    CcpJsonFieldTypeNumber annotation = field.getAnnotation(CcpJsonFieldTypeNumber.class);
		    Double value = annotation.maxValue();
		    T t = (T) value;
		    return t;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String fieldName = field.getName();
			String valorMais17 = "The field " + fieldName;
			String valorMais17Mais = valorMais17 + " has a value ";
			String valorMais17MaisMais = valorMais17Mais + providedValue;
			String valorMais17MaisMaisMais = valorMais17MaisMais + " that is greater than specified value ";
			String valorMais17MaisMaisMaisMais = valorMais17MaisMaisMais + boundValue;
			String errorMessage = valorMais17MaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais18 = "The field " + fieldName;
			String valorMais18Mais = valorMais18 + " can not accept numeric values greater than ";
			String ruleExplanation =  valorMais18Mais + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			boolean boundValueMenor3 = boundValue < Double.MAX_VALUE;
			return boundValueMenor3;
		}
	},
	doubleNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    Double number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName10 = new CcpFieldName(fieldName);
			   Double value = json.getAsDoubleNumber(ccpFieldName10);
		    boolean valueMenor3 = value < number;
		    return valueMenor3;
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
			String valorMais19 = "The field " + fieldName;
			String valorMais19Mais = valorMais19 + " has a value ";
			String valorMais19MaisMais = valorMais19Mais + providedValue;
			String valorMais19MaisMaisMais = valorMais19MaisMais + " that is less than specified value ";
			String valorMais19MaisMaisMaisMais = valorMais19MaisMaisMais + boundValue;
			String errorMessage = valorMais19MaisMaisMaisMais  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais20 = "The field " + fieldName;
			String valorMais20Mais = valorMais20 + " can not accept numeric values less than ";
			String ruleExplanation =  valorMais20Mais + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaior4 = boundValue > Double.MIN_VALUE;
			return boundValueMaior4;
		}
	},
	doubleNumberExactValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			boolean ruleExplanation3 = this.hasRuleExplanation(field, type);
			boolean valorIgual = false == ruleExplanation3;
			if(valorIgual) {
				return false;
			}
			
			Double number = this.getValidationParameter(field, type);
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName11 = new CcpFieldName(fieldName);
			   Double value = json.getAsDoubleNumber(ccpFieldName11);
		    boolean valueDiferente3 = value != number;
		    return valueDiferente3;
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
			String valorMais21 = "The field " + fieldName;
			String valorMais21Mais = valorMais21 + " has a value ";
			String valorMais21MaisMais = valorMais21Mais + providedValue;
			String valorMais21MaisMaisMais = valorMais21MaisMais + " that is different to specified value ";
			String valorMais21MaisMaisMaisMais = valorMais21MaisMaisMais + boundValue;
			String errorMessage = valorMais21MaisMaisMaisMais  + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais22 = "The field " + fieldName;
			String valorMais22Mais = valorMais22 + " can not accept numeric values different to ";
			String ruleExplanation =  valorMais22Mais + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Double boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaior5 = boundValue > Double.MIN_VALUE;
			return boundValueMaior5;
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
			   CcpFieldName ccpFieldName12 = new CcpFieldName(fieldName);
			   Double value = json.getAsDoubleNumber(ccpFieldName12);
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
			T t2 = (T) list;
			return t2;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String valorMais23 = "The field " + fieldName;
			String valorMais23Mais = valorMais23 + " has a value ";
			String valorMais23MaisMais = valorMais23Mais + providedValue;
			String valorMais23MaisMaisMais = valorMais23MaisMais + " that is not present in the allowed list ";
			String errorMessage = valorMais23MaisMaisMais + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Double> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais24 = "The field " + fieldName;
			String valorMais24Mais = valorMais24 + " can not accept numeric values that are not present in the following list: ";
			String ruleExplanation =  valorMais24Mais + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Object> allowedValues = this.getValidationParameter(field, type);
			boolean allowedValuesEmpty3 = allowedValues.isEmpty();
			boolean hasRuleExplanation = false == allowedValuesEmpty3;
			return hasRuleExplanation;
		}
	},
	arrayMinSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName13 = new CcpFieldName(fieldName);
			   Collection<?> value = json.getAsObjectList(ccpFieldName13);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			boolean validationParameterMaior = validationParameter > size;
			return validationParameterMaior;
		}

		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			Integer value = annotation.minSize();
			return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName14 = new CcpFieldName(fieldName);
			List<Object> providedValue = json.getAsObjectList(ccpFieldName14);
			String valorMais25 = "The field " + fieldName;
			String valorMais25Mais = valorMais25 + " has a value ";
			String valorMais25MaisMais = valorMais25Mais + providedValue;
			String valorMais25MaisMaisMais = valorMais25MaisMais 
					+ " that is a collection whith a size ";
					int providedValueSize = providedValue.size();
					String valorMais25MaisMaisMaisMais = valorMais25MaisMaisMais
					+ providedValueSize;
					String valorMais25MaisMaisMaisMaisMais = valorMais25MaisMaisMaisMais  + " that is less than specified value ";
					String valorMais25MaisMaisMaisMaisMaisMais = valorMais25MaisMaisMaisMaisMais + bound;
					String errorMessage = valorMais25MaisMaisMaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais26 = "The field " + fieldName;
			String valorMais26Mais = valorMais26 + " has to be a collection values with size that can not be less than ";
			String ruleExplanation =  valorMais26Mais + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaior6 = boundValue > Integer.MIN_VALUE;
			return boundValueMaior6;
		}
	},
	
	arrayExactSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			CcpFieldName ccpFieldName15 = new CcpFieldName(fieldName);
			Collection<?> value = json.getAsObjectList(ccpFieldName15);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			boolean validationParameterMaior2 = validationParameter > Integer.MIN_VALUE;
			boolean validationParameterMaior2E = validationParameterMaior2 && validationParameter != size;
			return validationParameterMaior2E;
		}

		
		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			Integer value = annotation.exactSize();
			return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName16 = new CcpFieldName(fieldName);
			List<Object> providedValue = json.getAsObjectList(ccpFieldName16);
			String valorMais27 = "The field " + fieldName;
			String valorMais27Mais = valorMais27 + " has a value ";
			String valorMais27MaisMais = valorMais27Mais + providedValue;
			String valorMais27MaisMaisMais = valorMais27MaisMais 
					+ " that is a collection whith a size ";
					int providedValueSize2 = providedValue.size();
					String valorMais27MaisMaisMaisMais = valorMais27MaisMaisMais
					+ providedValueSize2;
					String valorMais27MaisMaisMaisMaisMais = valorMais27MaisMaisMaisMais  + " that is different to specified value ";
					String valorMais27MaisMaisMaisMaisMaisMais = valorMais27MaisMaisMaisMaisMais + bound;
					String errorMessage = valorMais27MaisMaisMaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais28 = "The field " + fieldName;
			String valorMais28Mais = valorMais28 + " has to be a collection values with size that can not be different to ";
			String ruleExplanation =  valorMais28Mais + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaior7 = boundValue > Integer.MIN_VALUE;
			return boundValueMaior7;
		}
	},

	arrayMaxSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName17 = new CcpFieldName(fieldName);
			   Collection<?> value = json.getAsObjectList(ccpFieldName17);
			Integer validationParameter = this.getValidationParameter(field, type);
			int size = value.size();
			boolean validationParameterMenor = validationParameter < size;
			return validationParameterMenor;
		}

		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			Integer value = annotation.maxSize();
			return value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName18 = new CcpFieldName(fieldName);
			List<Object> providedValue = json.getAsObjectList(ccpFieldName18);
			String valorMais29 = "The field " + fieldName;
			String valorMais29Mais = valorMais29 + " has a value ";
			String valorMais29MaisMais = valorMais29Mais + providedValue;
			String valorMais29MaisMaisMais = valorMais29MaisMais 
					+ " that is a collection whith a size ";
					int providedValueSize3 = providedValue.size();
					String valorMais29MaisMaisMaisMais = valorMais29MaisMaisMais
					+ providedValueSize3;
					String valorMais29MaisMaisMaisMaisMais = valorMais29MaisMaisMaisMais  + " that is greater than specified value ";
					String valorMais29MaisMaisMaisMaisMaisMais = valorMais29MaisMaisMaisMaisMais + bound;
					String errorMessage = valorMais29MaisMaisMaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais30 = "The field " + fieldName;
			String valorMais30Mais = valorMais30 + " has to be collection values with size that can not be greater than ";
			String ruleExplanation =  valorMais30Mais + boundValue;
			return ruleExplanation;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean boundValueMenor4 = boundValue < Integer.MAX_VALUE;
			return boundValueMenor4;
		}
	},
	arrayNonReapeted(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    
			CcpJsonFieldValidatorArray annotation = field.getAnnotation(CcpJsonFieldValidatorArray.class);
			var nonRepeatedItems = annotation.nonRepeatedItems();
			boolean valorIgual2 = false == nonRepeatedItems;
			if(valorIgual2) {
				return false;
			}
			String fieldName = field.getName();
			CcpFieldName ccpFieldName19 = new CcpFieldName(fieldName);
			Collection<?> value = json.getAsObjectList(ccpFieldName19);
			Set<?> set = new HashSet<>(value);
			int size = set.size();
			int size2 = value.size();
			boolean sizeDiferente = size != size2;
			return sizeDiferente;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			CcpFieldName ccpFieldName20 = new CcpFieldName(fieldName);
			List<Object> providedValue = json.getAsObjectList(ccpFieldName20);
			String valorMais31 = "The field " + fieldName;
			String valorMais31Mais = valorMais31 + " has a value ";
			String valorMais31MaisMais = valorMais31Mais + providedValue;
			String errorMessage = valorMais31MaisMais 
					+ " that is a collection that has duplicated items";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String valorMais32 = "The field " + fieldName;
			String errorMessage = valorMais32 + " has to be a collection that can not accept duplicated items";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			return true;
		}
	},
	stringMinLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName21 = new CcpFieldName(fieldName);
			   String value = json.getAsString(ccpFieldName21);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			boolean lengthMenor = length < validationParameter;
			return lengthMenor;
		}

		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Integer value = annotation.minLength();
			return value;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName22 = new CcpFieldName(fieldName);
			String providedValue = json.getAsString(ccpFieldName22);
			String valorMais33 = "The field " + fieldName;
			String valorMais33Mais = valorMais33 + " has a value ";
			String valorMais33MaisMais = valorMais33Mais + providedValue;
			String valorMais33MaisMaisMais = valorMais33MaisMais 
					+ " that is a string whith a length ";
					int providedValueLength = providedValue.length();
					String valorMais33MaisMaisMaisMais = valorMais33MaisMaisMais
					+ providedValueLength;
					String valorMais33MaisMaisMaisMaisMais = valorMais33MaisMaisMaisMais  + " that is less than specified value ";
					String valorMais33MaisMaisMaisMaisMaisMais = valorMais33MaisMaisMaisMaisMais + bound;
					String errorMessage = valorMais33MaisMaisMaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais34 = "The field " + fieldName;
			String valorMais34Mais = valorMais34 + " accepts string value whith a specified  minimum length ";
			String valorMais34MaisMais = valorMais34Mais + bound;
			String errorMessage = valorMais34MaisMais + "";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaior8 = boundValue > 0;
			return boundValueMaior8;
		}
	},
	stringExactLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			boolean ruleExplanation4 = this.hasRuleExplanation(field, type);
    boolean noRules = false == ruleExplanation4;
			if(noRules) {
		    	return false;
		    }
			String fieldName = field.getName();
			CcpFieldName ccpFieldName23 = new CcpFieldName(fieldName);
			String value = json.getAsString(ccpFieldName23);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			boolean lengthDiferente = length != validationParameter;
			return lengthDiferente;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			boolean annotationIgual = annotation == null;
			if(annotationIgual) {
			}
			Integer value = annotation.exactLength();
			T t3 = (T)value;
			return t3;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			int bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName24 = new CcpFieldName(fieldName);
			String providedValue = json.getAsString(ccpFieldName24);
			String valorMais35 = "The field " + fieldName;
			String valorMais35Mais = valorMais35 + " has a value ";
			String valorMais35MaisMais = valorMais35Mais + providedValue;
			String valorMais35MaisMaisMais = valorMais35MaisMais 
					+ " that is a string whith a length ";
					int providedValueLength2 = providedValue.length();
					String valorMais35MaisMaisMaisMais = valorMais35MaisMaisMais
					+ providedValueLength2;
					String valorMais35MaisMaisMaisMaisMais = valorMais35MaisMaisMaisMais  + " that is different to specified value ";
					String valorMais35MaisMaisMaisMaisMaisMais = valorMais35MaisMaisMaisMaisMais + bound;
					String errorMessage = valorMais35MaisMaisMaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais36 = "The field " + fieldName;
			String valorMais36Mais = valorMais36 + " accepts string value whith a specified exact length ";
			String valorMais36MaisMais = valorMais36Mais + bound;
			String errorMessage = valorMais36MaisMais + "";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean boundValueMaior9 = boundValue > Integer.MIN_VALUE;
			return boundValueMaior9;
		}
	},
	stringMaxLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName25 = new CcpFieldName(fieldName);
			   String value = json.getAsString(ccpFieldName25);
			int length = value.length();
			Integer validationParameter = this.getValidationParameter(field, type);
			boolean lengthMaior = length > validationParameter;
			return lengthMaior;
		}

		
		Integer getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			Integer value = annotation.maxLength();
			return value;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			CcpFieldName ccpFieldName26 = new CcpFieldName(fieldName);
			String providedValue = json.getAsString(ccpFieldName26);
			String valorMais37 = "The field " + fieldName;
			String valorMais37Mais = valorMais37 + " has a value ";
			String valorMais37MaisMais = valorMais37Mais + providedValue;
			String valorMais37MaisMaisMais = valorMais37MaisMais 
					+ " that is a string whith a length ";
					int providedValueLength3 = providedValue.length();
					String valorMais37MaisMaisMaisMais = valorMais37MaisMaisMais
					+ providedValueLength3;
					String valorMais37MaisMaisMaisMaisMais = valorMais37MaisMaisMaisMais  + " that is greater than specified value ";
					String valorMais37MaisMaisMaisMaisMaisMais = valorMais37MaisMaisMaisMaisMais + bound;
					String errorMessage = valorMais37MaisMaisMaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer bound = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais38 = "The field " + fieldName;
			String valorMais38Mais = valorMais38 + " accepts string value whith a specified  maximum length ";
			String valorMais38MaisMais = valorMais38Mais + bound;
			String errorMessage = valorMais38MaisMais + "";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			Integer boundValue = this.getValidationParameter(field, type);
			boolean boundValueMenor5 = boundValue < Integer.MAX_VALUE;
			return boundValueMenor5;
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
			   CcpFieldName ccpFieldName27 = new CcpFieldName(fieldName);
			   String value = json.getAsString(ccpFieldName27);
			   boolean contains2 = validationParameter.contains(value);

			   boolean notContains = false == contains2;
			
			return notContains;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			String[] allowedValues = annotation.allowedValues();
			List<String> value = Arrays.asList(allowedValues);
			Class[] allowedValuesEnum = annotation.allowedValuesEnum();
			LinkedHashSet<String> set = new LinkedHashSet<String>(value);
			for (Class class1 : allowedValuesEnum) {
				try {
					Method method = class1.getDeclaredMethod("values");
					var invoke = method.invoke(null);
					Enum<?>[] enums = (Enum<?>[])invoke;
					for (Enum<?> enum1 : enums) {
						String name = enum1.name();
						set.add(name);
					}
				} catch (Exception e) {

				}
			}
			List<String> list = new ArrayList<>(set);
			T t4 = (T)list;
			return t4;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String valorMais39 = "The field " + fieldName;
			String valorMais39Mais = valorMais39 + " has a value ";
			String valorMais39MaisMais = valorMais39Mais + providedValue;
			String valorMais39MaisMaisMais = valorMais39MaisMais + " that is not present in the allowed list ";
			String errorMessage = valorMais39MaisMaisMais + validationParameter;
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Double> boundValue = this.getValidationParameter(field, type);
			String fieldName = field.getName();
			String valorMais40 = "The field " + fieldName;
			String valorMais40Mais = valorMais40 + " can not accept values that are not present in the following list: ";
			String ruleExplanation =  valorMais40Mais + boundValue;
			return ruleExplanation;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			List<Object> allowedValues = this.getValidationParameter(field, type);
			boolean allowedValuesEmpty4 = allowedValues.isEmpty();
			boolean hasRuleExplanation = false == allowedValuesEmpty4;
			return hasRuleExplanation;
		}

	},
	stringRegex(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			String validationParameter = this.getValidationParameter(field, type);
			String validationParameterTrim = validationParameter.trim();
			boolean doNotValidate = validationParameterTrim.isEmpty();
			if(doNotValidate) {
				return false;
			}
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName28 = new CcpFieldName(fieldName);
			   String value = json.getAsString(ccpFieldName28);
			   boolean matches2 = value.matches(validationParameter);
			   boolean matches = false == matches2;
			return matches;
		}

		@SuppressWarnings("unchecked")
		
		<T extends Object> T getValidationParameter(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			boolean annotationIgual2 = annotation == null;
			if(annotationIgual2) {
				String fieldName2 = field.getName();
				String fieldName2Mais = fieldName2 + " =  ";
				String fieldName2MaisMais = fieldName2Mais + type;
				CcpErrorJsonFieldTypeMissingStringAnnotation ccpErrorJsonFieldTypeMissingStringAnnotation = new CcpErrorJsonFieldTypeMissingStringAnnotation(fieldName2MaisMais);
				throw ccpErrorJsonFieldTypeMissingStringAnnotation;
			}
			String value = annotation.regexValidation();
			T t5 = (T)value;
			return t5;
		}

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			Object providedValue = this.getProvidedValue(json, field, type);
			String valorMais41 = "The field " + fieldName;
			String valorMais41Mais = valorMais41 + " has a value ";
			String valorMais41MaisMais = valorMais41Mais + providedValue;
			String valorMais41MaisMaisMais = valorMais41MaisMais + 
					" that is incompatible whith the specified regular expression ";
					String valorMais41MaisMaisMaisMais = valorMais41MaisMaisMais + validationParameter;
					String errorMessage = valorMais41MaisMaisMaisMais + "";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			Object validationParameter = this.getValidationParameter(field, type);
			String valorMais42 = "The field " + fieldName;
			String valorMais42Mais = valorMais42 + " accepts text value that matches with a specified regular expression ";
			String valorMais42MaisMais = valorMais42Mais + validationParameter;
			String errorMessage = valorMais42MaisMais + "";
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			String validationParameter = this.getValidationParameter(field, type);
			String validationParameterTrim2 = validationParameter.trim();
			boolean validationParameterTrim2Empty = validationParameterTrim2.isEmpty();
			boolean hasRuleExplanation = false == validationParameterTrim2Empty;
			return hasRuleExplanation;
		}
	},
	
	stringNotEmpty(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String valorMais43 = "The field " + fieldName;
			String errorMessage = valorMais43 + " must contain a not empty string";
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
			CcpFieldName ccpFieldName29 = new CcpFieldName(fieldName);
			String asString = json.getAsString(ccpFieldName29);
			boolean empty = asString.isEmpty();
			return empty;
		}

		public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String valorMais44 = "The field " + fieldName;
			String ruleExplanation = valorMais44 + " must contain a not empty string";
			return ruleExplanation;
		}
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean x = stringMinLength.hasRuleExplanation(field, type);
			if(x) {
				return false;
			}
			CcpJsonFieldTypeString annotation = field.getAnnotation(CcpJsonFieldTypeString.class);
			boolean allowsEmptyString = annotation.allowsEmptyString();
			boolean valorIgual3 = false == allowsEmptyString;
			return valorIgual3;
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
			String errorMessage = TimeValueExtractorFromAnnotation.max.getRuleExplanation(field, TimeOptions._before);
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.maxValue();
			boolean boundValueMenor6 = boundValue < Integer.MAX_VALUE;
			return boundValueMenor6;
		}
	},
	timeExactValueBeforeCurrentTime(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			boolean ruleExplanation5 = this.hasRuleExplanation(field, type);
			boolean valorIgual4 = false == ruleExplanation5;
			if(valorIgual4) {
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
			String errorMessage = TimeValueExtractorFromAnnotation.exact.getRuleExplanation(field, TimeOptions._before);
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.exactValue();
			boolean boundValueMenor7 = boundValue < Integer.MAX_VALUE;
			return boundValueMenor7;
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
			String errorMessage = TimeValueExtractorFromAnnotation.min.getRuleExplanation(field, TimeOptions._before);
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.maxValue();
			boolean boundValueMaior10 = boundValue > Integer.MIN_VALUE;
			return boundValueMaior10;
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
			String errorMessage = TimeValueExtractorFromAnnotation.max.getRuleExplanation(field, TimeOptions._after);
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.maxValue();
			boolean boundValueMenor8 = boundValue < Integer.MAX_VALUE;
			return boundValueMenor8;
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
			String errorMessage = TimeValueExtractorFromAnnotation.exact.getRuleExplanation(field, TimeOptions._after);
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.maxValue();
			boolean boundValueDiferente = boundValue != Integer.MAX_VALUE;
			return boundValueDiferente;
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
			String errorMessage = TimeValueExtractorFromAnnotation.min.getRuleExplanation(field, TimeOptions._after);
			return errorMessage;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			Integer boundValue = annotation.maxValue();
			boolean boundValueMaior11 = boundValue > Integer.MIN_VALUE;
			return boundValueMaior11;
		}
	},
	nestedJson(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			Map<String, Object> errors = this.getError(json, field, type);
			boolean errorsEmpty = errors.isEmpty();
			boolean hasNoErrors = false == errorsEmpty;
			return hasNoErrors;
		}
		
		public Map<String, Object> getError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			CcpFieldName ccpFieldName30 = new CcpFieldName(fieldName);
			CcpJsonRepresentation innerJson = json.getInnerJson(ccpFieldName30);
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
			
			CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.fields, rulesExplanation);
			return put.content;
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			var ruleExplanation6 = this.getRuleExplanation(field, type);
			var ruleExplanation6Empty = ruleExplanation6.isEmpty();
			boolean b = false == ruleExplanation6Empty;
			return b;
		}
	}, 
	emptyJson(CcpJsonFieldErrorHandleType.continueFieldValidation){

		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String valorMais45 = "The field " + fieldName;
			String errorMessage = valorMais45 + " has to be a not empty json";
			return errorMessage;
		}

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			CcpJsonFieldTypeNestedJson annotation = field.getAnnotation(CcpJsonFieldTypeNestedJson.class);
			
			boolean allowsEmptyJson = annotation.allowsEmptyJson();
			
			if(allowsEmptyJson) {
				return false;
			}
			
			String fieldName = field.getName();
			CcpFieldName ccpFieldName31 = new CcpFieldName(fieldName);
			CcpJsonRepresentation innerJson2 = json.getInnerJson(ccpFieldName31);
			boolean innerJson2Empty = innerJson2.isEmpty();

			boolean notEmptyJson = false == innerJson2Empty;
			
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
		CcpFieldName ccpFieldName32 = new CcpFieldName(fieldName);
		Object value = json.get(ccpFieldName32);

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
