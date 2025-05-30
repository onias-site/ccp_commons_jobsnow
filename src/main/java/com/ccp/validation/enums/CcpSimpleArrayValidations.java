package com.ccp.validation.enums;

import com.ccp.decorators.CcpJsonRepresentation;

public enum CcpSimpleArrayValidations {

	notEmptyArray{

		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			boolean x = json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().hasTheSizeThatIs().greaterThan(0d);
			return x;
		}},
	
	booleanItems {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			boolean x = json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isOfTheType().bool();
			return x;
		}
	},
	doubleItems {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			boolean x = json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isOfTheType().doubleNumber();
			return x;
		}
	},
	jsonItems {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			boolean x = json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isOfTheType().json();
			return x;
		}
	},
	listItems {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			boolean x = json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isOfTheType().list();
			return x;
		}
	},
	integerItems {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			boolean x = json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isOfTheType().longNumber();
			return x;
		}
	},
	nonRepeatedItems {
		
		public boolean isValidJson(CcpJsonRepresentation json, String... fields) {
			boolean x = json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().hasNonDuplicatedItems();
			return x;
		}
	},
	;
	public abstract boolean isValidJson(CcpJsonRepresentation json, String... fields);
}
