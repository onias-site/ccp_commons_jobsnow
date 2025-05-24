package com.ccp.validation.annotations;

import com.ccp.validation.enums.ObjectTextSizeValidations;

public @interface ObjectTextSize {

	ObjectTextSizeValidations rule();
	String[] fields();
	double bound();

}
