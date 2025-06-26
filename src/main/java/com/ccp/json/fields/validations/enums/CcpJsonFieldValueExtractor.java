package com.ccp.json.fields.validations.enums;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;

public enum CcpJsonFieldValueExtractor {
	fromArray {
		public Object getValue(CcpJsonRepresentation json, String fieldName) {
			List<Object> asObjectList = json.getAsObjectList(fieldName);
			return asObjectList;
		}
	},
	fromObject {
		@Override
		public Object getValue(CcpJsonRepresentation json, String fieldName) {
			Object object = json.get(fieldName);
			return object;
		}
	}
	;
	public abstract Object getValue(CcpJsonRepresentation json, String fieldName);
	
}
