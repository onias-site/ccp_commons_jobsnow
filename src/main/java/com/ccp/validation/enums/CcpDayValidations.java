package com.ccp.validation.enums;

public enum CcpDayValidations implements CcpBoundValidations, TimeEnlapsedValidations{
	equalsTo 
	{
		public boolean isTrue(Double bound, Double value) {
			return value.equals(bound);
		}
	},
	equalsOrGreaterThan
	{
		public boolean isTrue(Double bound, Double value) {
			return bound >= value;
		}
	},
	equalsOrLessThan
	{
		public boolean isTrue(Double bound, Double value) {
			return bound <= value;
		}
	},
	greaterThan
	{
		public boolean isTrue(Double bound, Double value) {
			return bound > value;
		}
	},
	lessThan
	{
		public boolean isTrue(Double bound, Double value) {
			return bound < value;
		}
	},
	;
	

}
