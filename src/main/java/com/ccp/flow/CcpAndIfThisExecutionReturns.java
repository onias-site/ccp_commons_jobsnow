package com.ccp.flow;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.exceptions.json.CcpErrorJsonFieldNotFound;
import com.ccp.exceptions.process.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatus;

public final class CcpAndIfThisExecutionReturns {
	protected final Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;
	protected final CcpJsonRepresentation flow;

	protected CcpAndIfThisExecutionReturns(Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {

		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
		this.flow = flow;
	}
	
	
	public CcpIfThisExecutionReturns ifThisExecutionReturns(CcpProcessStatus processStatus) {
		return new CcpIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, this.flow);
	}
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation endThisStatement(Function<CcpJsonRepresentation, CcpJsonRepresentation>... whatToNext) {
		try {
			CcpJsonRepresentation responseWhenTheFlowPerformsNormally = this.tryToPerformNormally(whatToNext);
			return responseWhenTheFlowPerformsNormally;
		} catch (CcpErrorFlowDisturb e) {
			CcpJsonRepresentation json = this.tryToFixTheFlow(e);
			CcpJsonRepresentation remainingFlow = this.flow.removeField(e.status);
			CcpAndIfThisExecutionReturns andIfThisExecutionReturns = new CcpAndIfThisExecutionReturns(this.givenFinalTargetProcess, json, remainingFlow);
			CcpJsonRepresentation endThisStatement = andIfThisExecutionReturns.endThisStatement(whatToNext);
			return endThisStatement;
		}
	}


	@SuppressWarnings("unchecked")
	private CcpJsonRepresentation tryToPerformNormally(
			Function<CcpJsonRepresentation, CcpJsonRepresentation>... whatToNext) {
		CcpJsonRepresentation responseWhenTheFlowPerformsNormally = this.givenFinalTargetProcess.apply(this.givenJson);
		for (Function<CcpJsonRepresentation, CcpJsonRepresentation> function : whatToNext) {
			function.apply(this.givenJson);
		}
		return responseWhenTheFlowPerformsNormally;
	}
 
	private CcpJsonRepresentation tryToFixTheFlow(CcpErrorFlowDisturb e) {
		try {
			Function<CcpJsonRepresentation, CcpJsonRepresentation>[] nextFlows = this.flow.getAsObject(e.status);
			CcpJsonRepresentation json = this.givenJson;
			
			for (Function<CcpJsonRepresentation, CcpJsonRepresentation> nextFlow : nextFlows) {
				try {
					json = nextFlow.apply(json);
				} catch (CcpErrorFlowDisturb flowDisturb) {
					json = flowDisturb.json.putAll(json);
				}
			}
			return json;
		} catch (CcpErrorJsonFieldNotFound ex) {
			throw ex;  
		}
	}

}
