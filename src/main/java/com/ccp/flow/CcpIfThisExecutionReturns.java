package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

/**
 * Fourth link of the fluent flow chain: associates a process status with the list of {@code CcpBusiness} executed as
 * alternative handling when that status occurs.
 */
public final class CcpIfThisExecutionReturns {
	/** The main process of the statement. */
	protected final CcpBusiness givenFinalTargetProcess;
	/** The input JSON of the main process. */
	protected final CcpJsonRepresentation givenJson;
	/** The status whose handlers are being declared. */
	protected final CcpProcessStatus processStatus;
	/** Flow map built so far: status name to the array of handlers of that status. */
	protected final CcpJsonRepresentation flow;

	/**
	 * Created by {@link CcpUsingTheGivenJson} and {@link CcpAndIfThisExecutionReturns}.
	 * @param givenFinalTargetProcess the main process of the statement
	 * @param givenJson the input JSON of the main process
	 * @param processStatus the status whose handlers are being declared
	 * @param flow flow map built so far
	 */
	protected CcpIfThisExecutionReturns(CcpBusiness givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpProcessStatus processStatus, CcpJsonRepresentation flow) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.processStatus = processStatus;
		this.givenJson = givenJson;
		this.flow = flow;
	}

	/**
	 * Registers the handling processes of the declared status (replacing any handlers previously declared for the same
	 * status) and moves on to the step of additional chaining.
	 * @param givenProcess the processes run, in order, when the status occurs
	 * @return the next step of the fluent chain
	 */
	public CcpExecuteTheGivenProcess thenExecuteTheGivenProcesses(CcpBusiness... givenProcess) {
		CcpJsonRepresentation nextFlow = this.flow.put(this.processStatus, givenProcess);
		CcpExecuteTheGivenProcess ccpExecuteTheGivenProcess = new CcpExecuteTheGivenProcess(this.givenFinalTargetProcess, this.givenJson, nextFlow);
		return ccpExecuteTheGivenProcess;
	}
}
