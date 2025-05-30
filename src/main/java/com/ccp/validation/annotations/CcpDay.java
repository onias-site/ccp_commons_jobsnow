package com.ccp.validation.annotations;

import com.ccp.validation.enums.CcpDayValidations;

public @interface CcpDay {
	CcpDayValidations rule();
	String[] fields();
	int bound();

}
