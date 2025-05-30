package com.ccp.validation.annotations;

import com.ccp.validation.enums.YearValidations;

public @interface CcpYear {
	YearValidations rule();
	String[] fields();
	int bound();

}
