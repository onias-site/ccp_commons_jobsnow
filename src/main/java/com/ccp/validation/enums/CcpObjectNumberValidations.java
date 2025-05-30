package com.ccp.validation.enums;

import com.ccp.decorators.CcpJsonRepresentation;

public enum CcpObjectNumberValidations implements CcpBoundValidations{

	equalsTo
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().numbersThenEachOneIs().equalsTo(bound);
		}
	},
	equalsOrGreaterThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().numbersThenEachOneIs().equalsOrGreaterThan(bound);
		}
	},
	equalsOrLessThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().numbersThenEachOneIs().equalsOrLessThan(bound);
		}
	},
	greaterThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().numbersThenEachOneIs().greaterThan(bound);
		}
	},
	lessThan
	{
		public boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields) {
			return json.itIsTrueThatTheFollowingFields(fields).ifTheyAreAll().numbersThenEachOneIs().lessThan(bound);
		}
	},
	;
}
