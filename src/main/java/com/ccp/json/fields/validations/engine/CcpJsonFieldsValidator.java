package com.ccp.json.fields.validations.engine;

import java.lang.reflect.Field;
import java.util.List;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.fields.validations.annotations.CcpJsonField;
import com.ccp.json.fields.validations.annotations.CcpJsonFieldArrayType;
import com.ccp.json.fields.validations.enums.CcpJsonFieldTypes;

public class CcpJsonFieldsValidator {
	
	private CcpJsonFieldsValidator() {}
	
	public static final CcpJsonFieldsValidator INSTANCE = new CcpJsonFieldsValidator();
	
	public CcpJsonRepresentation getErrors(Class<?> clazz, CcpJsonRepresentation json) {
		Field[] declaredFields = clazz.getDeclaredFields();
		CcpJsonRepresentation errors =  CcpOtherConstants.EMPTY_JSON;
		for (Field field : declaredFields) {
			try {
				boolean ignoreThisField = false == field.isAnnotationPresent(CcpJsonField.class);

				if (ignoreThisField) {
					continue; 
				}
	
				boolean isNotAnArray = false == field.isAnnotationPresent(CcpJsonFieldArrayType.class);
				
				if(isNotAnArray) {
					CcpJsonField jsonField = field.getAnnotation(CcpJsonField.class);
					CcpJsonFieldTypes type = jsonField.type();
					errors = type.getErrors(errors, json, field);
					continue;
				}

				boolean hasArrayErrors = CcpJsonFieldTypes.Array.hasErrors(json, field);
				
				if(hasArrayErrors) {
					errors = CcpJsonFieldTypes.Array.getErrors(errors, json, field);
					break;
				}
				
				String fieldName = field.getName();
				
				List<Object> asObjectList = json.getDynamicVersion().getAsObjectList(fieldName);
				
				for (Object obj : asObjectList) {
					CcpJsonField jsonField = field.getAnnotation(CcpJsonField.class);
					CcpJsonFieldTypes type = jsonField.type();
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
	
}
