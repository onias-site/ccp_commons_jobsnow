package com.ccp.especifications.mensageria.sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

public interface CcpMensageriaSender {

	default CcpMensageriaSender sendToMensageria(String topic, Class<?> jsonValidationClass, List<CcpJsonRepresentation> msgs) {
		int size = msgs.size();
		CcpJsonRepresentation[] a = new CcpJsonRepresentation[size];
		CcpJsonRepresentation[] array = msgs.toArray(a);
		CcpMensageriaSender send = this.sendToMensageria(topic, jsonValidationClass, array);
		return send;
	}
	
	default CcpMensageriaSender sendToMensageria(String topic, Class<?> jsonValidationClass, CcpJsonRepresentation... msgs) {
		
		String[] array = Arrays.asList(msgs).stream().map(x -> CcpJsonValidatorEngine.INSTANCE.validateJson(getClass(), x, topic).asUgglyJson()).collect(Collectors.toList())
		.toArray(new String[msgs.length]);
		CcpMensageriaSender send = this.sendToMensageria(topic, array);
		return send;
	}
	
	CcpMensageriaSender sendToMensageria(String topic, String... msgs);
 
	
	
}
