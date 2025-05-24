package com.ccp.exceptions.instant.messenger;

@SuppressWarnings("serial")
public class CcpInstantMessageThisBotWasBlockedByThisUser extends RuntimeException {
	public final String token;

	public CcpInstantMessageThisBotWasBlockedByThisUser(String token) {
		this.token = token;
	}
	
}
