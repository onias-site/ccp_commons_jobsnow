package com.ccp.decorators;

import java.util.Collection;
import java.util.function.Consumer;

import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;

/**
 * Check of {@code CcpStringDecorator.isList}: throws when the text is not a JSON list.
 */
public class CcpStringJsonListCheck implements Consumer<String> {

	/** The only instance: the check keeps no state. */
	public final static CcpStringJsonListCheck INSTANCE = new CcpStringJsonListCheck();

	private CcpStringJsonListCheck() {
	}

	/**
	 * Parses the text as a JSON list.
	 * @param text the text
	 */
	public void accept(String text) {
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		Collection<?> fromJson = jsonHandler.fromJson(text);
		fromJson.toString();
	}
}
