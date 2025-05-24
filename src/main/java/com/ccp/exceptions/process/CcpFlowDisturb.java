package com.ccp.exceptions.process;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

@SuppressWarnings("serial")
public class CcpFlowDisturb extends RuntimeException{
	
	public final CcpJsonRepresentation json;
	
	public final CcpProcessStatus status;
	
	public final String[] fields;

	public CcpFlowDisturb(CcpProcessStatus status, String... fields) {
		this(CcpOtherConstants.EMPTY_JSON, status, fields);
	}

	public CcpFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, String... fields) {
		super(json.put("statusNumber", status.asNumber()).put("statusName", status.name()).asPrettyJson());
		this.json = json;
		this.status = status;
		this.fields = fields;
	}

	public CcpFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, String message, String... fields) {
		super(message);
		this.json = json;
		this.status = status;
		this.fields = fields;
	}
	
	
}
