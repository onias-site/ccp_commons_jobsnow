package com.ccp.validation.annotations;

import com.ccp.validation.enums.CcpAllowedValuesValidations;

public @interface CcpAllowedValues {
	CcpAllowedValuesValidations rule();
	String[] allowedValues();
	String[] fields();
}
