package com.ccp.decorators;

/**
 * Decorator over a password text that checks its strength against the complexity criteria of the system: at least one
 * digit, one lowercase letter, one uppercase letter, one special character and between 8 and 20 characters.
 */
public class CcpPasswordDecorator implements CcpDecorator<String> {

	/**
	 * The rule of a strong password, the single source for {@link #isStrong()} and for the validation of the password
	 * field of the API: at least 8 characters, with a lowercase letter, an uppercase letter, a digit and any character
	 * that is neither a letter nor a digit.
	 */
	public static final String STRONG_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";

	/** The raw password. */
	public final String content;

	/**
	 * Wraps the password.
	 * @param content the raw password
	 */
	protected CcpPasswordDecorator(String content) {
		this.content = content;
	}

	/**
	 * Returns the raw password.
	 * @return the raw password
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Tells whether the password meets {@link #STRONG_PASSWORD_REGEX}. Until 2026-10-07 this method had a rule of its own,
	 * different from the one the API applies: at most 20 characters, and a closed list of special characters written with
	 * an en dash instead of the hyphen and a nested {@code [}, so that {@code -}, {@code .}, {@code _}, {@code [} and
	 * {@code ]} did not count as special.
	 * @return {@code true} when the password is strong
	 */
	public boolean isStrong() {
		boolean matches = this.content.matches(STRONG_PASSWORD_REGEX);
		return matches;
	}

	/**
	 * Returns the raw password.
	 * @return the raw password
	 */
	public String getContent() {
		return this.content;
	}

}
