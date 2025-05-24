package com.ccp.especifications.mensageria.sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpMensageriaSender {

	default CcpMensageriaSender send(String topic, List<CcpJsonRepresentation> msgs) {
		int size = msgs.size();
		CcpJsonRepresentation[] a = new CcpJsonRepresentation[size];
		CcpJsonRepresentation[] array = msgs.toArray(a);
		CcpMensageriaSender send = this.send(topic, array);
		return send;
	}
	
	default CcpMensageriaSender send(String topic, CcpJsonRepresentation... msgs) {
		String[] array = Arrays.asList(msgs).stream().map(x -> x.asUgglyJson()).collect(Collectors.toList())
		.toArray(new String[msgs.length]);
		CcpMensageriaSender send = this.send(topic, array);
		return send;
	}
	
	CcpMensageriaSender send(String topic, String... msgs);
 
	
	
}
