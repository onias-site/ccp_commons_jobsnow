package com.ccp.flow;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public final class CcpExecuteTheGivenProcess {

	protected final Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;
	protected final CcpJsonRepresentation flow;
	
	public CcpExecuteTheGivenProcess(Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {
		
		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
		this.flow = flow;
	}

	public CcpAndIfThisExecutionReturns and() {
		return new CcpAndIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, this.flow);
	}

	
	

}
