package com.ccp.validation.annotations;

import com.ccp.validation.enums.SimpleObjectValidations;

public @interface SimpleObject {
	SimpleObjectValidations rule ();
	String[] fields();
}

