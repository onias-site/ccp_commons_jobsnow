package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

/**
 * Quarto elo da cadeia. Associa um status de processo a uma lista de {@code CcpBusiness}
 * que devem ser executados como tratamento alternativo quando aquele status ocorrer.
 */
public final class CcpIfThisExecutionReturns {
	protected final CcpBusiness givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;
	protected final CcpProcessStatus processStatus;
	protected final CcpJsonRepresentation flow;

	protected CcpIfThisExecutionReturns(CcpBusiness givenFinalTargetProcess,
			CcpJsonRepresentation givenJson, CcpProcessStatus processStatus, CcpJsonRepresentation flow) {

		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.processStatus = processStatus;
		this.givenJson = givenJson;
		this.flow = flow;
	}
	
	/**
	 * Registra os processos de tratamento para o status declarado e avança para o passo de encadeamento adicional.
	 * @param givenProcess os processos a executar quando o status ocorrer
	 * @return o próximo passo da cadeia fluente
	 */
	public CcpExecuteTheGivenProcess thenExecuteTheGivenProcesses(CcpBusiness... givenProcess) {
		CcpJsonRepresentation nextFlow = this.flow.put(this.processStatus, givenProcess);
		return new CcpExecuteTheGivenProcess(this.givenFinalTargetProcess, this.givenJson, nextFlow);
	}
}
