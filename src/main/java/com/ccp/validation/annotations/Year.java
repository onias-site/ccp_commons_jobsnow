package com.ccp.validation.annotations;

import com.ccp.validation.enums.YearValidations;

public @interface Year {
	YearValidations rule();
	String[] fields();
	int bound();

}
