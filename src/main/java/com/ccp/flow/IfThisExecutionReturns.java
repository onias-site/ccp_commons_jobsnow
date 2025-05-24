package com.ccp.flow;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

public final class IfThisExecutionReturns {
	protected final Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;
	protected final CcpProcessStatus processStatus;
	protected final CcpJsonRepresentation flow;

	protected IfThisExecutionReturns(Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpProcessStatus processStatus, CcpJsonRepresentation flow) {

		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.processStatus = processStatus;
		this.givenJson = givenJson;
		this.flow = flow;
	}
	
	@SuppressWarnings("unchecked")
	public ExecuteTheGivenProcess thenExecuteTheGivenProcesses(Function<CcpJsonRepresentation, CcpJsonRepresentation>... givenProcess) {
		CcpJsonRepresentation nextFlow = this.flow.put(processStatus.name(), givenProcess);
		return new ExecuteTheGivenProcess(this.givenFinalTargetProcess, this.givenJson, nextFlow);
	}
}
