package com.ccp.json.fields.validations.engine;

import java.lang.reflect.Field;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.fields.validations.annotations.CcpJsonField;
import com.ccp.json.fields.validations.enums.CcpJsonFieldTypes;
import com.ccp.json.fields.validations.enums.CcpJsonFieldValueExtractor;

public class CcpJsonFieldsValidator {
	
	public void validate(Class<?> clazz, CcpJsonRepresentation json) {
		Field[] declaredFields = clazz.getDeclaredFields();
		CcpJsonRepresentation errors =  CcpOtherConstants.EMPTY_JSON;
		try {
			for (Field field : declaredFields) {
				{
					boolean ignoreThisField = field.isAnnotationPresent(CcpJsonField.class) == false;
					
					if(ignoreThisField) {
						continue;
					}
				}
				CcpJsonField jsonField = field.getAnnotation(CcpJsonField.class);

				CcpJsonFieldTypes type = jsonField.type();
				errors = type.validate(errors, json, field, clazz, CcpJsonFieldValueExtractor.fromObject);
			}
		} 
		catch (CcpJsonFieldErrorInterruptValidation e) {
			errors = e.validationResultFromField;
		}
		catch (Exception e) { 
			throw new RuntimeException(e);
		}	
	}
	
}
