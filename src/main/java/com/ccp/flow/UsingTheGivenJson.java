package com.ccp.flow;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

public final class UsingTheGivenJson {
	protected final Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess;
	
	protected final CcpJsonRepresentation givenJson;
	
	protected UsingTheGivenJson(Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess,
			CcpJsonRepresentation givenJson) {

		this.givenFinalTargetProcess = givenFinalTargetProcess;
		
		this.givenJson = givenJson;
	}
	
	public IfThisExecutionReturns butIfThisExecutionReturns(CcpProcessStatus processStatus) {
		return new IfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, CcpOtherConstants.EMPTY_JSON);
	}
}
