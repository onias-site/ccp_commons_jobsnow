package com.ccp.json.validations.fields.interfaces;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.enums.CcpJsonFieldError;
import com.ccp.json.validations.fields.enums.CcpJsonFieldsValidationContext;

/**
 * Central contract of the field types of the JSON validation: type compatibility, accumulation of errors and
 * explanation of the rules.
 */
public interface CcpJsonFieldType {

	/**
	 * Returns the check that tells whether the value of the field is compatible with the type.
	 * @param fieldName the field name
	 * @return the compatibility check
	 */
	abstract Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName);

	
	/**
	 * Tells whether the field breaks any rule of the type. An absent field has no errors; validations outside the context
	 * or not configured for the field (see {@code hasRuleExplanation} of each validation) are skipped.
	 * @param json the JSON being validated
	 * @param field the field
	 * @param context the validation context
	 * @return {@code true} at the first broken rule
	 */
	default boolean hasErrors(CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {
		java.lang.String fieldName = field.getName();
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		boolean containsAllFields = json.containsAllFields(ccpFieldName);

		boolean thisFieldIsAbsent = false == containsAllFields;
		
		if(thisFieldIsAbsent) {
			return false;
		}
		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		for (CcpJsonFieldValidatorInterface validation : validations) {
			boolean validValidationContext = validation.isValidValidationContext(context);
		
			boolean isNotValidValidationContext = false == validValidationContext;
			
			if(isNotValidValidationContext) {
				continue;
			}
			boolean ruleExplanation2 = validation.hasRuleExplanation(field, this);

			boolean hasNoRules = false == ruleExplanation2;
			
			if(hasNoRules) {
				continue;
			}
			
			boolean hasError = validation.hasError(json, field, this);
			if(hasError) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds to the errors every rule of the type that the field breaks. An absent field has no errors. Unlike
	 * {@link #hasErrors}, a validation is skipped only when the field does not declare the type, not when the validation
	 * itself is not configured.
	 * @param errors the accumulated errors
	 * @param json the JSON being validated
	 * @param field the field
	 * @param context the validation context
	 * @return the updated errors
	 */
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {

		java.lang.String fieldName = field.getName();
		CcpFieldName ccpFieldName2 = new CcpFieldName(fieldName);
		boolean containsAllFields2 = json.containsAllFields(ccpFieldName2);

		boolean fieldIsNotPresent = false == containsAllFields2;
		if(fieldIsNotPresent) {
			return errors;
		}

		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		
		for (CcpJsonFieldValidatorInterface errorType : validations) {
			boolean validValidationContext2 = errorType.isValidValidationContext(context);
			boolean isInvalidContextValidation = false == validValidationContext2;
			
			if(isInvalidContextValidation) {
				continue;
			}
			boolean ruleExplanation3 = this.hasRuleExplanation(field);

			boolean hasNoRules = false == ruleExplanation3;
			if(hasNoRules) {
				continue;
			}
			boolean error = errorType.hasError(json, field, this);

			boolean hasNoErrors = false == error;
			if(hasNoErrors) {
				continue;
			}
			errors = errorType.getErrors(errors, json, field, this);
		}
		
		return errors;
	}

	/**
	 * Adds the explanations of the rules of the type configured for the field, when the field declares the type.
	 * @param ruleExplanation the accumulated explanations
	 * @param field the field
	 * @return the updated explanations
	 */
	default CcpJsonRepresentation updateRuleExplanation(CcpJsonRepresentation ruleExplanation, Field field) {
		boolean ruleExplanation4 = this.hasRuleExplanation(field);
		boolean hasNoRulesExplanations = false == ruleExplanation4;
		if(hasNoRulesExplanations) {
			return ruleExplanation;
		}
		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		
		for (CcpJsonFieldValidatorInterface errorType : validations) {
			ruleExplanation = errorType.updateRuleExplanation(ruleExplanation, field, this);
		}
		return ruleExplanation;
	}

	/**
	 * Returns the default validations followed by the validations specific to the type.
	 * @param field the field
	 * @return the validations
	 */
	private List<CcpJsonFieldValidatorInterface> getAllValidations(Field field) {
		
		List<CcpJsonFieldValidatorInterface> validations = this.getDefaultValidations();
		CcpJsonFieldValidatorInterface[] errorTypes2 = this.getErrorTypes();
		List<CcpJsonFieldValidatorInterface> errorTypes = Arrays.asList(errorTypes2);
		validations.addAll(errorTypes);
		
		return validations;
	}

	/**
	 * Returns the validations every type shares: {@code incompatibleType} and {@code validateCollectionOrSigleValue}.
	 * @return a new modifiable list of the default validations
	 */
	default List<CcpJsonFieldValidatorInterface> getDefaultValidations(){
		List<CcpJsonFieldValidatorInterface> asList = new ArrayList<>(Arrays.asList(CcpJsonFieldError.incompatibleType, CcpJsonFieldError.validateCollectionOrSigleValue));
		return asList;
	}
	/**
	 * Returns the validations specific to the type.
	 * @return the specific validations
	 */
	CcpJsonFieldValidatorInterface[] getErrorTypes();

	/**
	 * Tells whether the field declares this type.
	 * @param field the field
	 * @return {@code true} when the field declares the type
	 */
	abstract boolean hasRuleExplanation(Field field);

	/**
	 * Returns the name of the type.
	 * @return the type name
	 */
	String name();

}
