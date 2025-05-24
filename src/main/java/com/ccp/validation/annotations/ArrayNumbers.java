package com.ccp.validation.annotations;

import com.ccp.validation.enums.ArrayNumbersValidations;

public @interface ArrayNumbers {

	ArrayNumbersValidations rule();
	String[] fields();
	double bound();
}
