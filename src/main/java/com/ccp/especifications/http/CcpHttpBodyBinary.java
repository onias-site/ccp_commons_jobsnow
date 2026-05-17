package com.ccp.especifications.http;

public class CcpHttpBodyBinary {
	
	public final CcpHttpContentType contentType;
	public final String fileName;
	public final Byte[] bytes;
	public final String name;
	
	public CcpHttpBodyBinary(CcpHttpContentType contentType, String name, String fileName, Byte[] bytes) {
		
		this.contentType = contentType;
		this.fileName = fileName;
		this.bytes = bytes;
		this.name = name;
	}
	
	public byte[] getBytes() {
		
		int k = 0;
		byte[] result = new byte[this.bytes.length];
		
		for (Byte _byte : this.bytes) {
			result[k++] = _byte; 
		}
		return result;
	}
}
