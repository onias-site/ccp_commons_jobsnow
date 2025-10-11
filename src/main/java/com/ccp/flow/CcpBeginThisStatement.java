package com.ccp.flow;

import com.ccp.especifications.mensageria.receiver.CcpBusiness;

public final class CcpBeginThisStatement {
	protected CcpBeginThisStatement() {
		
	}
	
	public CcpTryToExecuteTheGivenFinalTargetProcess tryToExecuteTheGivenFinalTargetProcess(CcpBusiness givenFinalTargetProcess) {
		return new CcpTryToExecuteTheGivenFinalTargetProcess(givenFinalTargetProcess);
	}
}
