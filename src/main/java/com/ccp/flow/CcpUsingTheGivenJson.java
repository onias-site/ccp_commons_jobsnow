package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

public final class CcpUsingTheGivenJson {
	protected final CcpBusiness givenFinalTargetProcess;
	
	protected final CcpJsonRepresentation givenJson;
	
	protected CcpUsingTheGivenJson(CcpBusiness givenFinalTargetProcess,
			CcpJsonRepresentation givenJson) {

		this.givenFinalTargetProcess = givenFinalTargetProcess;
		
		this.givenJson = givenJson;
	}
	
	public CcpIfThisExecutionReturns butIfThisExecutionReturns(CcpProcessStatus processStatus) {
		return new CcpIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, CcpOtherConstants.EMPTY_JSON);
	}
}
