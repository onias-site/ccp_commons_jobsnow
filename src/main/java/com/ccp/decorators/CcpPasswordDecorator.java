package com.ccp.decorators;

/**
 * Decorator over a password text that checks its strength against the complexity criteria of the system: at least one
 * digit, one lowercase letter, one uppercase letter, one special character and between 8 and 20 characters.
 */
public class CcpPasswordDecorator implements CcpDecorator<String> {

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
	 * Tells whether the password meets the complexity criteria: 8 to 20 characters with at least one digit, one lowercase
	 * letter, one uppercase letter and one special character among {@code !@#&()–[{}]:;',?/*~$^+=<>} (note that the
	 * dash in that list is the en dash "–", not the hyphen "-").
	 * @return {@code true} when the password is strong
	 */
	public boolean isStrong() {
		boolean matches = this.content.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$");
		if (matches){
		   return true;
		} 
		return false;
	}

	/**
	 * Returns the raw password.
	 * @return the raw password
	 */
	public String getContent() {
		return this.content;
	}

}
