package com.ccp.decorators;

import java.util.function.Consumer;

/**
 * Check of {@code CcpStringDecorator.isLongNumber}: throws when the text is neither parseable as {@code long} nor a
 * decimal ending with {@code ".0"}.
 */
public class CcpStringLongNumberCheck implements Consumer<String> {

	/** The only instance: the check keeps no state. */
	public final static CcpStringLongNumberCheck INSTANCE = new CcpStringLongNumberCheck();

	private CcpStringLongNumberCheck() {
	}

	/**
	 * Parses the text as a number.
	 * @param text the text
	 */
	@SuppressWarnings("unused")
	public void accept(String text) {
		boolean endsWithDecimalZero = text.endsWith(".0");
		Object parsedNumber = endsWithDecimalZero ? Double.valueOf(text) : Long.valueOf(text);
	}
}
