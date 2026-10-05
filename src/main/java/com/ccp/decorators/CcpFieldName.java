package com.ccp.decorators;


/**
 * Concrete {@code CcpJsonFieldName} that wraps any value as a JSON field name, so field identifiers can be created
 * dynamically from strings or other objects (for instance, numeric status codes).
 */
public final class CcpFieldName implements CcpJsonFieldName{

	/** The wrapped value; its text form is the field name. */
	private final Object name;

	/**
	 * Wraps any value as a field name.
	 * @param name the value whose text form is the field name
	 */
	public CcpFieldName(Object name) {
		this.name = name;
	}
	
	/**
	 * Wraps a text as a field name.
	 * @param name the field name
	 */
	public CcpFieldName(String name) {
		this((Object)name);
	}

	/**
	 * Returns the text form of the wrapped value ({@code "null"} for {@code null}).
	 * @return the field name
	 */
	public String name() {
		String nameAsText = "" + this.name;
		return nameAsText;
	}

	/**
	 * Returns the field name.
	 * @return the field name
	 */
	public String toString() {
		String name = this.name();
		return name;
	}
}
