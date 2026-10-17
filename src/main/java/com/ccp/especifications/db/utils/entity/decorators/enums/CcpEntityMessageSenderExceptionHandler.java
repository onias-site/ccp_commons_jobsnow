package com.ccp.especifications.db.utils.entity.decorators.enums;

/**
 * What happens when a message an entity annotation sends to the user fails; the {@code CcpEntityMessageTemplate}
 * applies it.
 */
public enum CcpEntityMessageSenderExceptionHandler {
	/** The failure is thrown. */
	THROWS,
	/** The failure is recorded as a warning and the flow goes on. */
	LENIENT,
	/** The failure is recorded as a warning, its stack trace is printed and the flow goes on. */
	LOG
}
