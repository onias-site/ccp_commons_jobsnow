package com.ccp.especifications.main.authentication;

/** Contract for obtaining authentication JWT tokens (GCP OAuth). */
public interface CcpAuthenticationProvider {

	/**
	 * Obtains the current JWT token.
	 * @return the JWT token
	 */
	String getJwtToken();
}
