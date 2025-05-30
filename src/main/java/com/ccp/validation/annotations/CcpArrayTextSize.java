package com.ccp.validation.annotations;

import com.ccp.validation.enums.CcpArrayTextSizeValidations;

public @interface CcpArrayTextSize {
	CcpArrayTextSizeValidations rule();
	String[] fields();
	double bound();

}
