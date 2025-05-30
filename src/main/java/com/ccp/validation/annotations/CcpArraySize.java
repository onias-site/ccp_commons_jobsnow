package com.ccp.validation.annotations;

import com.ccp.validation.enums.CcpArraySizeValidations;

public @interface CcpArraySize {
	CcpArraySizeValidations rule();
	String[] fields();
	double bound();

}
