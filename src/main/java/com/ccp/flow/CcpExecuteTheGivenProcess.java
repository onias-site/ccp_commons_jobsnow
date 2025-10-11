package com.ccp.flow;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.mensageria.receiver.CcpBusiness;

public final class CcpExecuteTheGivenProcess {

	protected final CcpBusiness givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;
	protected final CcpJsonRepresentation flow;
	
	public CcpExecuteTheGivenProcess(CcpBusiness givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {
		
		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
		this.flow = flow;
	}

	public CcpAndIfThisExecutionReturns and() {
		return new CcpAndIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, this.flow);
	}

	
	

}
