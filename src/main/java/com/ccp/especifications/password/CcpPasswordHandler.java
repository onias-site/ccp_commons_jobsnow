package com.ccp.especifications.password;

public interface CcpPasswordHandler {

	boolean matches(String password, String hash);

	String getHash(String password);

}
