package com.ccp.flow;


/**
 * Entry point of the fluent conditional-flow API of the framework. It builds pipelines such as "try to run X; if it
 * returns status Y, run Z; then end the statement", modeling business exceptions ({@code CcpErrorFlowDisturb})
 * declaratively:
 * <pre>
 * CcpTreeFlow.beginThisStatement()
 * .tryToExecuteTheGivenFinalTargetProcess(mainProcess)
 * .usingTheGivenJson(json)
 * .butIfThisExecutionReturns(STATUS_A).thenExecuteTheGivenProcesses(handlerA1, handlerA2)
 * .and().ifThisExecutionReturns(STATUS_B).thenExecuteTheGivenProcesses(handlerB)
 * .and().endThisStatement(afterSuccess);
 * </pre>
 */
public final class CcpTreeFlow {

	/**
	 * Starts building a flow statement. It is the only entry point; the other steps follow the fluent chain.
	 * @return the first link of the chain
	 */
	public static CcpBeginThisStatement beginThisStatement() {
		CcpBeginThisStatement ccpBeginThisStatement = new CcpBeginThisStatement();
		return ccpBeginThisStatement;
	}

	/*
	 * First link of the fluent chain (CcpBeginThisStatement):
	 * receives the main process to be attempted.
	 */


	/*
	 * Second link of the chain (CcpTryToExecuteTheGivenFinalTargetProcess): receives the input JSON passed to the main process.
	 */


	/*
	 * Third link of the chain (CcpUsingTheGivenJson): declares the first conditional handling,
	 * "but if this execution returns status X, then...".
	 */


	/*
	 * Fourth link of the chain (CcpIfThisExecutionReturns): associates a process status with the list of
	 * {@code CcpBusiness} executed as alternative handling when that status occurs.
	 */


	/*
	 * Fifth link of the chain (CcpExecuteTheGivenProcess): adds more conditional branches (through {@code and()}) or ends the statement.
	 */


	/*
	 * Sixth and last link of the chain (CcpAndIfThisExecutionReturns). Holds the actual execution logic: tries to run
	 * the main process and, if a {@code CcpErrorFlowDisturb} is thrown, looks up in the flow map the handling
	 * processes of that status and runs them, then tries the main process again.
	 */

}
