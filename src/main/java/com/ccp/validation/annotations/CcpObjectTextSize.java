package com.ccp.validation.annotations;

import com.ccp.validation.enums.CcpObjectTextSizeValidations;

public @interface CcpObjectTextSize {

	CcpObjectTextSizeValidations rule();
	String[] fields();
	double bound();

}
