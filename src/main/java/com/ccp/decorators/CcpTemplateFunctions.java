package com.ccp.decorators;

import java.util.function.Supplier;

/**
 * Functions available to text templates: each constant supplies, on every call, the text that replaces its
 * placeholder.
 */
public enum CcpTemplateFunctions implements Supplier<String>{
	/** The current time in milliseconds since the epoch, as text. */
	currentTimeMillis {
		/**
		 * Returns the current time in milliseconds as text.
		 * @return the current time in milliseconds
		 */
		public String get() {
			long currentTimeMillis = System.currentTimeMillis();
			String currentTimeMillisAsText = "" + currentTimeMillis;
			return currentTimeMillisAsText;
		}

	};
}
