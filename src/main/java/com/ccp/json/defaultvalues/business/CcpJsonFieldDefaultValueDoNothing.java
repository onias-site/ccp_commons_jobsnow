package com.ccp.json.defaultvalues.business;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Pass-through {@code CcpBusiness}: returns the JSON it receives, unchanged. Same as {@code CcpOtherConstants.DO_NOTHING}
 * but as a named class instead of a lambda, so it can be the default value of {@code jsonProducer} of
 * {@code CcpJsonFieldDefaultValue} (annotations only accept class literals).
 */
public class CcpJsonFieldDefaultValueDoNothing implements CcpBusiness {

	/**
	 * Returns the JSON unchanged.
	 * @param json the JSON
	 * @return the same JSON
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}
}
