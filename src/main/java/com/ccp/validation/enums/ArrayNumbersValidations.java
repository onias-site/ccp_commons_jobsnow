package com.ccp.validation.enums;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.validation.IfTheyAreArrayValuesSoEachOne;
import com.ccp.validation.ItIsTrueThatTheFollowingFields;
import com.ccp.validation.RangeSize;

public enum ArrayNumbersValidations implements BoundValidations{
	equalsTo
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			ItIsTrueThatTheFollowingFields itIsTrueThatTheFollowingFields = json.itIsTrueThatTheFollowingFields(fields);
			IfTheyAreArrayValuesSoEachOne ifTheyAreAllArrayValuesThenEachOne = itIsTrueThatTheFollowingFields.ifTheyAreAllArrayValuesThenEachOne();
			RangeSize numberAndItIs = ifTheyAreAllArrayValuesThenEachOne.isNumberAndItIs();
			return numberAndItIs.equalsTo(bound);
		}
	},
	equalsOrGreaterThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().equalsOrGreaterThan(bound);
		}
	},
	equalsOrLessThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().equalsOrLessThan(bound);
		}
	},
	greaterThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().greaterThan(bound);
		}
	},
	lessThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().lessThan(bound);
		}
	},
	;

}
