package com.ccp.validation.enums;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.validation.CcpJsonFieldsValidations;
import com.ccp.validation.annotations.CcpSimpleObject;
enum CcpSimpleObjectValidationsConstants{
	errors, wrongFields, value, name
	
}
public enum CcpSimpleObjectValidations {
	requiredFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.getDynamicVersion().containsAllFields(fields);
		}
	},
	requiredAtLeastOne {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.getDynamicVersion().containsAnyFields(fields);
		}
		
		public CcpJsonRepresentation validate(CcpJsonRepresentation json, CcpJsonRepresentation result, CcpSimpleObject validation) {
			
			String[] fields = validation.fields();
			
			boolean containsAnyFields = json.getDynamicVersion().containsAnyFields(fields);
			
			if(containsAnyFields) {
				return result;
			}
			
			String completeRuleName = CcpJsonFieldsValidations.getCompleteRuleName(CcpSimpleObject.class, this);
			CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON.addToList("wrongFields", (Object[])fields);
			result = result.addToItem(CcpSimpleObjectValidationsConstants.errors, completeRuleName, errors);
			return result;

		}
	},
	booleanFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.getDynamicVersion().itIsTrueThatTheFollowingFields(fields).areAllOfTheType().bool();
		}
	},
	doubleFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.getDynamicVersion().itIsTrueThatTheFollowingFields(fields).areAllOfTheType().doubleNumber();
		}
	},
	jsonFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.getDynamicVersion().itIsTrueThatTheFollowingFields(fields).areAllOfTheType().json();
		}
	},
	listFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.getDynamicVersion().itIsTrueThatTheFollowingFields(fields).areAllOfTheType().list();
		}
	},
	integerFields {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.getDynamicVersion().itIsTrueThatTheFollowingFields(fields).areAllOfTheType().longNumber();
		}
	},
	nonRepeatedLists {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			return json.getDynamicVersion().itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().hasNonDuplicatedItems();
		}
	},
	;
	public abstract boolean isValidJson(CcpJsonRepresentation json, String... fields);
	
	public CcpJsonRepresentation validate(CcpJsonRepresentation json, CcpJsonRepresentation result,
			CcpSimpleObject validation) {
		String[] fields = validation.fields();
		
		
		String completeRuleName = CcpJsonFieldsValidations.getCompleteRuleName(CcpSimpleObject.class, this);
		
		CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON;
		
		for (String field : fields) {
			
			boolean validJson = this.isValidJson(json,  field);
			
			if(validJson) {
				continue;
			}
			
			Object value = json.content.get(field);
			CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
					.put(CcpSimpleObjectValidationsConstants.name, field)
					.put(CcpSimpleObjectValidationsConstants.value, value)
					;
			errors = errors.addToList(CcpSimpleObjectValidationsConstants.wrongFields, fieldDetails);
			result = result.addToItem(CcpSimpleObjectValidationsConstants.errors, completeRuleName, errors);
		}
		return result;
	}

}
