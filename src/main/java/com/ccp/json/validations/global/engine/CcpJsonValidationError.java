package com.ccp.json.validations.global.engine;

import java.util.List;
import java.util.Set;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface.CcpErrorFields;

/**
 * Raised by {@code CcpJsonValidatorEngine} when the validation of a JSON fails. Carries the whole diagnosis: given
 * JSON, errors found, rules explanation, feature name and class holding the rules.
 */
@SuppressWarnings("serial")
public class CcpJsonValidationError extends RuntimeException {

	/**
	 * The diagnosis: {@code errors}, {@code featureName}, {@code classWithRules}, {@code rulesExplanation} and
	 * {@code givenJson}.
	 */
	public final CcpJsonRepresentation json;

	/**
	 * Builds the diagnosis and uses its pretty JSON as the message.
	 * @param clazz the class holding the rules
	 * @param givenJson the validated JSON
	 * @param errors the errors found
	 * @param rulesExplanation the explanation of the rules
	 * @param featureName the feature being validated
	 */
	CcpJsonValidationError(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
		super(getErrorMessage(clazz, givenJson, errors, rulesExplanation, featureName).asPrettyJson());
		this.json = getErrorMessage(clazz, givenJson, errors, rulesExplanation, featureName);
	}

	/**
	 * Builds the diagnosis JSON.
	 * @param clazz the class holding the rules
	 * @param givenJson the validated JSON
	 * @param errors the errors found
	 * @param rulesExplanation the explanation of the rules
	 * @param featureName the feature being validated
	 * @return the diagnosis
	 */
	private static CcpJsonRepresentation getErrorMessage(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
		CcpJsonRepresentation put3 = CcpOtherConstants.EMPTY_JSON
		.put(CcpValidationErrorFields.errors, errors);
		CcpJsonRepresentation put4 = put3
		.put(CcpValidationErrorFields.featureName, featureName);
		String clazzName = clazz.getName();
		CcpJsonRepresentation put5 = put4
		.put(CcpValidationErrorFields.classWithRules, clazzName);
		CcpJsonRepresentation put6 = put5
		.put(CcpValidationErrorFields.rulesExplanation, rulesExplanation);
		CcpJsonRepresentation body = put6
		.put(CcpValidationErrorFields.givenJson, givenJson);
		return body;
	}

	/** Fields of the diagnosis. */
	public static enum CcpValidationErrorFields implements CcpJsonFieldName {
		/** The class holding the rules. */
		classWithRules,
		/** The validated JSON. */
		givenJson,
		/** The errors found, by field (or by class for the global validations). */
		errors,
		/** The explanation of the rules. */
		rulesExplanation,
		/** The feature being validated. */
		featureName
	}

	/**
	 * Joins the descriptions of the field errors as sentences ({@code "description. "}).
	 * @return the joined descriptions
	 */
	public String getExplanedMessage() {
		CcpJsonRepresentation errors = this.json.getInnerJson(CcpValidationErrorFields.errors);
		Set<String> fieldSet = errors.fieldSet();
		String message = "";
		
		for (String field : fieldSet) {
			List<CcpJsonRepresentation> errorsFromField = errors.getAsJsonList(() -> field);
			for (CcpJsonRepresentation error : errorsFromField) {
				String errorDescription = error.getAsString(CcpErrorFields.errorDescription);
				message += (errorDescription + ". ");
			}
		}
		return message;
	}
}
