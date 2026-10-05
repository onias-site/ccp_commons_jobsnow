package com.ccp.especifications.mensageria.sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;
import java.util.stream.Stream;

/**
 * Contract for publishing messages to GCP PubSub topics. The JSON variants validate each message with the JSON
 * validation engine before publishing.
 */
public interface CcpMensageriaSender {

	/**
	 * List variant of {@link #sendToMensageria(String, Class, CcpJsonRepresentation...)}.
	 * @param topic the PubSub topic
	 * @param jsonValidationClass the validation class of the messages
	 * @param msgs the messages to publish
	 * @return this sender
	 */
	default CcpMensageriaSender sendToMensageria(String topic, Class<?> jsonValidationClass, List<CcpJsonRepresentation> msgs) {
		int size = msgs.size();
		CcpJsonRepresentation[] a = new CcpJsonRepresentation[size];
		CcpJsonRepresentation[] array = msgs.toArray(a);
		CcpMensageriaSender send = this.sendToMensageria(topic, jsonValidationClass, array);
		return send;
	}
	
	/**
	 * Validates every message against the validation class (all of them before publishing any) and publishes their
	 * compact JSON texts.
	 * @param topic the PubSub topic, also used as the feature name in validation errors
	 * @param jsonValidationClass the validation class of the messages
	 * @param msgs the messages to publish
	 * @return this sender
	 * @throws com.ccp.json.validations.global.engine.CcpJsonValidationError when a message breaks a rule
	 */
	default CcpMensageriaSender sendToMensageria(String topic, Class<?> jsonValidationClass, CcpJsonRepresentation... msgs) {
		Stream<CcpJsonRepresentation> stream = Arrays.asList(msgs).stream();
		var streamMap = stream.map(x -> CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, x, topic).asUgglyJson());
		var collect = streamMap.collect(Collectors.toList());

		String[] array = collect
		.toArray(new String[msgs.length]);
		CcpMensageriaSender send = this.sendToMensageria(topic, array);
		return send;
	}
	
	/**
	 * Publishes the already serialized messages, without validation.
	 * @param topic the PubSub topic
	 * @param msgs the serialized messages
	 * @return this sender
	 */
	CcpMensageriaSender sendToMensageria(String topic, String... msgs);
 
	
	
}
