package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

/**
 * Third link of the fluent flow chain: declares the first conditional handling, "but if this execution returns
 * status X, then...".
 */
public final class CcpUsingTheGivenJson {
	/** The main process of the statement. */
	protected final CcpBusiness givenFinalTargetProcess;
	/** The input JSON of the main process. */
	protected final CcpJsonRepresentation givenJson;

	/**
	 * Created only by {@link CcpTryToExecuteTheGivenFinalTargetProcess}.
	 * @param givenFinalTargetProcess the main process of the statement
	 * @param givenJson the input JSON of the main process
	 */
	protected CcpUsingTheGivenJson(CcpBusiness givenFinalTargetProcess, CcpJsonRepresentation givenJson) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
	}

	/**
	 * Registers the first process status to be handled, starting from an empty flow map, and moves on to declare which
	 * processes run in that case.
	 * @param processStatus the status that triggers the alternative handling
	 * @return the next step of the fluent chain
	 */
	public CcpIfThisExecutionReturns butIfThisExecutionReturns(CcpProcessStatus processStatus) {
		CcpIfThisExecutionReturns ccpIfThisExecutionReturns = new CcpIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, CcpOtherConstants.EMPTY_JSON);
		return ccpIfThisExecutionReturns;
	}
}
