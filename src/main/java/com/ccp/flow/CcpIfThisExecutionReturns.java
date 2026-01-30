package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

public final class CcpIfThisExecutionReturns {
	protected final CcpBusiness givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;
	protected final CcpProcessStatus processStatus;
	protected final CcpJsonRepresentation flow;

	protected CcpIfThisExecutionReturns(CcpBusiness givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpProcessStatus processStatus, CcpJsonRepresentation flow) {

		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.processStatus = processStatus;
		this.givenJson = givenJson;
		this.flow = flow;
	}
	
	public CcpExecuteTheGivenProcess thenExecuteTheGivenProcesses(CcpBusiness... givenProcess) {
		CcpJsonRepresentation nextFlow = this.flow.put(this.processStatus, givenProcess);
		return new CcpExecuteTheGivenProcess(this.givenFinalTargetProcess, this.givenJson, nextFlow);
	}
}
