package com.ccp.especifications.http;

public class CcpHttpBodyText {
	public final CcpHttpContentType contentType;
	public final String name;
	public final String text;
	public CcpHttpBodyText(CcpHttpContentType contentType, String name, String text) {
		this.contentType = contentType;
		this.name = name;
		this.text = text;
	}
	
	
}
