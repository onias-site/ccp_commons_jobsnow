package com.ccp.validation.annotations;

import com.ccp.constantes.CcpStringConstants;

public @interface Regex {
	String[] fields();
	CcpStringConstants value();
}
