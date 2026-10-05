package com.ccp.especifications.password;

/** Contract for hashing and checking passwords (BCrypt through Mindrot). */
public interface CcpPasswordHandler {

	/**
	 * Tells whether the plain password matches the hash.
	 * @param password the plain password
	 * @param hash the stored BCrypt hash
	 * @return {@code true} when the password matches
	 */
	boolean matches(String password, String hash);

	/**
	 * Generates the BCrypt hash of the password (a new salt each time).
	 * @param password the plain password
	 * @return the BCrypt hash
	 */
	String getHash(String password);

}
