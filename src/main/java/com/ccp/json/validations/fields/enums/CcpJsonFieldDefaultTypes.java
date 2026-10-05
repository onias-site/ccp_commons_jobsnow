package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldPrimaryKey;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorRequired;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeBoolean;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberInteger;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberUnsigned;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeAfter;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;
import com.ccp.decorators.CcpStringDecorator;

/** Catalog of the default field types of the validation framework. Each constant knows how to tell whether a value is compatible with the type, whether the field declares the type, and which validators (rules) apply to it. */
public enum CcpJsonFieldDefaultTypes implements CcpJsonFieldType {
	/** Presence of the required fields; any value is compatible. Its only rule is {@code requiredFieldIsMissing}. */
	Required{
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}

		/**
		 * Tells whether a required field is missing.
		 * @param json the JSON being validated
		 * @param field the field
		 * @param context the validation context
		 * @return {@code true} when the field is required and missing
		 */
		public boolean hasErrors(CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {
			boolean hasError = CcpJsonFieldError.requiredFieldIsMissing.hasError(json, field, this);
			return hasError;
		}
		
		/**
		 * The only rule of this type: {@code requiredFieldIsMissing}.
		 * @return the rules
		 */
		public  List<CcpJsonFieldValidatorInterface> getDefaultValidations() {
			return Arrays.asList(CcpJsonFieldError.requiredFieldIsMissing);
		}

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			
			boolean annotationRequiredIsPresent = field.isAnnotationPresent(CcpJsonFieldValidatorRequired.class);
			
			if(annotationRequiredIsPresent) {
				return true;
			}
			boolean hasAnnotationPrimaryKey = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);
			
			if(hasAnnotationPrimaryKey) {
				return true;
			}

			
			return false;
		}
		
		/**
		 * Adds the error of a missing required field to the accumulated errors.
		 * @param errors the accumulated errors
		 * @param json the JSON being validated
		 * @param field the field
		 * @param context the validation context
		 * @return the updated errors
		 */
		public CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {
			CcpJsonRepresentation errors2 = CcpJsonFieldError.requiredFieldIsMissing.getErrors(errors, json, field, this);
			return errors2;
		}

	},
	
	/** Boolean ({@code @CcpJsonFieldTypeBoolean}): the text of the value must be {@code true} or {@code false}, any case. */
	Boolean{
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getAsStringDecorator(new CcpFieldName(fieldName)).isBoolean();
		}

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeBoolean.class);
			return annotationPresent;
		}
	}, 
	/** Collection ({@code @CcpJsonFieldValidatorArray}): the value must be a JSON list; rules on size and repeated items. */
	Array(CcpJsonFieldTypeError.arrayMinSize, CcpJsonFieldTypeError.arrayExactSize, CcpJsonFieldTypeError.arrayMaxSize, CcpJsonFieldTypeError.arrayNonReapeted){
		
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getAsStringDecorator(new CcpFieldName(fieldName)).isList();
		}

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
			return annotationPresent;
		}
		
	},
	/** Nested JSON ({@code @CcpJsonFieldTypeNestedJson}): the value must be a JSON object; it is validated by its own class. */
	NestedJson(CcpJsonFieldTypeError.nestedJson, CcpJsonFieldTypeError.emptyJson){
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getAsStringDecorator(new CcpFieldName(fieldName)).isInnerJson();
		}

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNestedJson.class);
			return annotationPresent;
		}
	}, 
	/** Decimal number ({@code @CcpJsonFieldTypeNumber}); rules on bounds and allowed values. */
	Number(CcpJsonFieldTypeError.doubleNumberMaxValue, CcpJsonFieldTypeError.doubleNumberMinValue, CcpJsonFieldTypeError.doubleNumberExactValue, CcpJsonFieldTypeError.doubleNumberAllowed){
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getAsStringDecorator(new CcpFieldName(fieldName)).isDoubleNumber() ;
		}

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNumber.class);
			return annotationPresent;
		}
	}, 
	/** Integer number ({@code @CcpJsonFieldTypeNumberInteger}), a decimal ending in .0 being accepted; rules on bounds and allowed values. */
	NumberInteger(CcpJsonFieldTypeError.longNumberMaxValue, CcpJsonFieldTypeError.longNumberMinValue, CcpJsonFieldTypeError.longNumberExactValue, CcpJsonFieldTypeError.longNumberAllowed){
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getAsStringDecorator(new CcpFieldName(fieldName)).isLongNumber() ;
		}

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNumberInteger.class);
			return annotationPresent;
		}
	}, 

	/** Non-negative integer ({@code @CcpJsonFieldTypeNumberUnsigned}); rules on bounds and allowed values. */
	NumberUnsigned(CcpJsonFieldTypeError.unsignedNumberMaxValue, CcpJsonFieldTypeError.unsignedNumberMinValue, CcpJsonFieldTypeError.unsignedNumberExactValue, CcpJsonFieldTypeError.unsignedNumberAllowed){
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> {
				CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
				CcpStringDecorator asStringDecorator = json.getAsStringDecorator(ccpFieldName);
				var longNumber = asStringDecorator.isLongNumber();
				boolean isNotLongNumber = false == longNumber;

				if(isNotLongNumber) {
					return false;
				}
				CcpFieldName ccpFieldName2 = new CcpFieldName(fieldName);

				Long asLongNumber = json.getAsLongNumber(ccpFieldName2);
				boolean isNotNegative = asLongNumber >= 0;
				return isNotNegative;
			} ;
		}

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeNumberUnsigned.class);
			return annotationPresent;
		}

	}, 
	/** Text ({@code @CcpJsonFieldTypeString}); any value is compatible; rules on emptiness, length, pattern, allowed values and java class names. */
	String(CcpJsonFieldTypeError.stringNotEmpty, CcpJsonFieldTypeError.stringExactLength, CcpJsonFieldTypeError.stringRegex, CcpJsonFieldTypeError.stringAllowedValues, CcpJsonFieldTypeError.stringMaxLength, CcpJsonFieldTypeError.stringMinLength, CcpJsonFieldTypeError.stringJavaClass){
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> true;
		}
		

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeString.class);
			return annotationPresent;
		}
	}, 
	/** Past timestamp in milliseconds ({@code @CcpJsonFieldTypeTimeBefore}); rules on how long ago it is, in the declared granularity. */
	TimeBeforeCurrentDate(CcpJsonFieldTypeError.timeMaxValueBeforeCurrentTime, CcpJsonFieldTypeError.timeExactValueBeforeCurrentTime, CcpJsonFieldTypeError.timeMinValueBeforeCurrentTime){
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getAsStringDecorator(new CcpFieldName(fieldName)).isLongNumber();
		}

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeTimeBefore.class);
			return annotationPresent;
		}
	},
	/** Future timestamp in milliseconds ({@code @CcpJsonFieldTypeTimeAfter}); rules on how far ahead it is, in the declared granularity. */
	TimeAfterCurrentDate(CcpJsonFieldTypeError.timeMaxValueAfterCurrentTime, CcpJsonFieldTypeError.timeExactValueAfterCurrentTime, CcpJsonFieldTypeError.timeMinValueAfterCurrentTime){
		/**
		 * Returns the check that tells whether the value of the field is compatible with this type (see the description of the constant).
		 * @param fieldName the field name
		 * @return the compatibility check over the JSON
		 */
		public Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName) {
			return json -> json.getAsStringDecorator(new CcpFieldName(fieldName)).isLongNumber();
		}

		/**
		 * Tells whether the field declares this type (through its annotation).
		 * @param field the field
		 * @return {@code true} when the type annotation is present
		 */
		public boolean hasRuleExplanation(Field field) {
			boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeTimeAfter.class);
			return annotationPresent;
		}
	},
	
	;
	/** The rules specific to this type. */
	private final CcpJsonFieldValidatorInterface[] errorTypes;
	
	/**
	 * Associates the type with its specific rules.
	 * @param errorTypes the rules of the type
	 */
	private CcpJsonFieldDefaultTypes(CcpJsonFieldValidatorInterface... errorTypes) {
		this.errorTypes = errorTypes;
	}
	/**
	 * Returns the rules specific to this type.
	 * @return the rules
	 */
	public CcpJsonFieldValidatorInterface[] getErrorTypes() {
		return errorTypes;
	}

}
