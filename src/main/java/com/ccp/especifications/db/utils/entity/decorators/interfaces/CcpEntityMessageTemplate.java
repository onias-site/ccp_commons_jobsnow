package com.ccp.especifications.db.utils.entity.decorators.interfaces;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityMessageSenderExceptionHandler;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityMessageType;

/**
 * A message to the user named by the message annotations of an entity (such as
 * {@code @CcpAfterSaveFromMainEntitySendAnEmailMessageAndIfFailsSaveAWarning}). The class given to the annotation is
 * instantiated through its no-arg constructor and sends itself, so the annotation needs no other collaborator: whoever
 * defines the message also defines how it is delivered.
 */
public interface CcpEntityMessageTemplate {

	/**
	 * Sends this message to the user.
	 * @param json the record, which feeds the message
	 * @param exceptionHandler what happens when the sending fails
	 * @param messageTypes the kinds of message sent
	 * @return the JSON after the sending
	 */
	CcpJsonRepresentation sendMessage(CcpJsonRepresentation json, CcpEntityMessageSenderExceptionHandler exceptionHandler, CcpEntityMessageType... messageTypes);
}
