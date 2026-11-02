package com.ccp.especifications.email;

/** The lists of addresses that an e-mail provider keeps for the messages it could not, or must not, deliver. */
public enum CcpEmailSuppressionList {
	/** Addresses whose mail server permanently refused the messages. */
	bounces,
	/** Addresses whose mail server temporarily refused a message; the provider keeps sending to them. */
	blocks,
	/** Addresses that are malformed or do not exist at the mail server. */
	invalid_emails,
	/** Addresses whose owners marked a message as spam. */
	spam_reports
}
