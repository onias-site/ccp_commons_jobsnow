package com.ccp.validation.enums;

import com.ccp.decorators.CcpJsonRepresentation;

public enum CcpArrayTextSizeValidations implements CcpBoundValidations{
	equalsTo
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isTextAndHasSizeThatIs().equalsTo(bound);
		}
	},
	equalsOrGreaterThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isTextAndHasSizeThatIs().equalsOrGreaterThan(bound);
		}
	},
	equalsOrLessThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isTextAndHasSizeThatIs().equalsOrLessThan(bound);
		}
	},
	greaterThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isTextAndHasSizeThatIs().greaterThan(bound);
		}
	},
	lessThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAllArrayValuesThenEachOne().isTextAndHasSizeThatIs().lessThan(bound);
		}
	},
	;

}
