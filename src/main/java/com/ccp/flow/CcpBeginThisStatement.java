package com.ccp.flow;

import com.ccp.business.CcpBusiness;

public final class CcpBeginThisStatement {
	protected CcpBeginThisStatement() {
		
	}
	
	public CcpTryToExecuteTheGivenFinalTargetProcess tryToExecuteTheGivenFinalTargetProcess(CcpBusiness givenFinalTargetProcess) {
		return new CcpTryToExecuteTheGivenFinalTargetProcess(givenFinalTargetProcess);
	}
}
