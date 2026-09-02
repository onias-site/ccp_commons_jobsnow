package com.ccp.json.validations.global.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.json.validations.fields.annotations.CcpJsonCopyFieldValidationsFrom;
import com.ccp.json.validations.fields.annotations.CcpJsonFieldValidatorArray;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeBoolean;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeCustom;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNestedJson;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumber;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberInteger;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeNumberUnsigned;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeString;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeAfter;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;
import com.ccp.json.validations.fields.enums.CcpJsonFieldErrorSkipOthersValidationsToTheField;
import com.ccp.json.validations.fields.enums.CcpJsonFieldDefaultTypes;
import com.ccp.json.validations.fields.enums.CcpJsonFieldsValidationContext;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.global.annotations.CcpJsonCopyGlobalValidationsFrom;
import com.ccp.json.validations.global.annotations.CcpJsonGlobalValidations;
import com.ccp.json.validations.global.enums.CcpJsonValidatorDefaults;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;
import com.ccp.json.validations.global.interfaces.CcpJsonValidatorErrorBreakValidationsToTheClass;

/**
 * Engine singleton principal de validação de JSONs. Orquestra as validações globais (anotações de
 * classe) e por campo, coletando todos os erros antes de lançar {@code CcpJsonValidationError} caso
 * haja falhas. Também inspeciona anotações de tipo de campo para determinar o validador correto.
 */
public class CcpJsonValidatorEngine {
	enum JsonFields implements CcpJsonFieldName{
		field, type
		;
	}
	
	private CcpJsonValidatorEngine() {}
	
	public static final CcpJsonValidatorEngine INSTANCE = new CcpJsonValidatorEngine();
	
	/**
	 * Valida o JSON; retorna o JSON original se nenhum erro for encontrado, ou lança
	 * {@code CcpJsonValidationError} com diagnóstico completo caso haja erros.
	 * @param clazz a classe portadora das regras de validação
	 * @param json o JSON de entrada a ser validado
	 * @param featureName nome da funcionalidade para diagnóstico
	 */
	public CcpJsonRepresentation validateJson(Class<?> clazz, CcpJsonRepresentation json, String featureName) {
		
		CcpJsonRepresentation errors = this.getErrors(clazz, json);
		
		boolean hasNoJsonErrors = errors.isEmpty();
		
		if(hasNoJsonErrors) {
			return json;
		}
		
		CcpJsonRepresentation rulesExplanation = CcpJsonValidationRulesEngine.INSTANCE.getRulesExplanation(clazz);
		CcpJsonValidationError ccpJsonValidationError = new CcpJsonValidationError(clazz, json, errors, rulesExplanation, featureName);

		throw ccpJsonValidationError;
	}
	
	private CcpJsonRepresentation getErrors(Class<?> clazz, CcpJsonRepresentation json) {
		
		CcpJsonRepresentation errors = this.getErrorsFromClass(clazz, json);
		
		errors = this.addErrorsFromFields(errors, json, clazz);
		
		return errors;
	}

	/**
	 * Inspeciona as anotações do campo e retorna o {@code CcpJsonFieldType} correspondente.
	 * Lança {@code CcpJsonFieldNotValidated} se nenhuma anotação de tipo for encontrada.
	 */
	public CcpJsonFieldType getJsonFieldType(Field field) {
		boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeBoolean.class);
	
		if(annotationPresent) {
			return CcpJsonFieldDefaultTypes.Boolean;
		}
		boolean annotationPresent2 = field.isAnnotationPresent(CcpJsonFieldTypeNestedJson.class);

		if(annotationPresent2) {
			return CcpJsonFieldDefaultTypes.NestedJson;
		}
		boolean annotationPresent3 = field.isAnnotationPresent(CcpJsonFieldTypeNumber.class);

		if(annotationPresent3) {
			return CcpJsonFieldDefaultTypes.Number;
		}
		boolean annotationPresent4 = field.isAnnotationPresent(CcpJsonFieldTypeNumberUnsigned.class);

		if(annotationPresent4) {
			return CcpJsonFieldDefaultTypes.NumberUnsigned;
		}
		boolean annotationPresent5 = field.isAnnotationPresent(CcpJsonFieldTypeNumberInteger.class);

		if(annotationPresent5) {
			return CcpJsonFieldDefaultTypes.NumberInteger;
		}
		boolean annotationPresent6 = field.isAnnotationPresent(CcpJsonFieldTypeString.class);

		if(annotationPresent6) {
			return CcpJsonFieldDefaultTypes.String;
		}
		boolean annotationPresent7 = field.isAnnotationPresent(CcpJsonFieldTypeTimeAfter.class);

		if(annotationPresent7) {
			return CcpJsonFieldDefaultTypes.TimeAfterCurrentDate;
		}
		boolean annotationPresent8 = field.isAnnotationPresent(CcpJsonFieldTypeTimeBefore.class);

		if(annotationPresent8) {
			return CcpJsonFieldDefaultTypes.TimeBeforeCurrentDate;
		}
		boolean annotationPresent9 = field.isAnnotationPresent(CcpJsonFieldTypeCustom.class);

		if(annotationPresent9) {
			CcpJsonFieldTypeCustom annotation = field.getAnnotation(CcpJsonFieldTypeCustom.class);
			Class<?> value = annotation.value();
			CcpReflectionConstructorDecorator crcd = new CcpReflectionConstructorDecorator(value);
			CcpJsonFieldType newInstance = crcd.newInstance();
			return newInstance;
		}
		CcpJsonFieldNotValidated ccpJsonFieldNotValidated = new CcpJsonFieldNotValidated();

		throw ccpJsonFieldNotValidated;
	}
	
	/**
	 * Retorna o campo de referência quando {@code @CcpJsonCopyFieldValidationsFrom} está presente;
	 * caso contrário, retorna o próprio campo.
	 */
	public Field getReplacedField(Field field) {
		boolean annotationPresent10 = field.isAnnotationPresent(CcpJsonCopyFieldValidationsFrom.class);
	
		boolean useTheSameField = false == annotationPresent10;
		if(useTheSameField) {
			return field;
		}
		
		CcpJsonCopyFieldValidationsFrom annotation = field.getAnnotation(CcpJsonCopyFieldValidationsFrom.class);
		Class<?> classToAppendValidations = annotation.value();
		try {
			String fieldName = field.getName();
			Field declaredField = classToAppendValidations.getDeclaredField(fieldName);
			return declaredField;
		} catch (NoSuchFieldException e) {
			return field;
		}
	}
	
	private CcpJsonRepresentation addErrorsFromFields(CcpJsonRepresentation errors, CcpJsonRepresentation json, Class<?> clazz) {
		Field[] declaredFields = clazz.getDeclaredFields();
		Map<Field, CcpJsonRepresentation> map = new LinkedHashMap<>();
		
		for (Field field : declaredFields) {
			try {
				boolean hasErrors = CcpJsonFieldDefaultTypes.Required.hasErrors(json, field, CcpJsonFieldsValidationContext.collection);

				if(hasErrors) {
					errors = CcpJsonFieldDefaultTypes.Required.getErrors(errors, json, field, CcpJsonFieldsValidationContext.collection);
					continue;
				}
				
				Field replacedField = this.getReplacedField(field);
				CcpJsonFieldType jsonFieldType = this.getJsonFieldType(replacedField);
				CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON
				.put(JsonFields.field, field);

				CcpJsonRepresentation values = put2
				.put(JsonFields.type, jsonFieldType);
				map.put(replacedField, values);
			} catch (CcpJsonFieldNotValidated e) {

			}
		}
		
		Set<Field> fields = map.keySet();
		
		for (Field field : fields) {
			CcpJsonRepresentation values = map.get(field);
			CcpJsonFieldDefaultTypes type = values.getAsObject(JsonFields.type);
			Field oldField = values.getAsObject(JsonFields.field);
			try {
				boolean annotationPresent11 = oldField.isAnnotationPresent(CcpJsonFieldValidatorArray.class);

				boolean isNotAnArray = false == annotationPresent11;
				
				if(isNotAnArray) {
					errors = type.getErrors(errors, json, field, CcpJsonFieldsValidationContext.single);
					continue;
				}
				
				boolean hasArrayErrors = CcpJsonFieldDefaultTypes.Array.hasErrors(json, oldField, CcpJsonFieldsValidationContext.single);

				if(hasArrayErrors) {
					errors = CcpJsonFieldDefaultTypes.Array.getErrors(errors, json, oldField, CcpJsonFieldsValidationContext.single);
					continue;
				}
				String fieldName = field.getName();
				CcpFieldName ccpFieldName = new CcpFieldName(fieldName);

				List<Object> asObjectList = json.getAsObjectList(ccpFieldName);

				for (Object obj : asObjectList) {
					CcpFieldName ccpFieldName2 = new CcpFieldName(fieldName);
					CcpJsonRepresentation put = json.put(ccpFieldName2, obj);
					boolean errors2 = type.hasErrors(put, field, CcpJsonFieldsValidationContext.collection);
					boolean hasNoErrors = false == errors2;
					if(hasNoErrors) {
						continue;
					}
					errors = type.getErrors(errors, put, field, CcpJsonFieldsValidationContext.collection);
					break;
				}
				
			} catch (CcpJsonFieldErrorSkipOthersValidationsToTheField e) {
				errors = errors.mergeWithAnotherJson(e.validationResultFromField);
			}
		}
		return errors;
	}

	private CcpJsonRepresentation getErrorsFromClass(Class<?> clazz, CcpJsonRepresentation json) {
		boolean annotationPresent12 = clazz.isAnnotationPresent(CcpJsonCopyGlobalValidationsFrom.class);

		if(annotationPresent12) {
			CcpJsonCopyGlobalValidationsFrom annotation = clazz.getAnnotation(CcpJsonCopyGlobalValidationsFrom.class);
			Class<?> value = annotation.value();
			CcpJsonRepresentation errorsFromClass = this.getErrorsFromClass(value, json);
			return errorsFromClass;
		}

		CcpJsonRepresentation errors =  CcpOtherConstants.EMPTY_JSON;
		boolean annotationPresent13 = clazz.isAnnotationPresent(CcpJsonGlobalValidations.class);

		boolean annotationIsMissing = false == annotationPresent13;

		if(annotationIsMissing) {
			return errors;
		}
		CcpJsonValidatorDefaults[] ccpJsonValidatorDefaultsValues = CcpJsonValidatorDefaults.values();

		List<CcpJsonValidator> defaultGlobalValidations = Arrays.asList(ccpJsonValidatorDefaultsValues);
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
			try {
				errors = globalValidation.getErrors(errors, json, clazz);
			} catch (CcpJsonValidatorErrorBreakValidationsToTheClass e) {
				return e.errors;
			}
		}
		return errors;
	}


}
