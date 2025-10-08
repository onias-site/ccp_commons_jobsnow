package com.ccp.json.validations.global.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.json.validations.fields.enums.CcpJsonFieldType;
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
				ruleExplanation = CcpJsonFieldType.Array.updateRuleExplanation(ruleExplanation, field);
				ruleExplanation = CcpJsonFieldType.Required.updateRuleExplanation(ruleExplanation, field);
				CcpJsonFieldType type = CcpJsonValidatorEngine.INSTANCE.getJsonFieldType(field);	
				ruleExplanation = type.updateRuleExplanation(ruleExplanation, field);
			} catch (Exception e) {
				System.out.println();
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
