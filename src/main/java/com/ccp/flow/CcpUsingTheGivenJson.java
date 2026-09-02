package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

public final class CcpUsingTheGivenJson {
	protected final CcpBusiness givenFinalTargetProcess;
	protected final CcpJsonRepresentation givenJson;

	protected CcpUsingTheGivenJson(CcpBusiness givenFinalTargetProcess, CcpJsonRepresentation givenJson) {
		this.givenFinalTargetProcess = givenFinalTargetProcess;
		this.givenJson = givenJson;
	}

	/**
	 * Registra um status de processo a ser tratado e avança para declarar quais processos executar naquele caso.
	 * @param processStatus o status que dispara o tratamento alternativo
	 * @return o próximo passo da cadeia fluente
	 */
	public CcpIfThisExecutionReturns butIfThisExecutionReturns(CcpProcessStatus processStatus) {
		CcpIfThisExecutionReturns ccpIfThisExecutionReturns = new CcpIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, CcpOtherConstants.EMPTY_JSON);
		return ccpIfThisExecutionReturns;
	}
}
