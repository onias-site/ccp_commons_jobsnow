package com.ccp.especifications.instant.messenger;

@SuppressWarnings("serial")
public class CcpErrorInstantMessageThisBotWasBlockedByThisUser extends RuntimeException {
	public final String botName;

	public CcpErrorInstantMessageThisBotWasBlockedByThisUser(String token) {
		this.botName = token;
	}
	
}
