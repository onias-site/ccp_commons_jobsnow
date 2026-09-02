package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;

class AlreadyTransformedJson extends CcpJsonRepresentation{
	AlreadyTransformedJson(CcpJsonRepresentation json) {
		super(json.content);
	}
	
	public CcpJsonRepresentation redoJson(CcpJsonRepresentation json) {
		AlreadyTransformedJson alreadyTransformedJson = new AlreadyTransformedJson(json);
		return alreadyTransformedJson;
	}
}
