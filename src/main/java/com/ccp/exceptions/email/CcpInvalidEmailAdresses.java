package com.ccp.exceptions.email;

import java.util.List;

@SuppressWarnings("serial")
public class CcpInvalidEmailAdresses extends RuntimeException{
	
	public CcpInvalidEmailAdresses(List<?> invalidEmails) {
		super("These following mail addresses are not valid: " + invalidEmails);
	}
}
