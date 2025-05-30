package com.ccp.validation.annotations;

import com.ccp.validation.enums.CcpObjectNumberValidations;

public @interface CcpObjectNumbers {
	CcpObjectNumberValidations rule();
	String[] fields();
	double bound();

}
