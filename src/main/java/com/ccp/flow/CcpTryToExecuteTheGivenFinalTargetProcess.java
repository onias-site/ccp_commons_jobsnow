package com.ccp.flow;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public final class CcpTryToExecuteTheGivenFinalTargetProcess {
	protected final Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess;
	protected CcpTryToExecuteTheGivenFinalTargetProcess(
			Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
	}
	
	
	public CcpUsingTheGivenJson usingTheGivenJson (CcpJsonRepresentation givenJson) {
		return new CcpUsingTheGivenJson(this.givenFinalTargetProcess, givenJson);
	}
}
