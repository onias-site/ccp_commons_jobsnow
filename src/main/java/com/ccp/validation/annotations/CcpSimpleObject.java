package com.ccp.validation.annotations;

import com.ccp.validation.enums.CcpSimpleObjectValidations;

public @interface CcpSimpleObject {
	CcpSimpleObjectValidations rule ();
	String[] fields();
}

