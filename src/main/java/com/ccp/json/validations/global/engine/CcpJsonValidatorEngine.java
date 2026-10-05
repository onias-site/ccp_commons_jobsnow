package com.ccp.json.validations.global.engine;

import java.lang.reflect.Array;
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

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * Main singleton engine for JSON validation. Orchestrates the global validations (class
 * annotations) and the per-field ones, collecting every error before throwing {@code CcpJsonValidationError}
 * when there are failures. It also inspects field type annotations to determine the right validator.
 */
public class CcpJsonValidatorEngine {
	
	/** Singleton; use {@link #INSTANCE}. */
	private CcpJsonValidatorEngine() {}
	
	/** The single instance. */
	public static final CcpJsonValidatorEngine INSTANCE = new CcpJsonValidatorEngine();
	
	/**
	 * Validates the JSON against the global and field rules of the class.
	 * <p>
	 * Java arrays are treated as collections. The global validations run first; then each field: a missing required
	 * field reports only that; a field with a recognized type runs the rules of its type (for collections, the array rules
	 * and then each item, reporting the first bad item). A rule with {@code breakFieldValidation} stops the other rules of
	 * its field.
	 * @param clazz the class that carries the validation rules
	 * @param json the input JSON
	 * @param featureName name of the feature, for the diagnosis
	 * @return the same JSON when it is valid
	 * @throws CcpJsonValidationError with the full diagnosis when there are errors
	 */
	public CcpJsonRepresentation validateJson(Class<?> clazz, CcpJsonRepresentation json, String featureName) {

		CcpJsonRepresentation jsonWithArraysAsCollections = this.replaceArraysByCollections(json);

		CcpJsonRepresentation errors = this.getErrors(clazz, jsonWithArraysAsCollections);

		boolean hasNoJsonErrors = errors.isEmpty();

		if(hasNoJsonErrors) {
			return json;
		}

		CcpJsonRepresentation rulesExplanation = CcpJsonValidationRulesEngine.INSTANCE.getRulesExplanation(clazz);
		CcpJsonValidationError validationError = new CcpJsonValidationError(clazz, jsonWithArraysAsCollections, errors, rulesExplanation, featureName);

		throw validationError;
	}

	/**
	 * Returns the JSON with every field that holds a java array replaced by the equivalent list, so that the rest of the
	 * validation sees arrays and collections the same way. The original JSON is not changed: the replacement applies only to
	 * the validation.
	 * @param json the input JSON
	 * @return the JSON with lists instead of arrays
	 */
	private CcpJsonRepresentation replaceArraysByCollections(CcpJsonRepresentation json) {

		Set<String> fieldNames = json.fieldSet();

		CcpJsonRepresentation jsonWithArraysAsCollections = json;

		for (String fieldName : fieldNames) {
			Object value = json.content.get(fieldName);
			boolean isNotAnArray = false == this.isArray(value);

			if(isNotAnArray) {
				continue;
			}

			List<Object> collection = this.toCollection(value);
			CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
			jsonWithArraysAsCollections = jsonWithArraysAsCollections.put(ccpFieldName, collection);
		}

		return jsonWithArraysAsCollections;
	}

	/**
	 * Tells whether the value is a java array.
	 * @param value the value, possibly {@code null}
	 * @return {@code true} for a non-null array
	 */
	private boolean isArray(Object value) {
		boolean valueIsAbsent = value == null;

		if(valueIsAbsent) {
			return false;
		}

		Class<?> valueClass = value.getClass();
		boolean isArray = valueClass.isArray();
		return isArray;
	}

	/**
	 * Copies the items of a java array (primitive or not) into a list.
	 * @param array the array
	 * @return the list
	 */
	private List<Object> toCollection(Object array) {
		int length = Array.getLength(array);
		List<Object> collection = new ArrayList<>();

		for (int index = 0; index < length; index++) {
			Object item = Array.get(array, index);
			collection.add(item);
		}

		return collection;
	}

	/**
	 * Collects the global errors and then the field errors.
	 * @param clazz the validation class
	 * @param json the JSON
	 * @return the errors
	 */
	private CcpJsonRepresentation getErrors(Class<?> clazz, CcpJsonRepresentation json) {
		
		CcpJsonRepresentation errors = this.getErrorsFromClass(clazz, json);
		
		errors = this.addErrorsFromFields(errors, json, clazz);
		
		return errors;
	}

	/**
	 * Returns the {@code CcpJsonFieldType} declared by the annotations of the field, checked in this order: boolean, nested
	 * JSON, number, unsigned number, integer number, string, time after, time before, custom.
	 * @param field the field
	 * @return the field type
	 * @throws CcpJsonFieldNotValidated when the field declares no type
	 */
	public CcpJsonFieldType getJsonFieldType(Field field) {
		boolean isBoolean = field.isAnnotationPresent(CcpJsonFieldTypeBoolean.class);
	
		if(isBoolean) {
			return CcpJsonFieldDefaultTypes.Boolean;
		}
		boolean isNestedJson = field.isAnnotationPresent(CcpJsonFieldTypeNestedJson.class);

		if(isNestedJson) {
			return CcpJsonFieldDefaultTypes.NestedJson;
		}
		boolean isNumber = field.isAnnotationPresent(CcpJsonFieldTypeNumber.class);

		if(isNumber) {
			return CcpJsonFieldDefaultTypes.Number;
		}
		boolean isNumberUnsigned = field.isAnnotationPresent(CcpJsonFieldTypeNumberUnsigned.class);

		if(isNumberUnsigned) {
			return CcpJsonFieldDefaultTypes.NumberUnsigned;
		}
		boolean isNumberInteger = field.isAnnotationPresent(CcpJsonFieldTypeNumberInteger.class);

		if(isNumberInteger) {
			return CcpJsonFieldDefaultTypes.NumberInteger;
		}
		boolean isString = field.isAnnotationPresent(CcpJsonFieldTypeString.class);

		if(isString) {
			return CcpJsonFieldDefaultTypes.String;
		}
		boolean isTimeAfter = field.isAnnotationPresent(CcpJsonFieldTypeTimeAfter.class);

		if(isTimeAfter) {
			return CcpJsonFieldDefaultTypes.TimeAfterCurrentDate;
		}
		boolean isTimeBefore = field.isAnnotationPresent(CcpJsonFieldTypeTimeBefore.class);

		if(isTimeBefore) {
			return CcpJsonFieldDefaultTypes.TimeBeforeCurrentDate;
		}
		boolean isCustom = field.isAnnotationPresent(CcpJsonFieldTypeCustom.class);

		if(isCustom) {
			CcpJsonFieldTypeCustom annotation = field.getAnnotation(CcpJsonFieldTypeCustom.class);
			Class<?> value = annotation.value();
			CcpReflectionConstructorDecorator constructorDecorator = new CcpReflectionConstructorDecorator(value);
			CcpJsonFieldType customFieldType = constructorDecorator.newInstance();
			return customFieldType;
		}
		CcpJsonFieldNotValidated fieldNotValidatedError = new CcpJsonFieldNotValidated();

		throw fieldNotValidatedError;
	}
	
	/**
	 * Returns the field of the same name in the class of {@code @CcpJsonCopyFieldValidationsFrom}, when present (the field
	 * itself when that class does not declare it); otherwise the field itself.
	 * @param field the field
	 * @return the field holding the validations
	 */
	public Field getReplacedField(Field field) {
		boolean copiesValidations = field.isAnnotationPresent(CcpJsonCopyFieldValidationsFrom.class);
	
		boolean useTheSameField = false == copiesValidations;
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
	
	/**
	 * Adds the errors of every field of the class (see {@link #validateJson}).
	 * @param errors the accumulated errors
	 * @param json the JSON
	 * @param clazz the validation class
	 * @return the updated errors
	 */
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
				CcpJsonRepresentation jsonWithField = CcpOtherConstants.EMPTY_JSON
				.put(CcpJsonCommonsFields.field, field);

				CcpJsonRepresentation values = jsonWithField
				.put(CcpJsonCommonsFields.type, jsonFieldType);
				map.put(replacedField, values);
			} catch (CcpJsonFieldNotValidated e) {

			}
		}
		
		Set<Field> fields = map.keySet();
		
		for (Field field : fields) {
			CcpJsonRepresentation values = map.get(field);
			CcpJsonFieldType type = values.getAsObject(CcpJsonCommonsFields.type);
			Field oldField = values.getAsObject(CcpJsonCommonsFields.field);
			try {
				boolean isArrayField = oldField.isAnnotationPresent(CcpJsonFieldValidatorArray.class);

				boolean isNotAnArray = false == isArrayField;
				
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
					CcpFieldName itemFieldName = new CcpFieldName(fieldName);
					CcpJsonRepresentation jsonWithItem = json.put(itemFieldName, obj);
					boolean itemHasErrors = type.hasErrors(jsonWithItem, field, CcpJsonFieldsValidationContext.collection);
					boolean hasNoErrors = false == itemHasErrors;
					if(hasNoErrors) {
						continue;
					}
					errors = type.getErrors(errors, jsonWithItem, field, CcpJsonFieldsValidationContext.collection);
					break;
				}
				
			} catch (CcpJsonFieldErrorSkipOthersValidationsToTheField e) {
				errors = errors.mergeWithAnotherJson(e.validationResultFromField);
			}
		}
		return errors;
	}

	/**
	 * Runs the default and custom global validators of the class (or of the class named by
	 * {@code @CcpJsonCopyGlobalValidationsFrom}); a critical validator stops the others.
	 * @param clazz the validation class
	 * @param json the JSON
	 * @return the global errors
	 */
	private CcpJsonRepresentation getErrorsFromClass(Class<?> clazz, CcpJsonRepresentation json) {
		boolean copiesGlobalValidations = clazz.isAnnotationPresent(CcpJsonCopyGlobalValidationsFrom.class);

		if(copiesGlobalValidations) {
			CcpJsonCopyGlobalValidationsFrom annotation = clazz.getAnnotation(CcpJsonCopyGlobalValidationsFrom.class);
			Class<?> value = annotation.value();
			CcpJsonRepresentation errorsFromClass = this.getErrorsFromClass(value, json);
			return errorsFromClass;
		}

		CcpJsonRepresentation errors =  CcpOtherConstants.EMPTY_JSON;
		boolean hasGlobalValidations = clazz.isAnnotationPresent(CcpJsonGlobalValidations.class);

		boolean annotationIsMissing = false == hasGlobalValidations;

		if(annotationIsMissing) {
			return errors;
		}
		CcpJsonValidatorDefaults[] defaultValidators = CcpJsonValidatorDefaults.values();

		List<CcpJsonValidator> defaultGlobalValidations = Arrays.asList(defaultValidators);
		CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
		var customJsonValidators = annotation.customJsonValidators();
		var customValidatorClasses = Arrays.asList(customJsonValidators);
		var customValidatorClassesStream = customValidatorClasses
				.stream();
				var customValidatorConstructors = customValidatorClassesStream.map(x -> new CcpReflectionConstructorDecorator(x));
				var customValidatorInstances = customValidatorConstructors.map(constructor -> (CcpJsonValidator)constructor.newInstance());
				List<CcpJsonValidator> customGlobalValidations = customValidatorInstances
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
