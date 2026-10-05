package com.ccp.decorators;


/**
 * Raised when a field of a {@code CcpJsonRepresentation} is accessed as a JSON (or list of JSONs) but its actual value
 * has another, incompatible type (for instance a {@code Long} or a plain {@code String}).
 */
@SuppressWarnings("serial")
public class CCpErrorJsonFieldIsNotValidJsonList extends RuntimeException {
	/**
	 * Builds the error message naming the accessed field path, the type found and the full JSON at the moment of the error.
	 * @param json the JSON at the moment of the error
	 * @param clazz the actual type found in the field
	 * @param path the accessed field path
	 */
	CCpErrorJsonFieldIsNotValidJsonList(CcpJsonRepresentation json, Class<?> clazz, String... path) {
		super("The field path '" + java.util.Arrays.asList(path) + "' in the json does not represent a json type, instead, it represents a '" + clazz.getName() + "' in the json " + json);
	}
}
