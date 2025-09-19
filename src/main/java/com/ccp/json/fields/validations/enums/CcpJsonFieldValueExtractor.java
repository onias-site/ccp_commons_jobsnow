package com.ccp.json.fields.validations.enums;

import java.lang.reflect.Field;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;

public enum CcpJsonFieldValueExtractor {
	fromArray {
		@SuppressWarnings("unchecked")
		public <T> T getValue(CcpJsonRepresentation json, Field field) {
			String fieldName = field.getName();
			List<Object> asObjectList = json.getDynamicVersion().getAsObjectList(fieldName);
			return (T)asObjectList;
		}
	},
	fromObject {
		@SuppressWarnings("unchecked")
		@Override
		public <T> T getValue(CcpJsonRepresentation json, Field field) {
			String fieldName = field.getName();
			Object object = json.getDynamicVersion().get(fieldName);
			return (T)object;
		}
	}
	;
	public abstract <T> T getValue(CcpJsonRepresentation json, Field field);
	
}
