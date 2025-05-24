package com.ccp.validation.annotations;

import com.ccp.validation.enums.SimpleArrayValidations;

public @interface SimpleArray {
	SimpleArrayValidations rule ();
	String[] fields();
}
