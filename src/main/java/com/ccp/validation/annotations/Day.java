package com.ccp.validation.annotations;

import com.ccp.validation.enums.DayValidations;

public @interface Day {
	DayValidations rule();
	String[] fields();
	int bound();

}
