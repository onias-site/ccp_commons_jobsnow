package com.ccp.constantes;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.mensageria.receiver.CcpBusiness;

public interface CcpOtherConstants {

	CcpBusiness RETURNS_EMPTY_JSON = x -> CcpOtherConstants.EMPTY_JSON;
	CcpBusiness DO_NOTHING = json -> json;
	CcpJsonRepresentation EMPTY_JSON = CcpJsonRepresentation.getEmptyJson();
	String[] DELIMITERS = new String[] {"/", "\\", ".","\t", "\n", ":", "," , ";", "!", "?", "[", "]", "{", "}", "<", ">", "=", "(", ")", "'", "`",  "\""};
	

}
