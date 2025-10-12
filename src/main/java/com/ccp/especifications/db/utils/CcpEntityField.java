package com.ccp.especifications.db.utils;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public class CcpEntityField implements CcpJsonFieldName{

	public static final CcpEntityField TIMESTAMP = new CcpEntityField("timestamp", false, CcpOtherConstants.DO_NOTHING);
	public static final CcpEntityField DATE = new CcpEntityField("date", false, CcpOtherConstants.DO_NOTHING);
	
	public final CcpBusiness transformer;
	public final boolean primaryKey;
	public final String name;
	
	public CcpEntityField(String name, boolean primaryKey, CcpBusiness transformer) {
		this.transformer = transformer;
		this.primaryKey = primaryKey;
		this.name = name;
	}

	public String name() {
		return this.name;
	}
}
