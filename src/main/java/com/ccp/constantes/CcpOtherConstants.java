package com.ccp.constantes;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.mensageria.receiver.CcpBusiness;

public interface CcpOtherConstants {

	Function<CcpJsonRepresentation, CcpJsonRepresentation> RETURNS_EMPTY_JSON = x -> CcpOtherConstants.EMPTY_JSON;
	CcpBusiness DO_NOTHING = json -> json;
	CcpJsonRepresentation EMPTY_JSON = CcpJsonRepresentation.getEmptyJson();
	String[] DELIMITERS = new String[] {"/", "\\", ".","\t", "\n", ":", "," , ";", "!", "?", "[", "]", "{", "}", "<", ">", "=", "(", ")", "'", "`",  "\""};
	

}
