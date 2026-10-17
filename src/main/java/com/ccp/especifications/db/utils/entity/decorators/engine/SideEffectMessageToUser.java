package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.annotation.Annotation;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityAnnotationAttribute;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityMessageSenderExceptionHandler;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityMessageType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationPhase;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityMessageTemplate;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntitySideEffect;

/**
 * The side effect of the message annotations: sends to the user the messages named by the annotation, each one a
 * {@code CcpEntityMessageTemplate} instantiated through its no-arg constructor, with the kinds of message and the failure
 * policy the name of the annotation expresses. A failure never cancels the operation: the policy decides what happens.
 */
class SideEffectMessageToUser implements CcpEntitySideEffect {

	/** The attribute that names the messages: {@code value} (writes) or {@code messageTemplate} (transfers). */
	private final CcpEntityAnnotationAttribute messageAttribute;

	/** What happens when the sending fails. */
	private final CcpEntityMessageSenderExceptionHandler exceptionHandler;

	/** The kinds of message sent. */
	private final CcpEntityMessageType[] messageTypes;

	/**
	 * Associates the side effect with the values the name of its annotation expresses.
	 * @param messageAttribute the attribute that names the messages
	 * @param exceptionHandler what happens when the sending fails
	 * @param messageTypes the kinds of message sent
	 */
	SideEffectMessageToUser(CcpEntityAnnotationAttribute messageAttribute, CcpEntityMessageSenderExceptionHandler exceptionHandler, CcpEntityMessageType... messageTypes) {
		this.messageAttribute = messageAttribute;
		this.exceptionHandler = exceptionHandler;
		this.messageTypes = messageTypes;
	}

	/**
	 * Sends the messages named by the annotation, in the order they are declared, chaining their outputs.
	 * @param json the record
	 * @param annotation the annotation instance
	 * @param phase the phase of the side effect (a failure never cancels the operation)
	 * @return the JSON after the sendings
	 */
	public CcpJsonRepresentation execute(CcpJsonRepresentation json, Annotation annotation, CcpEntityOperationPhase phase) {
		Class<?>[] messageClasses = this.getMessageClasses(annotation);
		CcpJsonRepresentation result = json;
		for (Class<?> messageClass : messageClasses) {
			CcpReflectionConstructorDecorator messageConstructor = new CcpReflectionConstructorDecorator(messageClass);
			CcpEntityMessageTemplate message = messageConstructor.newInstance();
			result = message.sendMessage(result, this.exceptionHandler, this.messageTypes);
		}
		return result;
	}

	/**
	 * The messages named by the annotation: the write annotations take an array, the transfer ones a single class.
	 * @param annotation the annotation instance
	 * @return the message classes
	 */
	private Class<?>[] getMessageClasses(Annotation annotation) {
		Object configuredMessages = this.messageAttribute.readFrom(annotation);

		if(configuredMessages instanceof Class<?>[] messageClasses) {
			return messageClasses;
		}

		Class<?> messageClass = (Class<?>) configuredMessages;
		Class<?>[] messageClasses = new Class<?>[] {messageClass};
		return messageClasses;
	}
}
