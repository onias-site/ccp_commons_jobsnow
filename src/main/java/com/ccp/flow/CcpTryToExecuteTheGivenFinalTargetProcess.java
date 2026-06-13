package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Segundo elo da cadeia fluente. Recebe o JSON de entrada que será passado ao processo principal.
 */
public final class CcpTryToExecuteTheGivenFinalTargetProcess {
	protected final CcpBusiness givenFinalTargetProcess;
	protected CcpTryToExecuteTheGivenFinalTargetProcess(
			CcpBusiness givenFinalTargetProcess) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
	}
	
	
	/**
	 * Registra o JSON de entrada e avança para o passo onde se declaram os tratamentos condicionais por status.
	 * @param givenJson o JSON de entrada para o processo principal
	 * @return o próximo passo da cadeia fluente
	 */
	public CcpUsingTheGivenJson usingTheGivenJson (CcpJsonRepresentation givenJson) {
		return new CcpUsingTheGivenJson(this.givenFinalTargetProcess, givenJson);
	}
}
