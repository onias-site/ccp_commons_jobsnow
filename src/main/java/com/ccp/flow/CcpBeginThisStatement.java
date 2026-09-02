package com.ccp.flow;

import com.ccp.business.CcpBusiness;

public final class CcpBeginThisStatement {
	protected CcpBeginThisStatement() {
	}

	/**
	 * Registra o processo alvo principal e avança para o próximo passo da cadeia (informar o JSON de entrada).
	 * @param givenFinalTargetProcess o processo principal a ser executado
	 * @return o próximo passo da cadeia fluente
	 */
	public CcpTryToExecuteTheGivenFinalTargetProcess tryToExecuteTheGivenFinalTargetProcess(CcpBusiness givenFinalTargetProcess) {
		CcpTryToExecuteTheGivenFinalTargetProcess ccpTryToExecuteTheGivenFinalTargetProcess = new CcpTryToExecuteTheGivenFinalTargetProcess(givenFinalTargetProcess);
		return ccpTryToExecuteTheGivenFinalTargetProcess;
	}
}
