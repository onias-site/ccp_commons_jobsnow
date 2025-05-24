package com.ccp.flow;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public final class TryToExecuteTheGivenFinalTargetProcess {
	protected final Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess;
	protected TryToExecuteTheGivenFinalTargetProcess(
			Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
	}
	
	
	public UsingTheGivenJson usingTheGivenJson (CcpJsonRepresentation givenJson) {
		return new UsingTheGivenJson(this.givenFinalTargetProcess, givenJson);
	}
}
