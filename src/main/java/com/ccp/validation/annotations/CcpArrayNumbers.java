package com.ccp.validation.annotations;

import com.ccp.validation.enums.CcpArrayNumbersValidations;

public @interface CcpArrayNumbers {

	CcpArrayNumbersValidations rule();
	String[] fields();
	double bound();
}
