package com.ccp.flow;

import com.ccp.business.CcpBusiness;

/**
 * Primeiro elo da cadeia fluente de fluxo.
 * Recebe o processo principal que se deseja tentar executar.
 */
public final class CcpBeginThisStatement {
	protected CcpBeginThisStatement() {
		
	}
	
	/**
	 * Registra o processo alvo principal e avança para o próximo passo da cadeia (informar o JSON de entrada).
	 * @param givenFinalTargetProcess o processo principal a ser executado
	 * @return o próximo passo da cadeia fluente
	 */
	public CcpTryToExecuteTheGivenFinalTargetProcess tryToExecuteTheGivenFinalTargetProcess(CcpBusiness givenFinalTargetProcess) {
		return new CcpTryToExecuteTheGivenFinalTargetProcess(givenFinalTargetProcess);
	}
}
