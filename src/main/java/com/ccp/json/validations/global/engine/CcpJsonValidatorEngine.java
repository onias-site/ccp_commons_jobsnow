package com.ccp.json.validations.global.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidator;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeArray;
import com.ccp.json.validations.fields.engine.CcpJsonFieldErrorSkipOthersValidationsToTheField;
import com.ccp.json.validations.fields.enums.CcpJsonFieldType;
import com.ccp.json.validations.global.annotations.CcpJsonValidatorGlobal;
import com.ccp.json.validations.global.enums.CcpJsonValidatorDefaults;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;

public class CcpJsonValidatorEngine {
	
	private CcpJsonValidatorEngine() {}
	
	public static final CcpJsonValidatorEngine INSTANCE = new CcpJsonValidatorEngine();
	
	public CcpJsonRepresentation getErrors(Class<?> clazz, CcpJsonRepresentation json) {
		
		CcpJsonRepresentation errors = this.getErrorsFromClass(clazz, json);
		
		errors = this.addErrorsFromFields(errors, json, clazz);
		
		return errors;
	}

	private CcpJsonRepresentation addErrorsFromFields(CcpJsonRepresentation errors, CcpJsonRepresentation json, Class<?> clazz) {
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			try {
				boolean ignoreThisField = false == field.isAnnotationPresent(CcpJsonFieldValidator.class);

				if (ignoreThisField) {
					continue; 
				}
				boolean isNotAnArray = false == field.isAnnotationPresent(CcpJsonFieldTypeArray.class);
				
				if(isNotAnArray) {
					CcpJsonFieldValidator jsonField = field.getAnnotation(CcpJsonFieldValidator.class);
					CcpJsonFieldType type = jsonField.type();
					errors = type.getErrors(errors, json, field);
					continue;
				}
				boolean hasArrayErrors = CcpJsonFieldType.Array.hasErrors(json, field);
				
				if(hasArrayErrors) {
					errors = CcpJsonFieldType.Array.getErrors(errors, json, field);
					continue;
				}
				
				String fieldName = field.getName();
				
				List<Object> asObjectList = json.getDynamicVersion().getAsObjectList(fieldName);
				
				for (Object obj : asObjectList) {
					CcpJsonFieldValidator jsonField = field.getAnnotation(CcpJsonFieldValidator.class);
					CcpJsonFieldType type = jsonField.type();
					CcpJsonRepresentation put = json.getDynamicVersion().put(fieldName, obj);
					boolean hasNoErrors = false == type.hasErrors(put, field);
					if(hasNoErrors) {
						continue;
					}
					errors = type.getErrors(errors, put, field);
					break;
				}
				
			} catch (CcpJsonFieldErrorSkipOthersValidationsToTheField e) {
				errors = errors.putAll(e.validationResultFromField);
			}
		}
		return errors;
	}

	private CcpJsonRepresentation getErrorsFromClass(Class<?> clazz, CcpJsonRepresentation json) {
		CcpJsonRepresentation errors =  CcpOtherConstants.EMPTY_JSON;
		
		List<CcpJsonValidator> defaultGlobalValidations = Arrays.asList(CcpJsonValidatorDefaults.values());
		List<CcpJsonValidator> customGlobalValidations = Arrays.asList(clazz.getAnnotation(CcpJsonValidatorGlobal.class).customJsonValidators())
				.stream().map(x -> new CcpReflectionConstructorDecorator(x)).map(constructor -> (CcpJsonValidator)constructor.newInstance())
				.collect(Collectors.toList())	
				;
		List<CcpJsonValidator> allGlobalValidations = new ArrayList<>(defaultGlobalValidations); 
		allGlobalValidations.addAll(customGlobalValidations);
		
		for (CcpJsonValidator globalValidation : allGlobalValidations) {
			try {
				errors = globalValidation.getErrors(errors, json, clazz);
			} catch (CcpJsonValidatorErrorBreakValidationsToTheClass e) {
				return e.errors;
			}
		}
		return errors;
	}
	
}
