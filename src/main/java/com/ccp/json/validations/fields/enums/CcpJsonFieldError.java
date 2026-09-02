package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldPrimaryKey;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorRequired;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;

/**
 * Validadores transversais de campo: tipo incompatível, campo obrigatório ausente e conflito
 * coleção/valor único.
 * Constantes: {@code incompatibleType} (breakFieldValidation), {@code requiredFieldIsMissing}
 * (continueFieldValidation), {@code validateCollectionOrSigleValue} (breakFieldValidation).
 */
public enum CcpJsonFieldError implements CcpJsonFieldName, CcpJsonFieldValidatorInterface {
	
	incompatibleType(CcpJsonFieldErrorHandleType.breakFieldValidation) {

		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Object value = json.getAsObject(ccpFieldName);
			   boolean valueIgual = value == null;

			   if (valueIgual) {
				return false;
			}

			Predicate<CcpJsonRepresentation> evaluateCorrectType = type.evaluateCompatibleType(fieldName);
			boolean test = evaluateCorrectType.test(json);
			boolean incompatibleType = false == test;
			
			if(incompatibleType) {
				return true;
			}
			return false;
		}
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			CcpFieldName ccpFieldName2 = new CcpFieldName(fieldName);
			var get = json.get(ccpFieldName2);
			var getClass = get.getClass();
			String providedType = getClass.getName();
			String expectedType = type.name();
			boolean isArray = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			
			if(isArray) {
				String valorMais = "The field " + fieldName;
				String valorMaisMais = valorMais + " must be a collection ";
				String valorMaisMaisMais = valorMaisMais + expectedType;
				String valorMaisMaisMaisMais = valorMaisMaisMais + " but some item in this collection is the ";
				String valorMaisMaisMaisMaisMais = valorMaisMaisMaisMais+ providedType;
				String valorMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMais + " type";
				return valorMaisMaisMaisMaisMaisMais;
			}
			String valorMais2 = "The field " + fieldName;
			String valorMais2Mais = valorMais2 + " must be a ";
			String valorMais2MaisMais = valorMais2Mais + expectedType;
			String valorMais2MaisMaisMais = valorMais2MaisMais + " type, but this field is ";
			String valorMais2MaisMaisMaisMais = valorMais2MaisMaisMais + providedType;
			String valorMais2MaisMaisMaisMaisMais = valorMais2MaisMaisMaisMais + " type";
			return valorMais2MaisMaisMaisMaisMais;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean isArray = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			
			if(isArray) {
				return "";
			}
			
			String fieldName = field.getName();
			String expectedType = type.name();
			String valorMais3 = "The field " + fieldName;
			String valorMais3Mais = valorMais3 + " must be ";
			String valorMais3MaisMais = valorMais3Mais + expectedType;
			String valorMais3MaisMaisMais = valorMais3MaisMais + " type";
			return valorMais3MaisMaisMais;
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
			CcpFieldName ccpFieldName3 = new CcpFieldName(fieldName);
			boolean containsAllFields = json.containsAllFields(ccpFieldName3);
			boolean thisFieldIsNotPresent = false == containsAllFields;
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
			String valorMais4 = "The field " + fieldName;
			String errorMessage = valorMais4 + " is missing";
			return errorMessage;
		}

		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String valorMais5 = "The field " + fieldName;
			String ruleExplanation = valorMais5 + " is required";
			return ruleExplanation;
		}
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean notValidated = this.isNotValidated(field);
			boolean valorIgual = false == notValidated;
			return valorIgual;
		}
	},
	
	validateCollectionOrSigleValue(CcpJsonFieldErrorHandleType.breakFieldValidation){
		
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			boolean error = this.hasError(json, field, type);
			boolean hasNoError = false == error;
			
			if(hasNoError) {
				return "";
			}
			
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			String fieldName = field.getName();
		
			if(mustBeCollection) {
				Object providedValue = this.getProvidedValue(json, field, type);
				String valorMais6 = "The field " + fieldName;
				String valorMais6Mais = valorMais6 + " has a value ";
				String valorMais6MaisMais = valorMais6Mais + providedValue;
				String errorMessage = valorMais6MaisMais + " that is not a collection";
				return errorMessage;
			}
			CcpFieldName ccpFieldName4 = new CcpFieldName(fieldName);

			List<Object> providedValue = json.getAsObjectList(ccpFieldName4);
			String valorMais7 = "The field " + fieldName;
			String valorMais7Mais = valorMais7 + " has a value ";
			String valorMais7MaisMais = valorMais7Mais + providedValue;
			String errorMessage = valorMais7MaisMais + " that can not be a collection";
			return errorMessage;
		}

		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			String fieldName = field.getName();
			CcpFieldName ccpFieldName5 = new CcpFieldName(fieldName);
			CcpStringDecorator asStringDecorator = json.getAsStringDecorator(ccpFieldName5);
			boolean isCollection = asStringDecorator.isList();
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			boolean isCollectionResultado = isCollection ^ mustBeCollection;

			boolean hasError = (isCollectionResultado);
			return hasError;
		}

		public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			String fieldName = field.getName();
			String expectedType = type.name();

			if(mustBeCollection) {
				String valorMais8 = "The field " + fieldName;
				String valorMais8Mais = valorMais8 + " accepts only ";
				String valorMais8MaisMais = valorMais8Mais + expectedType;
				String errorMessage = valorMais8MaisMais + " collection values";
				return errorMessage;
				
			}
			String valorMais9 = "The field " + fieldName;
			String valorMais9Mais = valorMais9 + " accepts ";
			String valorMais9MaisMais = valorMais9Mais + expectedType;
			String errorMessage = valorMais9MaisMais + " value";
			return errorMessage;
		}
		
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean arrayEquals = CcpJsonFieldDefaultTypes.Array.equals(type);
			if(arrayEquals){
				return false;
			}
			return true;
		}

		public boolean isValidValidationContext(CcpJsonFieldsValidationContext context) {
			boolean singleEquals = CcpJsonFieldsValidationContext.single.equals(context);
			return singleEquals;
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
		CcpFieldName ccpFieldName6 = new CcpFieldName(fieldName);
		Object value = json.get(ccpFieldName6);

		return value;
	}
}
