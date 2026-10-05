package com.ccp.especifications.json;

/**
 * Contract of JSON serialization and deserialization (Gson): converts Java objects into JSON and back, and tells
 * whether a text is well-formed JSON.
 */
public interface CcpJsonHandler {

	/**
	 * Serializes the object as compact JSON.
	 * @param md the object to serialize
	 * @return the compact JSON text
	 */
	String toJson(Object md);

	/**
	 * Serializes the object as indented JSON.
	 * @param md the object to serialize
	 * @return the indented JSON text
	 */
	String asPrettyJson(Object md);

	/**
	 * Deserializes the JSON text into the inferred type (a map for objects, a list for arrays).
	 * @param <T> the target type
	 * @param md the JSON text
	 * @return the deserialized object
	 */
	<T> T fromJson(String md);

	/**
	 * Tells whether the text is a valid JSON object.
	 * @param src the text
	 * @return {@code true} for a valid JSON object
	 */
	boolean isValidJson(String src);
	
	/**
	 * Tells whether the text is a valid JSON list.
	 * @param src the text
	 * @return {@code true} for a valid JSON list
	 */
	boolean isValidJsonList(String src);
}
