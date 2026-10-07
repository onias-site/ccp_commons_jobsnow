package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

/**
 * Sixth and last link of the fluent flow chain. Holds the actual execution logic: tries to run the main process and,
 * if a {@code CcpErrorFlowDisturb} is thrown, runs the handlers registered for its status and tries again.
 */
public final class CcpAndIfThisExecutionReturns {
	/** The main process of the statement. */
	protected final CcpBusiness givenFinalTargetProcess;
	/** The JSON given to the main process in the current attempt. */
	protected final CcpJsonRepresentation givenJson;
	/** Flow map of the statuses still handled: status name to the array of handlers of that status. */
	protected final CcpJsonRepresentation flow;

	/**
	 * Keeps the state of the statement.
	 * @param givenFinalTargetProcess the main process of the statement
	 * @param givenJson the JSON given to the main process in this attempt
	 * @param flow flow map of the statuses still handled
	 */
	protected CcpAndIfThisExecutionReturns(CcpBusiness givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
		this.flow = flow;
	}

	/**
	 * Adds one more conditional branch to the flow (goes back to the fourth link of the chain).
	 * @param processStatus the status to be handled
	 * @return the fourth link of the chain, keeping the branches declared so far
	 */
	public CcpIfThisExecutionReturns ifThisExecutionReturns(CcpProcessStatus processStatus) {
		CcpIfThisExecutionReturns ccpIfThisExecutionReturns = new CcpIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, this.flow);
		return ccpIfThisExecutionReturns;
	}

	/**
	 * Runs the statement.
	 * <ol>
	 * <li>Runs the main process with the current JSON and then each {@code whatToNext} process with that same input JSON
	 * (their results are discarded); returns the result of the main process.</li>
	 * <li>If a {@code CcpErrorFlowDisturb} is thrown (by the main process or by a {@code whatToNext}), runs the handlers
	 * registered for its status in order, each one receiving the output of the previous one. A handler that itself throws
	 * {@code CcpErrorFlowDisturb} does not stop the chain: the JSON of that exception is merged with the current JSON
	 * (the current values win) and the next handler runs.</li>
	 * <li>Removes that status from the flow map and starts over from step 1 with the JSON produced by the handlers, so the
	 * main process is attempted again. Each status is therefore handled at most once.</li>
	 * </ol>
	 * The retry of step 3 is the design of the flow: the handlers of a status fix the input so the main process can succeed.
	 * A thrown status with no registered handler (or already handled) goes up as the original {@code CcpErrorFlowDisturb}.
	 * @param whatToNext processes run after the main process succeeds
	 * @return the result of the main process
	 */
	public CcpJsonRepresentation endThisStatement(CcpBusiness... whatToNext) {
		try {
			CcpJsonRepresentation responseWhenTheFlowPerformsNormally = this.tryToPerformNormally(whatToNext);
			return responseWhenTheFlowPerformsNormally;
		} catch (CcpErrorFlowDisturb e) {
			// a status with no handler (never configured, or already handled once in this statement) goes up as it came;
			// until 2026-10-07 it became a CcpErrorJsonFieldNotFound about the flow, losing the status and the message
			boolean hasNoHandler = false == this.flow.containsField(e.status);
			if(hasNoHandler) {
				throw e;
			}
			CcpJsonRepresentation json = this.tryToFixTheFlow(e);
			CcpJsonRepresentation remainingFlow = this.flow.removeFields(e.status);
			CcpAndIfThisExecutionReturns andIfThisExecutionReturns = new CcpAndIfThisExecutionReturns(this.givenFinalTargetProcess, json, remainingFlow);
			CcpJsonRepresentation endThisStatement = andIfThisExecutionReturns.endThisStatement(whatToNext);
			return endThisStatement;
		}
	}

	/**
	 * Runs the main process and then the {@code whatToNext} processes, all with the current input JSON.
	 * @param whatToNext processes run after the main process
	 * @return the result of the main process
	 */
	private CcpJsonRepresentation tryToPerformNormally(CcpBusiness... whatToNext) {
		CcpJsonRepresentation responseWhenTheFlowPerformsNormally = this.givenFinalTargetProcess.execute(this.givenJson);
		for (CcpBusiness function : whatToNext) {
			function.execute(this.givenJson);
		}
		return responseWhenTheFlowPerformsNormally;
	}

	/**
	 * Runs, in order, the handlers registered for the status of the exception, chaining their outputs.
	 * @param e the exception whose status selects the handlers
	 * @return the JSON produced by the last handler
	 * (the caller only calls it for a status that has handlers)
	 */
	private CcpJsonRepresentation tryToFixTheFlow(CcpErrorFlowDisturb e) {
		CcpBusiness[] nextFlows = this.flow.getAsObject(e.status);
		CcpJsonRepresentation json = this.givenJson;
		for (CcpBusiness nextFlow : nextFlows) {
			try {
				json = nextFlow.execute(json);
			} catch (CcpErrorFlowDisturb flowDisturb) {
				json = flowDisturb.json.mergeWithAnotherJson(json);
			}
		}
		return json;
	}
}
