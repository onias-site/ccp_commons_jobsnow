package com.ccp.flow;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public final class BeginThisStatement {
	protected BeginThisStatement() {
		
	}
	
	public TryToExecuteTheGivenFinalTargetProcess tryToExecuteTheGivenFinalTargetProcess(Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess) {
		return new TryToExecuteTheGivenFinalTargetProcess(givenFinalTargetProcess);
	}
}
