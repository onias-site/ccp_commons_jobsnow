package com.ccp.validation.annotations;

import com.ccp.validation.enums.AllowedValuesValidations;

public @interface AllowedValues {
	AllowedValuesValidations rule();
	String[] allowedValues();
	String[] fields();
}
