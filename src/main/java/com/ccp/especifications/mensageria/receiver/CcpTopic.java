package com.ccp.especifications.mensageria.receiver;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpTopic extends Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	default boolean canSave() {
		return true;
	}

//	
//	default CcpJsonRepresentation sendToMensageria(CcpJsonRepresentation json) {
//		JnMensageriaSender jms = new JnMensageriaSender(this);
//		CcpJsonRepresentation apply = jms.apply(json);
//		return apply;
//	}
}
