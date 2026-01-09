package com.ccp.constantes;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpOtherConstants {

	CcpBusiness RETURNS_EMPTY_JSON = x -> CcpOtherConstants.EMPTY_JSON;
	CcpBusiness DO_NOTHING = json -> json;
	CcpJsonRepresentation EMPTY_JSON = CcpJsonRepresentation.getEmptyJson();
	String[] DELIMITERS_ARRAY = new String[] {"/", "\\", ".","\t", "\n", ":", "," , ";", "!", "?", "[", "]", "{", "}", "<", ">", "=", "(", ")", "'", "`",  "\""};
	String DELIMITERS = "\r|\t|\n|\\s|\\:|\\,|\\-|\\;|\\!|\\?|\\[|\\]|\\{|\\}|\\<|\\>|\\=|\\(|\\)\\ |\\'|\\\"|\\`";

}
