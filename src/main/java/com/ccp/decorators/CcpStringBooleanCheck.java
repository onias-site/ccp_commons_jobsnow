package com.ccp.decorators;

import java.util.function.Consumer;

/**
 * Check of {@code CcpStringDecorator.isBoolean}: throws when the text is neither {@code "true"} nor {@code "false"}
 * (case insensitive).
 */
public class CcpStringBooleanCheck implements Consumer<String> {

	/** The only instance: the check keeps no state. */
	public final static CcpStringBooleanCheck INSTANCE = new CcpStringBooleanCheck();

	private CcpStringBooleanCheck() {
	}

	/**
	 * Compares the text with {@code "true"} and {@code "false"}.
	 * @param text the text
	 */
	public void accept(String text) {
		boolean isTrue = "true".equalsIgnoreCase(text);
		if (isTrue) {
			return;
		}
		boolean isFalse = "false".equalsIgnoreCase(text);
		if (isFalse) {
			return;
		}
		CcpErrorStringIsNotBoolean notBooleanError = new CcpErrorStringIsNotBoolean();
		throw notBooleanError;
	}

	/** Internal signal for a text that is neither {@code true} nor {@code false}. */
	@SuppressWarnings("serial")
	private static class CcpErrorStringIsNotBoolean extends RuntimeException {
	}
}
