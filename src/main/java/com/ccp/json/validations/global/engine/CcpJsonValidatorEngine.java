package com.ccp.json.validations.global.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.CcpJsonCommonsFields;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeBoolean;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberInteger;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberNatural;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeAfter;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;
import com.ccp.json.validations.fields.engine.CcpJsonFieldErrorSkipOthersValidationsToTheField;
import com.ccp.json.validations.fields.engine.CcpJsonFieldNotValidated;
import com.ccp.json.validations.fields.enums.CcpJsonFieldType;
import com.ccp.json.validations.fields.enums.CcpJsonFieldsValidationContext;
import com.ccp.json.validations.global.annotations.CcpJsonValidatorGlobal;
import com.ccp.json.validations.global.enums.CcpJsonValidatorDefaults;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;

public class CcpJsonValidatorEngine {
	
	private CcpJsonValidatorEngine() {}
	
	public static final CcpJsonValidatorEngine INSTANCE = new CcpJsonValidatorEngine();
	
	public CcpJsonRepresentation validateJson(Class<?> clazz, CcpJsonRepresentation json, String featureName) {
		
		CcpJsonRepresentation errors = this.getErrors(clazz, json);
		
		boolean hasNoJsonErrors = errors.isEmpty();
		
		if(hasNoJsonErrors) {
			return json;
		}
		
		CcpJsonRepresentation rulesExplanation = CcpJsonValidationRulesEngine.INSTANCE.getRulesExplanation(clazz);
	
		throw new CcpJsonValidationError(clazz, json, errors, rulesExplanation, featureName);
	}
	
	public CcpJsonRepresentation getErrors(Class<?> clazz, CcpJsonRepresentation json) {
		
		CcpJsonRepresentation errors = this.getErrorsFromClass(clazz, json);
		
		errors = this.addErrorsFromFields(errors, json, clazz);
		
		return errors;
	}

	public CcpJsonFieldType getJsonFieldType(Field field) {
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeBoolean.class)) {
			return CcpJsonFieldType.Boolean;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeNestedJson.class)) {
			return CcpJsonFieldType.NestedJson;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeNumber.class)) {
			return CcpJsonFieldType.Number;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeNumberNatural.class)) {
			return CcpJsonFieldType.NumberUnsigned;
		}

		if(field.isAnnotationPresent(CcpJsonFieldTypeNumberInteger.class)) {
			return CcpJsonFieldType.NumberInteger;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeString.class)) {
			return CcpJsonFieldType.String;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeTimeAfter.class)) {
			return CcpJsonFieldType.TimeAfterCurrentDate;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeTimeBefore.class)) {
			return CcpJsonFieldType.TimeBeforeCurrentDate;
		}
		
		throw new CcpJsonFieldNotValidated();
	}
	
	private Field getReplacedField(Field field) {
		
		boolean useTheSameField = false == field.isAnnotationPresent(CcpJsonCommonsFields.class);
		if(useTheSameField) {
			return field;
		}
		
		CcpJsonCommonsFields annotation = field.getAnnotation(CcpJsonCommonsFields.class);
		Class<?> classToAppendValidations = annotation.value();
		try {
			String fieldName = field.getName();
			Field declaredField = classToAppendValidations.getDeclaredField(fieldName);
			return declaredField;
		} catch (NoSuchFieldException e) {
			return field;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private CcpJsonRepresentation addErrorsFromFields(CcpJsonRepresentation errors, CcpJsonRepresentation json, Class<?> clazz) {
		Field[] declaredFields = clazz.getDeclaredFields();
		Map<Field, CcpJsonFieldType> map = new LinkedHashMap<>();
		
		for (Field field : declaredFields) {
			try {
				boolean hasErrors = CcpJsonFieldType.Required.hasErrors(json, field, CcpJsonFieldsValidationContext.collection);

				if(hasErrors) {
					errors = CcpJsonFieldType.Required.getErrors(errors, json, field, CcpJsonFieldsValidationContext.collection);
					continue;
				}
				
				Field replacedField = this.getReplacedField(field);
				CcpJsonFieldType jsonFieldType = this.getJsonFieldType(replacedField);
				map.put(replacedField, jsonFieldType);
			} catch (CcpJsonFieldNotValidated e) {

			}
		}
		
		Set<Field> fields = map.keySet();
		
		for (Field field : fields) {
			
			CcpJsonFieldType type = map.get(field);
			try {

				boolean isNotAnArray = false == field.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
				
				if(isNotAnArray) {
					errors = type.getErrors(errors, json, field, CcpJsonFieldsValidationContext.single);
					continue;
				}
				
				boolean hasArrayErrors = CcpJsonFieldType.Array.hasErrors(json, field, CcpJsonFieldsValidationContext.single);

				if(hasArrayErrors) {
					errors = CcpJsonFieldType.Array.getErrors(errors, json, field, CcpJsonFieldsValidationContext.single);
					continue;
				}
				String fieldName = field.getName();
				
				List<Object> asObjectList = json.getDynamicVersion().getAsObjectList(fieldName);
				
				for (Object obj : asObjectList) {
					CcpJsonRepresentation put = json.getDynamicVersion().put(fieldName, obj);
					boolean hasNoErrors = false == type.hasErrors(put, field, CcpJsonFieldsValidationContext.collection);
					if(hasNoErrors) {
						continue;
					}
					errors = type.getErrors(errors, put, field, CcpJsonFieldsValidationContext.collection);
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
		
		boolean annotationIsMissing = false == clazz.isAnnotationPresent(CcpJsonValidatorGlobal.class);
		
		if(annotationIsMissing) {
			return errors;
		}

		List<CcpJsonValidator> defaultGlobalValidations = Arrays.asList(CcpJsonValidatorDefaults.values());
		CcpJsonValidatorGlobal annotation = clazz.getAnnotation(CcpJsonValidatorGlobal.class);
		List<CcpJsonValidator> customGlobalValidations = Arrays.asList(annotation.customJsonValidators())
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
