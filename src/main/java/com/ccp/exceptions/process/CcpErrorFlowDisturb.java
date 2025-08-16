package com.ccp.exceptions.process;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;
enum CcpErrorFlowDisturbConstants  implements CcpJsonFieldName{
	reason, statusNumber, statusName
	
	
}
@SuppressWarnings("serial")
public class CcpErrorFlowDisturb extends RuntimeException{
	
	public final CcpJsonRepresentation json;
	
	public final CcpProcessStatus status;
	
	public final String[] fields;

	public CcpErrorFlowDisturb(CcpProcessStatus status, String... fields) {
		this(CcpOtherConstants.EMPTY_JSON, status, fields);
	}

	public CcpErrorFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, String... fields) {
		super(json.getOrDefault(CcpErrorFlowDisturbConstants.reason, json.put(CcpErrorFlowDisturbConstants.statusNumber, status.asNumber()).put(CcpErrorFlowDisturbConstants.statusName, status.name()).asPrettyJson()));
		this.json = json;
		this.status = status;
		this.fields = fields;
	}

	public CcpErrorFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, String message, String... fields) {
		super(message);
		this.json = json;
		this.status = status;
		this.fields = fields;
	}
	
	
}
