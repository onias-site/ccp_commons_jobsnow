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
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine.CcpJsonFieldNotValidated;
import com.ccp.json.validations.fields.enums.CcpJsonFieldDefaultTypes;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.global.annotations.CcpJsonGlobalValidations;
import com.ccp.json.validations.global.enums.CcpJsonValidatorDefaults;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;

/**
 * Engine singleton de geração de explicações de regras de validação. Percorre as anotações globais
 * ({@code @CcpJsonGlobalValidations}) e os campos de uma classe de validação, produzindo um JSON
 * descrevendo todas as regras ativas.
 */
public class CcpJsonValidationRulesEngine {

	private CcpJsonValidationRulesEngine() {}

	public static final CcpJsonValidationRulesEngine INSTANCE = new CcpJsonValidationRulesEngine();

	/**
	 * Combina as explicações de regras globais e de campo da classe informada e retorna o JSON
	 * completo de regras.
	 * @param clazz a classe de validação a ser inspecionada
	 */
	public CcpJsonRepresentation getRulesExplanation(Class<?> clazz) {
		
		CcpJsonRepresentation rulesExplanations = this.getRulesExplanationsFromClass(clazz);
		
		rulesExplanations = this.addRulesExplanationsFromFields(rulesExplanations, clazz);
		
		return rulesExplanations;
	}

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

	private CcpJsonRepresentation getRulesExplanationsFromClass(Class<?> clazz) {
		
		CcpJsonRepresentation rulesExplanation =  CcpOtherConstants.EMPTY_JSON;
		
		List<CcpJsonValidator> defaultGlobalValidations = Arrays.asList(CcpJsonValidatorDefaults.values());
		boolean skipThisClass = false == clazz.isAnnotationPresent(CcpJsonGlobalValidations.class);
		if(skipThisClass) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
		List<CcpJsonValidator> customGlobalValidations = Arrays.asList(annotation.customJsonValidators())
				.stream().map(x -> new CcpReflectionConstructorDecorator(x)).map(constructor -> (CcpJsonValidator)constructor.newInstance())
				.collect(Collectors.toList())	
				;
		List<CcpJsonValidator> allGlobalValidations = new ArrayList<>(defaultGlobalValidations); 
		allGlobalValidations.addAll(customGlobalValidations);
		
		for (CcpJsonValidator globalValidation : allGlobalValidations) {
			Object ruleExplanation = globalValidation.getRuleExplanation(clazz);
			rulesExplanation = rulesExplanation.addToList(new CcpFieldName(clazz.getName()), ruleExplanation);
		}
		return rulesExplanation;
	}
	
}
