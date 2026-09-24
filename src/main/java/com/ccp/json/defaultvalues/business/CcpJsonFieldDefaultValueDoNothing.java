package com.ccp.json.defaultvalues.business;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * {@code CcpBusiness} pass-through: devolve o mesmo JSON que recebeu, sem modificação.
 * Equivale a {@code CcpOtherConstants.DO_NOTHING}, porém como classe nomeada, e não como lambda,
 * para que possa ser usada como valor padrão do atributo {@code jsonProducer} de
 * {@code CcpJsonFieldDefaultValue} (anotações só aceitam literais de classe).
 */
public class CcpJsonFieldDefaultValueDoNothing implements CcpBusiness {

	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		return json;
	}
}
