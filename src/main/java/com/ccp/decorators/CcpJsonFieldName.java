package com.ccp.decorators;

/**
 * Name of a JSON field. Usually implemented by enums, so field names are declared as constants instead of bare
 * strings and are passed to the {@code CcpJsonRepresentation} accessors.
 */
public interface CcpJsonFieldName{

	/**
	 * Returns the key used in the JSON map.
	 * @return {@link #name()} by default
	 */
	default String getValue() {
		String name = this.name();
		return name;
	}

	/**
	 * Returns the name of the field (the enum constant name, for enums).
	 * @return the field name
	 */
	String name();
}
