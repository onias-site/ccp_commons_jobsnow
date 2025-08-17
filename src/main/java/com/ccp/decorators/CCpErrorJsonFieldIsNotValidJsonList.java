package com.ccp.decorators;

import java.util.Arrays;

@SuppressWarnings("serial")
public class CCpErrorJsonFieldIsNotValidJsonList extends RuntimeException {
	public CCpErrorJsonFieldIsNotValidJsonList(CcpJsonRepresentation json, Class<?> clazz, String... path) {
		super("The field path '" + Arrays.asList(path) + "' in the json does not represent a json type, instead, it represents a '" + clazz.getName() + "' in the json " + json);
	}
}
