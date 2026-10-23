package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

/**
 * Business that interrupts the flow: throws {@code CcpErrorFlowDisturb} with its input and the given status.
 */
public class CcpBusinessThrowFlowDisturb implements CcpBusiness {

	/** The status the flow is interrupted with. */
	private final CcpProcessStatus status;

	/**
	 * Keeps the status of the interruption.
	 * @param status the status the flow is interrupted with
	 */
	public CcpBusinessThrowFlowDisturb(CcpProcessStatus status) {
		this.status = status;
	}

	/**
	 * Interrupts the flow.
	 * @param json the input, carried by the error
	 * @return never returns
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpErrorFlowDisturb ccpErrorFlowDisturb = new CcpErrorFlowDisturb(json, this.status);
		throw ccpErrorFlowDisturb;
	}
}
