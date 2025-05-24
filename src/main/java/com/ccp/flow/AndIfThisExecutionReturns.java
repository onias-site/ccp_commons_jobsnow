package com.ccp.flow;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.exceptions.json.CcpJsonFieldNotFound;
import com.ccp.exceptions.process.CcpFlowDisturb;
import com.ccp.process.CcpProcessStatus;

public final class AndIfThisExecutionReturns {
	protected final Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;
	protected final CcpJsonRepresentation flow;

	protected AndIfThisExecutionReturns(Function<CcpJsonRepresentation, CcpJsonRepresentation> givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {

		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
		this.flow = flow;
	}
	
	
	public IfThisExecutionReturns ifThisExecutionReturns(CcpProcessStatus processStatus) {
		return new IfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, this.flow);
	}
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation endThisStatement(Function<CcpJsonRepresentation, CcpJsonRepresentation>... whatToNext) {
		try {
			CcpJsonRepresentation responseWhenTheFlowPerformsNormally = this.tryToPerformNormally(whatToNext);
			return responseWhenTheFlowPerformsNormally;
		} catch (CcpFlowDisturb e) {
			CcpJsonRepresentation json = this.tryToFixTheFlow(e);
			CcpJsonRepresentation remainingFlow = this.flow.removeField(e.status.name());
			AndIfThisExecutionReturns andIfThisExecutionReturns = new AndIfThisExecutionReturns(this.givenFinalTargetProcess, json, remainingFlow);
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
 
	private CcpJsonRepresentation tryToFixTheFlow(CcpFlowDisturb e) {
		try {
			Function<CcpJsonRepresentation, CcpJsonRepresentation>[] nextFlows = this.flow.getAsObject(e.status.name());
//			System.out.println("Fluxos a serem executados por este desvio de fluxo: " + Arrays.asList(nextFlows));
			CcpJsonRepresentation json = this.givenJson;
			
			for (Function<CcpJsonRepresentation, CcpJsonRepresentation> nextFlow : nextFlows) {
				try {
					json = nextFlow.apply(json);
				} catch (CcpFlowDisturb flowDisturb) {
					json = flowDisturb.json.putAll(json);
				}
			}
			return json;
		} catch (CcpJsonFieldNotFound ex) {
			throw ex;
		}
	}

}
