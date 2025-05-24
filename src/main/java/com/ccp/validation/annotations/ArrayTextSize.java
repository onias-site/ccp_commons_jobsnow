package com.ccp.validation.annotations;

import com.ccp.validation.enums.ArrayTextSizeValidations;

public @interface ArrayTextSize {
	ArrayTextSizeValidations rule();
	String[] fields();
	double bound();

}
