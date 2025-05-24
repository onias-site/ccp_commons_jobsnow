package com.ccp.validation.annotations;

import com.ccp.validation.enums.ObjectNumberValidations;

public @interface ObjectNumbers {
	ObjectNumberValidations rule();
	String[] fields();
	double bound();

}
