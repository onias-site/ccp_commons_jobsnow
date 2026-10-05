package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/** Second link of the fluent flow chain: receives the input JSON passed to the main process. */
public final class CcpTryToExecuteTheGivenFinalTargetProcess {
	/** The main process of the statement. */
	protected final CcpBusiness givenFinalTargetProcess;

	/**
	 * Created only by {@link CcpBeginThisStatement}.
	 * @param givenFinalTargetProcess the main process of the statement
	 */
	protected CcpTryToExecuteTheGivenFinalTargetProcess(CcpBusiness givenFinalTargetProcess) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
	}

	/**
	 * Registers the input JSON and moves on to the step where the conditional handlings per status are declared.
	 * @param givenJson the input JSON of the main process
	 * @return the next step of the fluent chain
	 */
	public CcpUsingTheGivenJson usingTheGivenJson(CcpJsonRepresentation givenJson) {
		CcpUsingTheGivenJson ccpUsingTheGivenJson = new CcpUsingTheGivenJson(this.givenFinalTargetProcess, givenJson);
		return ccpUsingTheGivenJson;
	}
}
