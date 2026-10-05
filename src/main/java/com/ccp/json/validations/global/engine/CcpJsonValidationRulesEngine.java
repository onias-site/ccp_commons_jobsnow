package com.ccp.json.validations.global.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.json.validations.fields.enums.CcpJsonFieldDefaultTypes;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.global.annotations.CcpJsonGlobalValidations;
import com.ccp.json.validations.global.enums.CcpJsonValidatorDefaults;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;

/**
 * Singleton that explains the validation rules of a class: walks the global annotation
 * ({@code @CcpJsonGlobalValidations}) and the fields, producing a JSON describing every configured rule.
 */
public class CcpJsonValidationRulesEngine {

	/** Singleton; use {@link #INSTANCE}. */
	private CcpJsonValidationRulesEngine() {}

	/** The single instance. */
	public static final CcpJsonValidationRulesEngine INSTANCE = new CcpJsonValidationRulesEngine();

	/**
	 * Combines the explanations of the global and of the field rules of the class.
	 * @param clazz the validation class
	 * @return the rules, by class name (global) and by field name
	 */
	public CcpJsonRepresentation getRulesExplanation(Class<?> clazz) {
		
		CcpJsonRepresentation rulesExplanations = this.getRulesExplanationsFromClass(clazz);
		
		rulesExplanations = this.addRulesExplanationsFromFields(rulesExplanations, clazz);
		
		return rulesExplanations;
	}

	/**
	 * Adds, for each field with a recognized type, the explanations of its array, required and type rules.
	 * @param ruleExplanation the accumulated explanations
	 * @param clazz the validation class
	 * @return the updated explanations
	 */
	private CcpJsonRepresentation addRulesExplanationsFromFields(CcpJsonRepresentation ruleExplanation, Class<?> clazz) {
		Field[] declaredFields = clazz.getDeclaredFields();
		
		
		for (Field field : declaredFields) {
			try {
				ruleExplanation = CcpJsonFieldDefaultTypes.Array.updateRuleExplanation(ruleExplanation, field);
				ruleExplanation = CcpJsonFieldDefaultTypes.Required.updateRuleExplanation(ruleExplanation, field);
				Field replacedField = CcpJsonValidatorEngine.INSTANCE.getReplacedField(field);
				CcpJsonFieldType type = CcpJsonValidatorEngine.INSTANCE.getJsonFieldType(replacedField);	
				ruleExplanation = type.updateRuleExplanation(ruleExplanation, replacedField);
			} catch (CcpJsonFieldNotValidated e) {
			}
		}
		return ruleExplanation;
	}

	/**
	 * Lists, under the class name, the explanation of every default and custom global validator; empty when the class has
	 * no {@code @CcpJsonGlobalValidations} (a {@code @CcpJsonCopyGlobalValidationsFrom} is not followed here).
	 * @param clazz the validation class
	 * @return the global explanations
	 */
	private CcpJsonRepresentation getRulesExplanationsFromClass(Class<?> clazz) {
		
		CcpJsonRepresentation rulesExplanation =  CcpOtherConstants.EMPTY_JSON;
		CcpJsonValidatorDefaults[] ccpJsonValidatorDefaultsValues = CcpJsonValidatorDefaults.values();

		List<CcpJsonValidator> defaultGlobalValidations = Arrays.asList(ccpJsonValidatorDefaultsValues);
		boolean annotationPresent = clazz.isAnnotationPresent(CcpJsonGlobalValidations.class);
		boolean skipThisClass = false == annotationPresent;
		if(skipThisClass) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
		var customJsonValidators = annotation.customJsonValidators();
		var asList = Arrays.asList(customJsonValidators);
		var stream = asList
				.stream();
				var streamMap = stream.map(x -> new CcpReflectionConstructorDecorator(x));
				var streamMapMap = streamMap.map(constructor -> (CcpJsonValidator)constructor.newInstance());
				List<CcpJsonValidator> customGlobalValidations = streamMapMap
				.collect(Collectors.toList())	
				;
		List<CcpJsonValidator> allGlobalValidations = new ArrayList<>(defaultGlobalValidations); 
		allGlobalValidations.addAll(customGlobalValidations);
		
		for (CcpJsonValidator globalValidation : allGlobalValidations) {
			Object ruleExplanation = globalValidation.getRuleExplanation(clazz);
			String clazzName = clazz.getName();
			CcpFieldName ccpFieldName = new CcpFieldName(clazzName);
			rulesExplanation = rulesExplanation.addToList(ccpFieldName, ruleExplanation);
		}
		return rulesExplanation;
	}
	
}
