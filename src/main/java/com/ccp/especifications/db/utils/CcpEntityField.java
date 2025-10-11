package com.ccp.especifications.db.utils;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public class CcpEntityField implements CcpJsonFieldName{

	public static final CcpEntityField TIMESTAMP = new CcpEntityField("timestamp", false, CcpOtherConstants.DO_NOTHING);
	public static final CcpEntityField DATE = new CcpEntityField("date", false, CcpOtherConstants.DO_NOTHING);
	
	public final Function<CcpJsonRepresentation, CcpJsonRepresentation> transformer;
	public final boolean primaryKey;
	public final String name;
	
	public CcpEntityField(String name, boolean primaryKey, Function<CcpJsonRepresentation, CcpJsonRepresentation> transformer) {
		this.transformer = transformer;
		this.primaryKey = primaryKey;
		this.name = name;
	}

	public String name() {
		return this.name;
	}
}
