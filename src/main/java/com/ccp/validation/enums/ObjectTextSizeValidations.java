package com.ccp.validation.enums;

import com.ccp.decorators.CcpJsonRepresentation;

public enum ObjectTextSizeValidations implements BoundValidations{
	equalsTo
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().equalsTo(bound);
		}
	},
	equalsOrGreaterThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().equalsOrGreaterThan(bound);
		}
	},
	equalsOrLessThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().equalsOrLessThan(bound);
		}
	},
	greaterThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().greaterThan(bound);
		}
	},
	lessThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().lessThan(bound);
		}
	},
	;
}
