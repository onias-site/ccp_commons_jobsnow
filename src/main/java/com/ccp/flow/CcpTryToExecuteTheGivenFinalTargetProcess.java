package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

public final class CcpTryToExecuteTheGivenFinalTargetProcess {
	protected final CcpBusiness givenFinalTargetProcess;
	protected CcpTryToExecuteTheGivenFinalTargetProcess(
			CcpBusiness givenFinalTargetProcess) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
	}
	
	
	public CcpUsingTheGivenJson usingTheGivenJson (CcpJsonRepresentation givenJson) {
		return new CcpUsingTheGivenJson(this.givenFinalTargetProcess, givenJson);
	}
}
