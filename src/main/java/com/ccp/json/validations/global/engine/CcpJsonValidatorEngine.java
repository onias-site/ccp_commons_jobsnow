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
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
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
import com.ccp.json.validations.fields.enums.CcpJsonFieldErrorHandleType.CcpJsonFieldErrorSkipOthersValidationsToTheField;
import com.ccp.json.validations.fields.enums.CcpJsonFieldDefaultTypes;
import com.ccp.json.validations.fields.enums.CcpJsonFieldsValidationContext;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldType;
import com.ccp.json.validations.global.annotations.CcpJsonCopyGlobalValidationsFrom;
import com.ccp.json.validations.global.annotations.CcpJsonGlobalValidations;
import com.ccp.json.validations.global.enums.CcpJsonValidatorDefaults;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator.CcpJsonValidatorErrorBreakValidationsToTheClass;

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
	
		throw new CcpJsonValidationError(clazz, json, errors, rulesExplanation, featureName);
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
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeBoolean.class)) {
			return CcpJsonFieldDefaultTypes.Boolean;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeNestedJson.class)) {
			return CcpJsonFieldDefaultTypes.NestedJson;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeNumber.class)) {
			return CcpJsonFieldDefaultTypes.Number;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeNumberUnsigned.class)) {
			return CcpJsonFieldDefaultTypes.NumberUnsigned;
		}

		if(field.isAnnotationPresent(CcpJsonFieldTypeNumberInteger.class)) {
			return CcpJsonFieldDefaultTypes.NumberInteger;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeString.class)) {
			return CcpJsonFieldDefaultTypes.String;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeTimeAfter.class)) {
			return CcpJsonFieldDefaultTypes.TimeAfterCurrentDate;
		}
		
		if(field.isAnnotationPresent(CcpJsonFieldTypeTimeBefore.class)) {
			return CcpJsonFieldDefaultTypes.TimeBeforeCurrentDate;
		}

		if(field.isAnnotationPresent(CcpJsonFieldTypeCustom.class)) {
			CcpJsonFieldTypeCustom annotation = field.getAnnotation(CcpJsonFieldTypeCustom.class);
			Class<?> value = annotation.value();
			CcpReflectionConstructorDecorator crcd = new CcpReflectionConstructorDecorator(value);
			CcpJsonFieldType newInstance = crcd.newInstance();
			return newInstance;
		}
		
		throw new CcpJsonFieldNotValidated();
	}
	
	/**
	 * Retorna o campo de referência quando {@code @CcpJsonCopyFieldValidationsFrom} está presente;
	 * caso contrário, retorna o próprio campo.
	 */
	public Field getReplacedField(Field field) {
		
		boolean useTheSameField = false == field.isAnnotationPresent(CcpJsonCopyFieldValidationsFrom.class);
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
		} catch(Exception e) {
			throw new RuntimeException(e);
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
				
				CcpJsonRepresentation values = CcpOtherConstants.EMPTY_JSON
				.put(JsonFields.field, field)
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

				boolean isNotAnArray = false == oldField.isAnnotationPresent(CcpJsonFieldValidatorArray.class);
				
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
				
				List<Object> asObjectList = json.getAsObjectList(new CcpFieldName(fieldName));

				for (Object obj : asObjectList) {
					CcpJsonRepresentation put = json.put(new CcpFieldName(fieldName), obj);
					boolean hasNoErrors = false == type.hasErrors(put, field, CcpJsonFieldsValidationContext.collection);
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

		if(clazz.isAnnotationPresent(CcpJsonCopyGlobalValidationsFrom.class)) {
			CcpJsonCopyGlobalValidationsFrom annotation = clazz.getAnnotation(CcpJsonCopyGlobalValidationsFrom.class);
			Class<?> value = annotation.value();
			CcpJsonRepresentation errorsFromClass = this.getErrorsFromClass(value, json);
			return errorsFromClass;
		}

		CcpJsonRepresentation errors =  CcpOtherConstants.EMPTY_JSON;

		boolean annotationIsMissing = false == clazz.isAnnotationPresent(CcpJsonGlobalValidations.class);

		if(annotationIsMissing) {
			return errors;
		}

		List<CcpJsonValidator> defaultGlobalValidations = Arrays.asList(CcpJsonValidatorDefaults.values());
		CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
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

	/**
	 * Exceção de controle de fluxo lançada quando um campo não possui nenhuma anotação de tipo
	 * reconhecida. Capturada silenciosamente para pular o campo.
	 */
	@SuppressWarnings("serial")
	public static class CcpJsonFieldNotValidated extends RuntimeException {
		private CcpJsonFieldNotValidated() {}
	}

	/**
	 * Exceção lançada por {@code CcpJsonValidatorEngine} quando a validação de um JSON falha. Carrega
	 * todos os dados diagnósticos: JSON fornecido, erros encontrados, explicação das regras, nome da
	 * funcionalidade e classe portadora das regras.
	 */
	@SuppressWarnings("serial")
	public static class CcpJsonValidationError extends RuntimeException {

		public final CcpJsonRepresentation json;

		/** Monta o JSON de diagnóstico completo como mensagem da exceção. */
		private CcpJsonValidationError(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
			super(getErrorMessage(clazz, givenJson, errors, rulesExplanation, featureName).asPrettyJson());
			this.json = getErrorMessage(clazz, givenJson, errors, rulesExplanation, featureName);
		}

		private static CcpJsonRepresentation getErrorMessage(Class<?> clazz, CcpJsonRepresentation givenJson, CcpJsonRepresentation errors, CcpJsonRepresentation rulesExplanation, String featureName) {
			CcpJsonRepresentation body = CcpOtherConstants.EMPTY_JSON
			.put(ValidationErrorFields.errors, errors)
			.put(ValidationErrorFields.featureName, featureName)
			.put(ValidationErrorFields.classWithRules, clazz.getName())
			.put(ValidationErrorFields.rulesExplanation, rulesExplanation)
			.put(ValidationErrorFields.givenJson, givenJson);
			return body;
		}

		private enum ValidationErrorFields implements CcpJsonFieldName {
			classWithRules, givenJson, errors, rulesExplanation, featureName
		}
	}
}
