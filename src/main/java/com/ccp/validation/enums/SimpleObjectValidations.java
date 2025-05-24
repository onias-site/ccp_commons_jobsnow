package com.ccp.validation.enums;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.validation.CcpJsonFieldsValidations;
import com.ccp.validation.annotations.SimpleObject;

public enum SimpleObjectValidations {
	requiredFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.containsAllFields(fields);
		}
	},
	requiredAtLeastOne {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.containsAnyFields(fields);
		}
		
		public CcpJsonRepresentation validate(CcpJsonRepresentation json, CcpJsonRepresentation result, SimpleObject validation) {
			
			String[] fields = validation.fields();
			
			boolean containsAnyFields = json.containsAnyFields(fields);
			
			if(containsAnyFields) {
				return result;
			}
			
			String completeRuleName = CcpJsonFieldsValidations.getCompleteRuleName(SimpleObject.class, this);
			CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON.addToList("wrongFields", (Object[])fields);
			result = result.addToItem("errors", completeRuleName, errors);
			return result;

		}
	},
	booleanFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).areAllOfTheType().bool();
		}
	},
	doubleFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).areAllOfTheType().doubleNumber();
		}
	},
	jsonFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).areAllOfTheType().json();
		}
	},
	listFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).areAllOfTheType().list();
		}
	},
	integerFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).areAllOfTheType().longNumber();
		}
	},
	nonRepeatedLists {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().hasNonDuplicatedItems();
		}
	},
	;
	public abstract boolean isValidJson(CcpJsonRepresentation json, String... fields);
	
	public CcpJsonRepresentation validate(CcpJsonRepresentation json, CcpJsonRepresentation result,
			SimpleObject validation) {
		String[] fields = validation.fields();
		
		
		String completeRuleName = CcpJsonFieldsValidations.getCompleteRuleName(SimpleObject.class, this);
		
		CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON;
		
		for (String field : fields) {
			
			boolean validJson = this.isValidJson(json,  field);
			
			if(validJson) {
				continue;
			}
			
			Object value = json.content.get(field);
			CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
					.put("name", field)
					.put("value", value)
					;
			errors = errors.addToList("wrongFields", fieldDetails);
			result = result.addToItem("errors", completeRuleName, errors);
		}
		return result;
	}

}
