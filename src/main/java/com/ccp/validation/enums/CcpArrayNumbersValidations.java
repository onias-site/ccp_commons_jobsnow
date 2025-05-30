package com.ccp.validation.enums;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.validation.CcpIfTheyAreArrayValuesSoEachOne;
import com.ccp.validation.CcpItIsTrueThatTheFollowingFields;
import com.ccp.validation.CcpRangeSize;

public enum CcpArrayNumbersValidations implements CcpBoundValidations{
	equalsTo
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			CcpItIsTrueThatTheFollowingFields itIsTrueThatTheFollowingFields = json.itIsTrueThatTheFollowingFields(fields);
			CcpIfTheyAreArrayValuesSoEachOne ifTheyAreAllArrayValuesThenEachOne = itIsTrueThatTheFollowingFields.ifTheyAreAllArrayValuesThenEachOne();
			CcpRangeSize numberAndItIs = ifTheyAreAllArrayValuesThenEachOne.isNumberAndItIs();
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
