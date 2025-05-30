package com.ccp.exceptions.instant.messenger;

@SuppressWarnings("serial")
public class CcpErrorInstantMessageThisBotWasBlockedByThisUser extends RuntimeException {
	public final String token;

	public CcpErrorInstantMessageThisBotWasBlockedByThisUser(String token) {
		this.token = token;
	}
	
}
