package com.ccp.flow;

import com.ccp.business.CcpBusiness;

/** First link of the fluent flow chain: receives the main process to be attempted. */
public final class CcpBeginThisStatement {
	/** The single instance: the class keeps no state. */
	public final static CcpBeginThisStatement INSTANCE = new CcpBeginThisStatement();

	/** Created only by {@link CcpTreeFlow#beginThisStatement()}. */
	private CcpBeginThisStatement() {
	}

	/**
	 * Registers the main target process and moves on to the next step of the chain (the input JSON).
	 * @param givenFinalTargetProcess the main process to run
	 * @return the next step of the fluent chain
	 */
	public CcpTryToExecuteTheGivenFinalTargetProcess tryToExecuteTheGivenFinalTargetProcess(CcpBusiness givenFinalTargetProcess) {
		CcpTryToExecuteTheGivenFinalTargetProcess ccpTryToExecuteTheGivenFinalTargetProcess = new CcpTryToExecuteTheGivenFinalTargetProcess(givenFinalTargetProcess);
		return ccpTryToExecuteTheGivenFinalTargetProcess;
	}
}
