package com.ccp.flow;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public final class CcpBeginThisStatement {
	protected CcpBeginThisStatement() {
		
	}
	
	public CcpTryToExecuteTheGivenFinalTargetProcess tryToExecuteTheGivenFinalTargetProcess(Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess) {
		return new CcpTryToExecuteTheGivenFinalTargetProcess(givenFinalTargetProcess);
	}
}
