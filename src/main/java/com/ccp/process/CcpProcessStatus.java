package com.ccp.process;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.flow.CcpErrorFlowDisturb;

/**
 * Contract of a process status (analogous to HTTP response codes). It extends {@code CcpJsonFieldName}, so a status
 * can be used directly as a JSON key, and offers status verification for tests and the flow-disturbing exception.
 */
public interface CcpProcessStatus extends CcpJsonFieldName{
	/**
	 * Returns the numeric code of the status (e.g. 200, 404, 409).
	 * @return the numeric code
	 */
	int asNumber();

	/**
	 * Compares the received status code with the expected one.
	 * @param actualStatus the received status code
	 * @param message additional diagnostic message appended to the error
	 * @return the name of this status when the codes match
	 * @throws UnexpectedProcessStatus when the codes differ
	 */
	default String verifyStatus(int actualStatus, String message) {
		int expectedStatus = this.asNumber();
		
		boolean correctStatus = expectedStatus == actualStatus;
		
		String testName = this.name();
		
		if(correctStatus) {
			return testName;
		}
		UnexpectedProcessStatus unexpectedProcessStatus = new UnexpectedProcessStatus(message, testName, expectedStatus, actualStatus);
		throw unexpectedProcessStatus;
	}
	
	/**
	 * Checks the numeric code and, when the received name is not blank, the status name as well.
	 * @param actualStatus the received status code
	 * @param actualStatusName the received status name; blank means "do not check the name"
	 * @return this status when everything matches
	 * @throws UnexpectedProcessStatus when the code or the name differs
	 */
	default CcpProcessStatus verifyStatusNames(int actualStatus, String actualStatusName) {
		String expectedStatusName = this.verifyStatus(actualStatus, "");
		String actualStatusNameTrim = actualStatusName.trim();

		boolean empty = actualStatusNameTrim.isEmpty();
		if(empty) {
			return this;
		}
		
		boolean correctStatusNumberAndCorrectStatusName = actualStatusName.equals(expectedStatusName);
		
		if(correctStatusNumberAndCorrectStatusName) {
			return this;
		}
		UnexpectedProcessStatus unexpectedProcessStatus2 = new UnexpectedProcessStatus(expectedStatusName, actualStatusName);
		throw unexpectedProcessStatus2;
	}
	
	
	/**
	 * Throws a {@code CcpErrorFlowDisturb} carrying the JSON and this status, to interrupt the flow in a controlled way
	 * so that the flow engine can route it to the handler of this status.
	 * @param json context JSON of the exception
	 * @return never returns
	 */
	default CcpJsonRepresentation throwException(CcpJsonRepresentation json) {
		CcpErrorFlowDisturb ccpErrorFlowDisturb = new CcpErrorFlowDisturb(json, this);
		throw ccpErrorFlowDisturb;
	}

	/** Raised by the verification methods when the received status (code or name) differs from the expected one. */
	@SuppressWarnings("serial")
	public static class UnexpectedProcessStatus extends RuntimeException {
		/**
		 * Builds the error of a code mismatch.
		 * @param message additional diagnostic message
		 * @param testName name of the expected status
		 * @param expectedStatus expected code
		 * @param actualStatus received code
		 */
		private UnexpectedProcessStatus(String message, String testName, int expectedStatus, int actualStatus) {
			super(String.format("In the test '%s' it was expected the status '%s', but status '%s' was received. Message: " + message, testName, expectedStatus, actualStatus));
		}
		/**
		 * Builds the error of a name mismatch.
		 * @param expectedStatusName expected name
		 * @param actualStatusName received name
		 */
		private UnexpectedProcessStatus(String expectedStatusName, String actualStatusName) {
			super(String.format("It was expected the status name '%s' but status name '%s' was received insted", expectedStatusName, actualStatusName));
		}
	}
}
