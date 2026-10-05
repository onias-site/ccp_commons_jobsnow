package com.ccp.json.validations.fields.interfaces;

import java.lang.reflect.Field;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.json.validations.fields.enums.CcpJsonFieldErrorHandleType;
import com.ccp.json.validations.fields.enums.CcpJsonFieldsValidationContext;

/**
 * Contract of the validation rules of a field: error check, message, rule explanation and accumulation of errors and
 * explanations in JSON.
 */
public interface CcpJsonFieldValidatorInterface {

	/**
	 * Tells whether the rule applies to the validation context.
	 * @param context the validation context (single value or collection item)
	 * @return {@code true} by default
	 */
	default boolean isValidValidationContext(CcpJsonFieldsValidationContext context) {
		return true;
	}

	/**
	 * What happens to the other validations of the field when this rule is broken.
	 * @return the error handling strategy
	 */
	CcpJsonFieldErrorHandleType getErrorHandleType() ;

	/**
	 * Identificador do validador.
	 * @return nome do validador
	 */
	String name();

	/**
	 * Describes the error found.
	 * @param json the JSON being validated
	 * @param field the field
	 * @param type the declared type of the field
	 * @return the error message
	 */
	String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type);

	/**
	 * Tells whether the field breaks the rule.
	 * @param json the JSON being validated
	 * @param field the field
	 * @param type the declared type of the field
	 * @return {@code true} when the rule is broken
	 */
	boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type);

	/**
	 * Explains the rule in natural language.
	 * @param field the field
	 * @param type the declared type of the field
	 * @return the explanation
	 */
	Object getRuleExplanation(Field field, CcpJsonFieldType type);

	/**
	 * Returns the error object of the field.
	 * @param json the JSON being validated
	 * @param field the field
	 * @param type the declared type of the field
	 * @return the error message by default
	 */
	default Object getError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
		String errorMessage = this.getErrorMessage(json, field, type);
		return errorMessage;
	}
	
	/**
	 * When the field breaks the rule, appends {@code {errorName, errorDescription}} to the list of errors of the field and
	 * applies the error handling strategy (which may stop the other validations of the field).
	 * @param errors the accumulated errors
	 * @param json the JSON being validated
	 * @param field the field
	 * @param type the declared type of the field
	 * @return the updated errors
	 */
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {
		boolean error2 = this.hasError(json, field, type);

		boolean hasNoError = false == error2;

		if (hasNoError) {
			return errors;
		}

		String fieldName = field.getName();
		
		Object error = this.getError(json, field, type);
		String name = this.name();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put(CcpErrorFields.errorName, name);

				CcpJsonRepresentation errorObject = put
				.put(CcpErrorFields.errorDescription, error);
				CcpFieldName ccpFieldName = new CcpFieldName(fieldName);

				CcpJsonRepresentation updatedErrors = errors.addToList(ccpFieldName, errorObject);

		CcpJsonFieldErrorHandleType errorHandleType = this.getErrorHandleType();
		
		errorHandleType.maybeBreakValidation(updatedErrors);
		
		return updatedErrors;
	}
	
	/**
	 * When the rule is configured and its explanation is not blank, appends {@code {ruleName, ruleDescription}} to the
	 * list of rules of the field.
	 * @param allRules the accumulated explanations
	 * @param field the field
	 * @param type the declared type of the field
	 * @return the updated explanations
	 */
	default CcpJsonRepresentation updateRuleExplanation(CcpJsonRepresentation allRules, Field field, CcpJsonFieldType type) {
		boolean ruleExplanation2 = this.hasRuleExplanation(field, type);

		boolean hasNoRules = false == ruleExplanation2;
		
		if(hasNoRules) {
			return allRules;
		}
		
		String fieldName = field.getName();
		
		Object ruleExplanation = this.getRuleExplanation(field, type);
		String toString = ruleExplanation.toString();
		String toStringTrim = toString.trim();
		boolean toStringTrimEmpty = toStringTrim.isEmpty();
		if(toStringTrimEmpty) {
			return allRules;
		}
		String name2 = this.name();
		CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON
				.put(RuleFields.ruleName, name2);
				CcpJsonRepresentation rule = put2
				.put(RuleFields.ruleDescription, ruleExplanation);
				CcpFieldName ccpFieldName2 = new CcpFieldName(fieldName);
				CcpJsonRepresentation updatedRuleExplanation = allRules.addToList(ccpFieldName2, rule);
		
		return updatedRuleExplanation;
	}
	
	/**
	 * Tells whether the field configures this rule.
	 * @param field the field
	 * @param type the declared type of the field
	 * @return {@code true} when the rule is configured
	 */
	boolean hasRuleExplanation(Field field, CcpJsonFieldType type) ;
	/** Fields of a rule explanation. */
	enum RuleFields implements CcpJsonFieldName{
		/** The rule name. */
		ruleName,
		/** The rule explanation. */
		ruleDescription
		;
	}
	/** Fields of an error. */
	public static enum CcpErrorFields implements CcpJsonFieldName{
		/** The rule name. */
		errorName,
		/** The error description. */
		errorDescription
		;
	}
}
