package com.ccp.validation.annotations;

import com.ccp.validation.enums.ArraySizeValidations;

public @interface ArraySize {
	ArraySizeValidations rule();
	String[] fields();
	double bound();

}
