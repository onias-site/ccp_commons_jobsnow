package com.ccp.flow;

import com.ccp.decorators.CcpErrorJsonFieldNotFound;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.mensageria.receiver.CcpBusiness;
import com.ccp.process.CcpProcessStatus;

public final class CcpAndIfThisExecutionReturns {
	protected final CcpBusiness givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;
	protected final CcpJsonRepresentation flow;

	protected CcpAndIfThisExecutionReturns(CcpBusiness givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {

		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
		this.flow = flow;
	}
	
	
	public CcpIfThisExecutionReturns ifThisExecutionReturns(CcpProcessStatus processStatus) {
		return new CcpIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, this.flow);
	}
	public CcpJsonRepresentation endThisStatement(CcpBusiness... whatToNext) {
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


	private CcpJsonRepresentation tryToPerformNormally(
			CcpBusiness... whatToNext) {
		CcpJsonRepresentation responseWhenTheFlowPerformsNormally = this.givenFinalTargetProcess.apply(this.givenJson);
		for (CcpBusiness function : whatToNext) {
			function.apply(this.givenJson);
		}
		return responseWhenTheFlowPerformsNormally;
	}
 
	private CcpJsonRepresentation tryToFixTheFlow(CcpErrorFlowDisturb e) {
		try {
			CcpBusiness[] nextFlows = this.flow.getAsObject(e.status);
			CcpJsonRepresentation json = this.givenJson;
			
			for (CcpBusiness nextFlow : nextFlows) {
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
