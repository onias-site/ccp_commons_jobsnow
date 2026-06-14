package com.ccp.process;

@SuppressWarnings("serial")
public class UnexpectedProcessStatus extends RuntimeException{

	public UnexpectedProcessStatus(String message, String testName, int expectedStatus, int actualStatus) {
		super(String.format("In the test '%s' it was expected the status '%s', but status '%s' was received. Message: " + message, testName, expectedStatus, actualStatus));
	}

	public UnexpectedProcessStatus(String expectedStatusName, String actualStatusName) {
		super(String.format("It was expected the status name '%s' but status name '%s' was received insted", expectedStatusName, actualStatusName));
	}

}
