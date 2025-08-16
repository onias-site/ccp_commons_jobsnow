package com.ccp.process;

import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public interface CcpProcessStatus  extends CcpJsonFieldName{
	int asNumber();
	default String verifyStatus(int actualStatus) {
		int expectedStatus = this.asNumber();
		
		boolean correctStatus = expectedStatus == actualStatus;
		
		String testName = this.name();
		
		if(correctStatus) {
			return testName;
		}
		
		String msg = String.format("In the test '%s' it was expected the status '%s', but status '%s' was received", testName, expectedStatus, actualStatus);
		throw new RuntimeException(msg);
	}
	
	default CcpProcessStatus verifyStatus(int actualStatus, String actualStatusName) {
		String expectedStatusName = this.verifyStatus(actualStatus);
		
		if(actualStatusName.trim().isEmpty()) {
			return this;
		}
		
		boolean correctStatusNumberAndCorrectStatusName = actualStatusName.equals(expectedStatusName);
		
		if(correctStatusNumberAndCorrectStatusName) {
			return this;
		}
		String msg = String.format("It was expected the status name '%s' but status name '%s' was received insted", expectedStatusName, actualStatusName);
		throw new RuntimeException(msg);
	}
}
