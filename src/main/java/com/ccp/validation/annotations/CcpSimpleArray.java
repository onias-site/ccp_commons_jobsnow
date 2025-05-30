package com.ccp.validation.annotations;

import com.ccp.validation.enums.CcpSimpleArrayValidations;

public @interface CcpSimpleArray {
	CcpSimpleArrayValidations rule ();
	String[] fields();
}
