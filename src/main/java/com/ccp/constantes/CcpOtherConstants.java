package com.ccp.constantes;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpOtherConstants {

	Function<CcpJsonRepresentation, CcpJsonRepresentation> RETURNS_EMPTY_JSON = x -> CcpOtherConstants.EMPTY_JSON;
	Function<CcpJsonRepresentation, CcpJsonRepresentation> DO_NOTHING = json -> json;
	CcpJsonRepresentation EMPTY_JSON = CcpJsonRepresentation.getEmptyJson();
	String[] DELIMITERS = new String[] {"/", "\\", ".","\t", "\n", ":", "," , ";", "!", "?", "[", "]", "{", "}", "<", ">", "=", "(", ")", "'", "`",  "\""};
	

}
