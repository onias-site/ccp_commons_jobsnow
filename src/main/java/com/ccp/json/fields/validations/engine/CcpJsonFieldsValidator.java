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
		try {
			for (Field field : declaredFields) {
				
				boolean ignoreThisField = false == field.isAnnotationPresent(CcpJsonField.class);

				if (ignoreThisField) {
					continue; 
				}
	
				boolean isNotAnArray = false == field.isAnnotationPresent(CcpJsonFieldArrayType.class);
				
				if(isNotAnArray) {
					CcpJsonField jsonField = field.getAnnotation(CcpJsonField.class);
					CcpJsonFieldTypes type = jsonField.type();
					errors = type.evaluate(errors, json, field);
					continue;
				}
				
				String fieldName = field.getName();
				
				List<Object> asObjectList = json.getDynamicVersion().getAsObjectList(fieldName);
				
				for (Object obj : asObjectList) {
					CcpJsonField jsonField = field.getAnnotation(CcpJsonField.class);
					CcpJsonFieldTypes type = jsonField.type();
					CcpJsonRepresentation put = json.getDynamicVersion().put(fieldName, obj);
					
					boolean hasNoErrors = false == type.hasErrors(json, field);
					if(hasNoErrors) {
						continue;
					}
					errors = type.evaluate(errors, put, field);
					break;
				}
			}
		} 
		catch (CcpJsonFieldErrorInterruptValidation e) {
			errors = e.validationResultFromField;
		}
		catch (Exception e) { 
			throw new RuntimeException(e);
		}
		
		return errors;
	}
	
}
