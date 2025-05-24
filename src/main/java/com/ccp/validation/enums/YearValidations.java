package com.ccp.validation.enums;

public enum YearValidations implements TimeEnlapsedValidations{
	equalsTo 
	{
		public boolean isTrue(Double bound, Double difference) {
			boolean true1 = bound.equals(difference);
			return true1;
		}
	},
	equalsOrGreaterThan
	{
		public boolean isTrue(Double bound, Double difference) {
			boolean true1 = bound <= difference;
			return true1;
		}
	},
	equalsOrLessThan
	{
		public boolean isTrue(Double bound, Double difference) {
			boolean true1 = bound >= difference;
			return true1;
		}
	},
	greaterThan
	{
		public boolean isTrue(Double bound, Double difference) {
			boolean true1 = bound < difference;
			return true1;
		}
	},
	lessThan
	{
		public boolean isTrue(Double bound, Double difference) {
			boolean true1 = bound > difference;
			return true1;
		}
	},
	;

}
