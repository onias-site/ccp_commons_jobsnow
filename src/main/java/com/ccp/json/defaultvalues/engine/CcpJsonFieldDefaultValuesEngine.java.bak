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
 * Engine singleton que preenche, no JSON em tratamento, os campos anotados com
 * {@code CcpJsonFieldDefaultValue} que não foram informados. Espelha a mecânica da engine de
 * validação, inclusive o respeito a {@code CcpJsonCopyFieldValidationsFrom}, mas ao invés de
 * acusar erro, grava valor.
 */
public class CcpJsonFieldDefaultValuesEngine {

	private CcpJsonFieldDefaultValuesEngine() {}

	public static final CcpJsonFieldDefaultValuesEngine INSTANCE = new CcpJsonFieldDefaultValuesEngine();

	/**
	 * Devolve o JSON acrescido dos valores padrão declarados nos campos da classe portadora das regras.
	 * Campos já presentes no JSON são preservados como estão.
	 * @param clazz a classe portadora das regras (a mesma usada na validação)
	 * @param json o JSON em tratamento
	 */
	public CcpJsonRepresentation putDefaultValues(Class<?> clazz, CcpJsonRepresentation json) {

		Field[] declaredFields = clazz.getDeclaredFields();

		CcpJsonRepresentation jsonWithDefaultValues = json;

		for (Field field : declaredFields) {
			jsonWithDefaultValues = this.putDefaultValue(field, jsonWithDefaultValues);
		}

		return jsonWithDefaultValues;
	}

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
	 * Um único valor vira String; dois ou mais viram lista de Strings.
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

	private CcpJsonRepresentation getJsonFromProducer(CcpJsonFieldDefaultValue annotation, CcpJsonRepresentation json) {

		Class<? extends CcpBusiness> jsonProducer = annotation.jsonProducer();
		CcpReflectionConstructorDecorator crcd = new CcpReflectionConstructorDecorator(jsonProducer);
		CcpBusiness business = crcd.newInstance();
		CcpJsonRepresentation producedJson = business.execute(json);

		return producedJson;
	}

}
