package com.ccp.especifications.instant.messenger;

/** Raised by the instant messenger when the recipient user blocked the bot. */
@SuppressWarnings("serial")
public class CcpErrorInstantMessageThisBotWasBlockedByThisUser extends RuntimeException {
	/** The token of the blocked bot (despite the name). */
	public final String botName;
	/**
	 * Builds the error for the bot.
	 * @param token the token of the blocked bot
	 */
	public CcpErrorInstantMessageThisBotWasBlockedByThisUser(String token) {
		this.botName = token;
	}
}
