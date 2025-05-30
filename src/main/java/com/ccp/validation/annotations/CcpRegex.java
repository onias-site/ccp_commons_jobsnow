package com.ccp.validation.annotations;

import com.ccp.constantes.CcpStringConstants;

public @interface CcpRegex {
	String[] fields();
	CcpStringConstants value();
}
