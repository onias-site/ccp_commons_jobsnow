package com.ccp.constantes;

public enum CcpStringConstants {

	EMAIL("email"),
	LANGUAGE("language"),
	EMAIL_REGEX("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"),
	CHARACTERS_TO_GENERATE_TOKEN("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"),
	STRONG_PASSWORD_REGEX("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$"),
	
	;
	
	private CcpStringConstants(String value) {
		this.value = value;
	}

	public final String value;
}
