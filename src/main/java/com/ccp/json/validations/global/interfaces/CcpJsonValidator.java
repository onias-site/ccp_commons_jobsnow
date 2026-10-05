package com.ccp.json.validations.global.interfaces;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Contract of the class-level (global) validators. The default {@code getErrors} collects the error and, when the
 * validation is critical, throws {@code CcpJsonValidatorErrorBreakValidationsToTheClass} to stop the other global
 * validations.
 */
public interface CcpJsonValidator {

	/**
	 * Tells whether the global validation fails for the JSON.
	 * @param json the JSON
	 * @param clazz the validation class
	 * @return {@code true} when it fails
	 */
	boolean hasError(CcpJsonRepresentation json, Class<?> clazz);

	/**
	 * Describes the failure.
	 * @param json the JSON
	 * @param clazz the validation class
	 * @return the error
	 */
	Object getErrorMessage(CcpJsonRepresentation json, Class<?> clazz);

	/**
	 * Tells whether a failure must stop the other global validations.
	 * @param json the JSON
	 * @param clazz the validation class
	 * @return {@code true} when critical
	 */
	boolean isCriticalValidation(CcpJsonRepresentation json, Class<?> clazz);

	/**
	 * Explains the validation in natural language.
	 * @param clazz the validation class
	 * @return the explanation
	 */
	Object getRuleExplanation(Class<?> clazz);

	/**
	 * Appends the error, under the class name, to the accumulated errors when the validation fails.
	 * @param errors the accumulated errors
	 * @param json the JSON
	 * @param clazz the validation class
	 * @return the updated errors
	 * @throws CcpJsonValidatorErrorBreakValidationsToTheClass when a critical validation fails
	 */
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Class<?> clazz) {
		boolean error2 = this.hasError(json, clazz);

		boolean hasNoError = false == error2;

		if (hasNoError) {
			return errors;
		}

		String className = clazz.getName();

		Object error = this.getErrorMessage(json, clazz);
		CcpFieldName ccpFieldName = new CcpFieldName(className);

		CcpJsonRepresentation updatedErrors = errors.addToList(ccpFieldName, error);

		boolean criticalValidation = this.isCriticalValidation(json, clazz);
		
		if(criticalValidation) {
			CcpJsonValidatorErrorBreakValidationsToTheClass ccpJsonValidatorErrorBreakValidationsToTheClass = new CcpJsonValidatorErrorBreakValidationsToTheClass(updatedErrors);
			throw ccpJsonValidatorErrorBreakValidationsToTheClass;
		}

		return updatedErrors;
	}

}
