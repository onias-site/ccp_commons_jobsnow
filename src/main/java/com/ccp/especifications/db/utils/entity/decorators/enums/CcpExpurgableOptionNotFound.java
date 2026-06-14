package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class CcpExpurgableOptionNotFound extends RuntimeException{

	public final String format;

	public CcpExpurgableOptionNotFound(String format) {
		super("The format '" + format + "' whas not found in the following list: " + Arrays.asList(CcpEntityExpurgableOptions.values())
		.stream().map(x -> x.format).collect(Collectors.toList())
		);
		this.format = format;
	}
	
	
	
}
