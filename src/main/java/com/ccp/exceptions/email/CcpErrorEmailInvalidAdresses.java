package com.ccp.exceptions.email;

import java.util.List;

@SuppressWarnings("serial")
public class CcpErrorEmailInvalidAdresses extends RuntimeException{
	
	public CcpErrorEmailInvalidAdresses(List<?> invalidEmails) {
		super("These following mail addresses are not valid: " + invalidEmails);
	}
}
