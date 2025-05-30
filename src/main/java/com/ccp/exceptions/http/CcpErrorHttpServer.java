package com.ccp.exceptions.http;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpErrorHttpServer extends CcpErrorHttp{

	public CcpErrorHttpServer(CcpJsonRepresentation entity) {
		super(entity);
	}


}
