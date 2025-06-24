package com.ccp.json.fields.validations.engine;

import java.lang.reflect.Field;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.fields.validations.annotations.CcpJsonField;
import com.ccp.json.fields.validations.enums.CcpJsonFieldRequiredOptions;
import com.ccp.json.fields.validations.enums.CcpJsonFieldStatusType;

public class CcpJsonFieldsValidator {

	
	public void validate(Class<?> clazz, CcpJsonRepresentation json) {
		
		Field[] declaredFields = clazz.getDeclaredFields();
		try {
			for (Field field : declaredFields) {
				field.setAccessible(true);
				boolean ignoreThisField = field.isAnnotationPresent(CcpJsonField.class) == false;
				
				if(ignoreThisField) {
					continue;
				}
				
				CcpJsonField jsonField = field.getAnnotation(CcpJsonField.class);
				CcpJsonFieldRequiredOptions required = jsonField.required();
				String fieldName = field.getName();
				CcpJsonFieldStatusType validate = required.validate(fieldName, json);
				
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}
	
}
