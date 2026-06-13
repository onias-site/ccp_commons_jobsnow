package com.ccp.especifications.mensageria.sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

/**
 * Contrato para publicação de mensagens em tópicos GCP PubSub. Valida as mensagens antes de publicar
 * usando a engine de validação JSON.
 */
public interface CcpMensageriaSender {

	/**
	 * Converte lista para array e delega ao método varargs.
	 * @param topic nome do tópico PubSub
	 * @param jsonValidationClass classe de validação do JSON
	 * @param msgs lista de mensagens a publicar
	 * @return this para encadeamento
	 */
	default CcpMensageriaSender sendToMensageria(String topic, Class<?> jsonValidationClass, List<CcpJsonRepresentation> msgs) {
		int size = msgs.size();
		CcpJsonRepresentation[] a = new CcpJsonRepresentation[size];
		CcpJsonRepresentation[] array = msgs.toArray(a);
		CcpMensageriaSender send = this.sendToMensageria(topic, jsonValidationClass, array);
		return send;
	}
	
	/**
	 * Valida cada JSON e converte para String antes de publicar no tópico.
	 * @param topic nome do tópico PubSub
	 * @param jsonValidationClass classe de validação do JSON
	 * @param msgs mensagens a validar e publicar
	 * @return this para encadeamento
	 */
	default CcpMensageriaSender sendToMensageria(String topic, Class<?> jsonValidationClass, CcpJsonRepresentation... msgs) {
		
		String[] array = Arrays.asList(msgs).stream().map(x -> CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, x, topic).asUgglyJson()).collect(Collectors.toList())
		.toArray(new String[msgs.length]);
		CcpMensageriaSender send = this.sendToMensageria(topic, array);
		return send;
	}
	
	/**
	 * Publica as strings diretamente no tópico sem validação adicional.
	 * @param topic nome do tópico PubSub
	 * @param msgs mensagens já serializadas a publicar
	 * @return this para encadeamento
	 */
	CcpMensageriaSender sendToMensageria(String topic, String... msgs);
 
	
	
}
