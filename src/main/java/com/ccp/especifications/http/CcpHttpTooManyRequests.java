package com.ccp.especifications.http;

/** Raised when a remote API refuses a call because of its rate limit (HTTP 429). */
@SuppressWarnings("serial")
public class CcpHttpTooManyRequests extends RuntimeException {
	/** Builds the error. */
	public CcpHttpTooManyRequests() {
	}
}
