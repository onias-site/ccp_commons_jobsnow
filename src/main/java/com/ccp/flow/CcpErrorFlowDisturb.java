package com.ccp.flow;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;
import com.ccp.process.CcpProcessStatusDefault;
@SuppressWarnings("serial")
public class CcpErrorFlowDisturb extends RuntimeException{
	enum JsonFieldNames implements CcpJsonFieldName{
		reason, statusNumber, statusName
	}
	
	public static class CcpUnprocessableEntity extends CcpErrorFlowDisturb{
		public CcpUnprocessableEntity(CcpJsonRepresentation json) {
			super(json, CcpProcessStatusDefault.UNPROCESSABLE_ENTITY);
		}
	}
	
	public final CcpJsonRepresentation json;
	
	public final CcpProcessStatus status;
	
	public final String[] fields;

	public CcpErrorFlowDisturb(CcpProcessStatus status, String... fields) {
		this(CcpOtherConstants.EMPTY_JSON, status, fields);
	}

	public CcpErrorFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, String... fields) {
		super(getErrorMessage(json, status));
		this.json = json;
		this.status = status;
		this.fields = fields;
	}

	private static String getErrorMessage(CcpJsonRepresentation json, CcpProcessStatus status) {
		return json.getOrDefault(JsonFieldNames.reason, () -> json.put(JsonFieldNames.statusNumber, status.asNumber()).put(JsonFieldNames.statusName, status.name()).asPrettyJson());
	}

	public CcpErrorFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, String message, String... fields) {
		super(message);
		this.json = json;
		this.status = status;
		this.fields = fields;
	}
	
	
}
