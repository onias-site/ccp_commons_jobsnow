package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldPrimaryKey;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.json.defaultvalues.annotations.CcpJsonFieldDefaultValue;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorRequired;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

/**
 * Cross-cutting field validators: incompatible type, missing required field and
 * collection/single value conflict.
 * Constants: {@code incompatibleType} (breakFieldValidation), {@code requiredFieldIsMissing}
 * (continueFieldValidation), {@code validateCollectionOrSigleValue} (breakFieldValidation).
 */
public enum CcpJsonFieldError implements CcpJsonFieldName, CcpJsonFieldValidatorInterface {
	
	/**
	 * The value must be of the declared type of the field (for a collection, every item); breaks the other validations of
	 * the field when broken.
	 */
	incompatibleType(CcpJsonFieldErrorHandleType.breakFieldValidation) {

		/**
		 * Tells whether the value is not compatible with the declared type.
		 * @param json the JSON being validated
		 * @param field the field
		 * @param type the declared type of the field
		 * @return {@code true} when the type is incompatible
		 */
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			
		    String fieldName = field.getName();
			   CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			   Object value = json.getAsObject(ccpFieldName);
			   boolean isNull = value == null;

			   if (isNull) {
				return false;
			}

			Predicate<CcpJsonRepresentation> evaluateCorrectType = type.evaluateCompatibleType(fieldName);
			boolean matchesExpectedType = evaluateCorrectType.test(json);
			boolean hasIncompatibleType = false == matchesExpectedType;

			if(hasIncompatibleType) {
				return true;
			}
			return false;
		}
		
		/**
		 * Names the expected and the provided types.
		 * @param json the JSON being validated
		 * @param field the field
		 * @param type the declared type of the field
		 * @return the error message
		 */
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			var providedValue = json.get(ccpFieldName);
			var providedValueClass = providedValue.getClass();
			String providedType = providedValueClass.getName();
			String expectedType = type.name();
			boolean isArray = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			
			if(isArray) {
				String fieldLabel = "The field " + fieldName;
				String messageBeforeExpectedType = fieldLabel + " must be a collection ";
				String messageWithExpectedType = messageBeforeExpectedType + expectedType;
				String messageBeforeProvidedType = messageWithExpectedType + " but some item in this collection is the ";
				String messageWithProvidedType = messageBeforeProvidedType+ providedType;
				String errorMessage = messageWithProvidedType + " type";
				return errorMessage;
			}
			String fieldLabel = "The field " + fieldName;
			String messageBeforeExpectedType = fieldLabel + " must be a ";
			String messageWithExpectedType = messageBeforeExpectedType + expectedType;
			String messageBeforeProvidedType = messageWithExpectedType + " type, but this field is ";
			String messageWithProvidedType = messageBeforeProvidedType + providedType;
			String errorMessage = messageWithProvidedType + " type";
			return errorMessage;
		}

		/**
		 * Explains the expected type; nothing for collections, whose type is explained by {@code validateCollectionOrSigleValue}.
		 * @param field the field
		 * @param type the declared type of the field
		 * @return the explanation
		 */
		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean isArray = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			
			if(isArray) {
				return "";
			}
			
			String fieldName = field.getName();
			String expectedType = type.name();
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeExpectedType = fieldLabel + " must be ";
			String explanationWithExpectedType = explanationBeforeExpectedType + expectedType;
			String ruleExplanation = explanationWithExpectedType + " type";
			return ruleExplanation;
		}

		/**
		 * The type rule is always explained.
		 * @param field the field
		 * @param type the declared type of the field
		 * @return always {@code true}
		 */
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {

			return true;
		}
	},
	/**
	 * The field must be present when it is required ({@code @CcpJsonFieldValidatorRequired} or primary key) and has no
	 * default value.
	 */
	requiredFieldIsMissing(CcpJsonFieldErrorHandleType.continueFieldValidation) {

		/**
		 * Tells whether a required field is absent.
		 * @param json the JSON being validated
		 * @param field the field
		 * @param type the declared type of the field
		 * @return {@code true} when a required field is missing
		 */
		public boolean hasError(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
			
			boolean notValidated = this.isNotValidated(field);
			
			if(notValidated) {
				return false;
			}
			
			
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			boolean containsAllFields = json.containsAllFields(ccpFieldName);
			boolean thisFieldIsNotPresent = false == containsAllFields;
			return thisFieldIsNotPresent;
		}

		/**
		 * Tells whether the presence of the field is not checked: it has a default value, or it is neither required nor
		 * primary key.
		 * @param field the field
		 * @return {@code true} when the field is optional
		 */
		private boolean isNotValidated(Field field) {
			boolean hasAnnotationDefaultValue = this.hasAnnotationDefaultValue(field);

			if(hasAnnotationDefaultValue) {
				return true;
			}

			boolean hasAnnotationRequired = field.isAnnotationPresent(CcpJsonFieldValidatorRequired.class);

			if(hasAnnotationRequired) {
				return false;
			}

			boolean hasAnnotationPrimaryKey = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);

			if(hasAnnotationPrimaryKey) {
				return false;
			}

			return true;
		}

		/**
		 * A field with a default value is never required: if it is absent, the
		 * {@code CcpJsonFieldDefaultValuesEngine} fills it in. The annotation is also searched in the field from which
		 * the validations are copied, since {@code CcpJsonFieldValidatorRequired} is usually
		 * declared in the local field and {@code CcpJsonFieldDefaultValue} in the source class.
		 */
		private boolean hasAnnotationDefaultValue(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldDefaultValue.class);

			if(annotationPresent) {
				return true;
			}

			Field replacedField = CcpJsonValidatorEngine.INSTANCE.getReplacedField(field);
			boolean annotationPresentInTheReplacedField = replacedField.isAnnotationPresent(CcpJsonFieldDefaultValue.class);

			return annotationPresentInTheReplacedField;
		}

		/**
		 * Tells that the field is missing.
		 * @param json the JSON being validated
		 * @param field the field
		 * @param type the declared type of the field
		 * @return the error message
		 */
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String errorMessage = fieldLabel + " is missing";
			return errorMessage;
		}

		/**
		 * Tells that the field is required.
		 * @param field the field
		 * @param type the declared type of the field
		 * @return the explanation
		 */
		public String getRuleExplanation(Field field, CcpJsonFieldType type) {
			String fieldName = field.getName();
			String fieldLabel = "The field " + fieldName;
			String ruleExplanation = fieldLabel + " is required";
			return ruleExplanation;
		}
		/**
		 * The rule is explained only for required fields.
		 * @param field the field
		 * @param type the declared type of the field
		 * @return {@code true} when the field is required
		 */
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean notValidated = this.isNotValidated(field);
			boolean isValidated = false == notValidated;
			return isValidated;
		}
	},
	
	/**
	 * A field annotated with {@code @CcpJsonFieldValidatorArray} must hold a collection, and any other field must not;
	 * breaks the other validations of the field when broken. Checked only in the single-value context.
	 */
	validateCollectionOrSigleValue(CcpJsonFieldErrorHandleType.breakFieldValidation){
		
		/**
		 * Tells whether the value should or should not be a collection.
		 * @param json the JSON being validated
		 * @param field the field
		 * @param type the declared type of the field
		 * @return the error message, or {@code ""} when there is no error
		 */
		public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			boolean hasError = this.hasError(json, field, type);
			boolean hasNoError = false == hasError;
			
			if(hasNoError) {
				return "";
			}
			
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			String fieldName = field.getName();
		
			if(mustBeCollection) {
				Object providedValue = this.getProvidedValue(json, field, type);
				String fieldLabel = "The field " + fieldName;
				String fieldWithValueLabel = fieldLabel + " has a value ";
				String messageWithProvidedValue = fieldWithValueLabel + providedValue;
				String errorMessage = messageWithProvidedValue + " that is not a collection";
				return errorMessage;
			}
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);

			List<Object> providedValue = json.getAsObjectList(ccpFieldName);
			String fieldLabel = "The field " + fieldName;
			String fieldWithValueLabel = fieldLabel + " has a value ";
			String messageWithProvidedValue = fieldWithValueLabel + providedValue;
			String errorMessage = messageWithProvidedValue + " that can not be a collection";
			return errorMessage;
		}

		/**
		 * Tells whether the value being a JSON list disagrees with the presence of {@code @CcpJsonFieldValidatorArray}.
		 * @param json the JSON being validated
		 * @param field the field
		 * @param type the declared type of the field
		 * @return {@code true} on a mismatch
		 */
		public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
			
			String fieldName = field.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			CcpStringDecorator asStringDecorator = json.getAsStringDecorator(ccpFieldName);
			boolean isCollection = asStringDecorator.isList();
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			boolean collectionMismatch = isCollection ^ mustBeCollection;

			boolean hasError = (collectionMismatch);
			return hasError;
		}

		/**
		 * Explains whether the field accepts a collection or a single value of its type.
		 * @param field the field
		 * @param type the declared type of the field
		 * @return the explanation
		 */
		public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean mustBeCollection = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			String fieldName = field.getName();
			String expectedType = type.name();

			if(mustBeCollection) {
				String fieldLabel = "The field " + fieldName;
				String explanationBeforeExpectedType = fieldLabel + " accepts only ";
				String explanationWithExpectedType = explanationBeforeExpectedType + expectedType;
				String ruleExplanation = explanationWithExpectedType + " collection values";
				return ruleExplanation;
				
			}
			String fieldLabel = "The field " + fieldName;
			String explanationBeforeExpectedType = fieldLabel + " accepts ";
			String explanationWithExpectedType = explanationBeforeExpectedType + expectedType;
			String ruleExplanation = explanationWithExpectedType + " value";
			return ruleExplanation;
		}
		
		/**
		 * Explained for every type except {@code Array}.
		 * @param field the field
		 * @param type the declared type of the field
		 * @return {@code false} only for the {@code Array} type
		 */
		public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
			boolean arrayEquals = CcpJsonFieldDefaultTypes.Array.equals(type);
			if(arrayEquals){
				return false;
			}
			return true;
		}

		/**
		 * Applies only to the single-value context.
		 * @param context the validation context
		 * @return {@code true} for {@code single}
		 */
		public boolean isValidValidationContext(CcpJsonFieldsValidationContext context) {
			boolean singleEquals = CcpJsonFieldsValidationContext.single.equals(context);
			return singleEquals;
		}
	},
	

	;
	
	/**
	 * Associates the rule with what happens to the other validations of the field when it is broken.
	 * @param handleType the error handling strategy
	 */
	private CcpJsonFieldError(CcpJsonFieldErrorHandleType handleType) {
		this.errorHandleType = handleType;
	}

	/** What happens to the other validations of the field when this rule is broken. */
	private final CcpJsonFieldErrorHandleType errorHandleType;

	/**
	 * Returns what happens to the other validations of the field when this rule is broken.
	 * @return the error handling strategy
	 */
	public CcpJsonFieldErrorHandleType getErrorHandleType() {
		return this.errorHandleType;
	}
	
	/**
	 * Returns the value of the field in the JSON.
	 * @param json the JSON being validated
	 * @param field the field
	 * @param type the declared type of the field
	 * @return the value
	 */
	protected final Object getProvidedValue(CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {

		String fieldName = field.getName();
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		Object value = json.get(ccpFieldName);

		return value;
	}
}
