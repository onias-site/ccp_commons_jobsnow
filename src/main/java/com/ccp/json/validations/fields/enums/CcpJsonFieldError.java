package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpDynamicJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldPrimaryKey;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorRequired;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;

public enum CcpJsonFieldError implements CcpJsonFieldName, CcpJsonFieldValidatorInterface {
	
	incompatibleType(CcpJsonFieldErrorHandleType.breakFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			
		    String fieldName = field.getName();
			Object value = json.getDynamicVersion().getAsObject(fieldName);

			if (value == null) {
				return false;
			}

			Predicate<CcpJsonRepresentation> evaluateCorrectType = type.evaluateCompatibleType(fieldName);
			boolean incompatibleType = false == evaluateCorrectType.test(json);
			
			if(incompatibleType) {
				return true;
			}
			return false;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String providedType = json.getDynamicVersion().get(fieldName).getClass().getName();
			String expectedType = type.name();
			boolean isArray = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			
			if(isArray) {
				return "The field " + fieldName + " must be a collection " + expectedType + " but some item in this collection is the "+ providedType + " type";
			}
			return "The field " + fieldName + " must be a " + expectedType + " type, but this field is " + providedType + " type";
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean isArray = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			
			if(isArray) {
				return "";
			}
			
			String fieldName = field.getName();
			String expectedType = type.name();
			return "The field " + fieldName + " must be " + expectedType + " type";
		}

		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {

			return true;
		}
	},
	requiredFieldIsMissing(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			
			boolean notValidated = this.isNotValidated(field);
			
			if(notValidated) {
				return false;
			}
			
			
			String fieldName = field.getName();
			boolean thisFieldIsNotPresent = false == json.getDynamicVersion().containsAllFields(fieldName);
			return thisFieldIsNotPresent;
		}

		private boolean isNotValidated(Field field) {
			boolean hasNoAnnotationRequired = field.isAnnotationPresent(CcpJsonFieldValidatorRequired.class);
			
			if(hasNoAnnotationRequired) {
				return false;
			}

			boolean hasNoAnnotationPrimaryKey = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);
			
			if(hasNoAnnotationPrimaryKey) {
				return false;
			}
			
			return true;
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
			boolean notValidated = this.isNotValidated(field);
			return false == notValidated;
		}
	},
	
	validateCollectionOrSigleValue(CcpJsonFieldErrorHandleType.breakFieldValidation){
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			boolean hasNoError = false == this.hasError(json, field, type);
			
			if(hasNoError) {
				return "";
			}
			
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
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
			CcpDynamicJsonRepresentation dynamicVersion = json.getDynamicVersion();
			CcpStringDecorator asStringDecorator = dynamicVersion.getAsStringDecorator(fieldName);
			boolean isCollection = asStringDecorator.isList();
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			
			boolean hasError = (isCollection ^ mustBeCollection);
			return hasError;
		}

		public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
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
			if(CcpJsonFieldDefaultTypes.Array.equals(type)){
				return false;
			}
			return true;
		}

		public boolean isValidValidationContext(CcpJsonFieldsValidationContext context) {
			return CcpJsonFieldsValidationContext.single.equals(context);
		}
	},
	

	;
	
	private CcpJsonFieldError(CcpJsonFieldErrorHandleType handleType) {
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
}
