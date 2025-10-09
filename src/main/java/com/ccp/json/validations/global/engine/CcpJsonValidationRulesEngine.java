package com.ccp.json.validations.global.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.json.validations.fields.engine.CcpJsonFieldNotValidated;
import com.ccp.json.validations.fields.enums.CcpJsonFieldDefaultTypes;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.global.annotations.CcpJsonValidatorGlobal;
import com.ccp.json.validations.global.enums.CcpJsonValidatorDefaults;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;

public class CcpJsonValidationRulesEngine {
	
	private CcpJsonValidationRulesEngine() {}
	
	public static final CcpJsonValidationRulesEngine INSTANCE = new CcpJsonValidationRulesEngine();
	
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
		boolean skipThisClass = clazz.isAnnotationPresent(CcpJsonValidatorGlobal.class) == false;
		if(skipThisClass) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
		List<CcpJsonValidator> customGlobalValidations = Arrays.asList(annotation.customJsonValidators())
				.stream().map(x -> new CcpReflectionConstructorDecorator(x)).map(constructor -> (CcpJsonValidator)constructor.newInstance())
				.collect(Collectors.toList())	
				;
		List<CcpJsonValidator> allGlobalValidations = new ArrayList<>(defaultGlobalValidations); 
		allGlobalValidations.addAll(customGlobalValidations);
		
		for (CcpJsonValidator globalValidation : allGlobalValidations) {
			Object ruleExplanation = globalValidation.getRuleExplanation(clazz);
			rulesExplanation = rulesExplanation.getDynamicVersion().addToList(clazz.getName(), ruleExplanation);
		}
		return rulesExplanation;
	}
	
}
