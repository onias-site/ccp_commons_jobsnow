package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Fifth link of the fluent flow chain: leads to more conditional branches or to the end of the statement, through
 * {@link #and()}.
 */
public final class CcpExecuteTheGivenProcess {
	/** The main process of the statement. */
	protected final CcpBusiness givenFinalTargetProcess;
	/** The input JSON of the main process. */
	protected final CcpJsonRepresentation givenJson;
	/** Flow map built so far: status name to the array of handlers of that status. */
	protected final CcpJsonRepresentation flow;

	/**
	 * Keeps the state of the statement built so far.
	 * @param givenFinalTargetProcess the main process of the statement
	 * @param givenJson the input JSON of the main process
	 * @param flow flow map built so far
	 */
	public CcpExecuteTheGivenProcess(CcpBusiness givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
		this.flow = flow;
	}

	/**
	 * Moves on to the last link, where more branches can be declared or the statement can be ended.
	 * @return the last link of the fluent chain
	 */
	public CcpAndIfThisExecutionReturns and() {
		CcpAndIfThisExecutionReturns ccpAndIfThisExecutionReturns = new CcpAndIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, this.flow);
		return ccpAndIfThisExecutionReturns;
	}
}
