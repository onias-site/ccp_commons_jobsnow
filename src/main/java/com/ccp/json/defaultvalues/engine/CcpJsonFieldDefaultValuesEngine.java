package com.ccp.json.defaultvalues.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.json.defaultvalues.annotations.CcpJsonFieldDefaultValue;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

/**
 * Singleton that fills, in the JSON being handled, the absent fields annotated with {@code CcpJsonFieldDefaultValue}. It
 * mirrors the mechanics of the validation engine, including {@code CcpJsonCopyFieldValidationsFrom}, but instead of
 * reporting an error it writes a value.
 */
public class CcpJsonFieldDefaultValuesEngine {

	/** Singleton; use {@link #INSTANCE}. */
	private CcpJsonFieldDefaultValuesEngine() {}

	/** The single instance. */
	public static final CcpJsonFieldDefaultValuesEngine INSTANCE = new CcpJsonFieldDefaultValuesEngine();

	/**
	 * Returns the JSON plus the default values declared in the fields of the class holding the rules, field by field in
	 * declaration order (a later default may use an earlier one in its template). Fields already present are kept as they
	 * are.
	 * @param clazz the class holding the rules (the same used in the validation)
	 * @param json the JSON being handled
	 * @return the JSON with the default values
	 */
	public CcpJsonRepresentation putDefaultValues(Class<?> clazz, CcpJsonRepresentation json) {

		Field[] declaredFields = clazz.getDeclaredFields();

		CcpJsonRepresentation jsonWithDefaultValues = json;

		for (Field field : declaredFields) {
			jsonWithDefaultValues = this.putDefaultValue(field, jsonWithDefaultValues);
		}

		return jsonWithDefaultValues;
	}

	/**
	 * Fills one field when it is absent and declares a default value (in itself or in the field it copies the validations
	 * from).
	 * @param field the field
	 * @param json the JSON being handled
	 * @return the JSON, possibly with the field filled
	 */
	private CcpJsonRepresentation putDefaultValue(Field field, CcpJsonRepresentation json) {

		String fieldName = field.getName();
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName);

		boolean fieldWasInformed = json.containsField(ccpFieldName);

		if(fieldWasInformed) {
			return json;
		}

		Field replacedField = CcpJsonValidatorEngine.INSTANCE.getReplacedField(field);
		boolean annotationPresent = replacedField.isAnnotationPresent(CcpJsonFieldDefaultValue.class);

		boolean annotationIsMissing = false == annotationPresent;

		if(annotationIsMissing) {
			return json;
		}

		CcpJsonFieldDefaultValue annotation = replacedField.getAnnotation(CcpJsonFieldDefaultValue.class);
		String[] defaultStrings = annotation.defaultStrings();

		boolean defaultStringsIsEmpty = defaultStrings.length == 0;

		if(defaultStringsIsEmpty) {
			CcpJsonRepresentation producedJson = this.getJsonFromProducer(annotation, json);
			return producedJson;
		}

		CcpJsonRepresentation jsonWithDefaultStrings = this.putDefaultStrings(defaultStrings, ccpFieldName, json);
		return jsonWithDefaultStrings;
	}

	/**
	 * Resolves each default text as a template over the JSON and stores the result in the field.
	 * @param defaultStrings the default texts
	 * @param ccpFieldName the field
	 * @param json the JSON being handled
	 * @return the JSON with the field filled
	 */
	private CcpJsonRepresentation putDefaultStrings(String[] defaultStrings, CcpFieldName ccpFieldName, CcpJsonRepresentation json) {

		List<String> resolvedTemplates = new ArrayList<>();

		for (String defaultString : defaultStrings) {
			CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(defaultString);
			CcpTextDecorator text = ccpStringDecorator.text();
			CcpTextDecorator resolvedTemplate = text.resolveTemplate(json);
			String resolvedTemplateContent = resolvedTemplate.content;
			resolvedTemplates.add(resolvedTemplateContent);
		}

		Object defaultValue = this.getDefaultValue(resolvedTemplates);
		CcpJsonRepresentation jsonWithDefaultStrings = json.put(ccpFieldName, defaultValue);

		return jsonWithDefaultStrings;
	}

	/**
	 * A single value becomes a String; two or more become a list of Strings.
	 * @param resolvedTemplates the resolved texts
	 * @return the value to store
	 */
	private Object getDefaultValue(List<String> resolvedTemplates) {

		int size = resolvedTemplates.size();
		boolean isASingleValue = size == 1;

		if(isASingleValue) {
			String singleValue = resolvedTemplates.get(0);
			return singleValue;
		}

		return resolvedTemplates;
	}

	/**
	 * Runs the producer of the annotation over the JSON.
	 * @param annotation the default value annotation
	 * @param json the JSON being handled
	 * @return the JSON returned by the producer
	 */
	private CcpJsonRepresentation getJsonFromProducer(CcpJsonFieldDefaultValue annotation, CcpJsonRepresentation json) {

		Class<? extends CcpBusiness> jsonProducer = annotation.jsonProducer();
		CcpReflectionConstructorDecorator crcd = new CcpReflectionConstructorDecorator(jsonProducer);
		CcpBusiness business = crcd.newInstance();
		CcpJsonRepresentation producedJson = business.execute(json);

		return producedJson;
	}

}
