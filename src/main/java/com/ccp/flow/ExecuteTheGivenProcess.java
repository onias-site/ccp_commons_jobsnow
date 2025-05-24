package com.ccp.flow;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public final class ExecuteTheGivenProcess {

	protected final Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;
	protected final CcpJsonRepresentation flow;
	
	public ExecuteTheGivenProcess(Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {
		
		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
		this.flow = flow;
	}

	public AndIfThisExecutionReturns and() {
		return new AndIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, this.flow);
	}

	
	

}
