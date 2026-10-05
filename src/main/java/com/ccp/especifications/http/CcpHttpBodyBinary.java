package com.ccp.especifications.http;

/** A binary part of a multipart HTTP request: content type, form field name, file name and bytes. */
public class CcpHttpBodyBinary {

	/** The content type of the part. */
	public final CcpHttpContentType contentType;
	/** The file name. */
	public final String fileName;
	/** The content of the file. */
	public final Byte[] bytes;
	/** The form field name. */
	public final String name;

	/**
	 * Builds the binary part.
	 * @param contentType the content type of the file
	 * @param name the form field name
	 * @param fileName the file name
	 * @param bytes the content of the file
	 */
	public CcpHttpBodyBinary(CcpHttpContentType contentType, String name, String fileName, Byte[] bytes) {
		
		this.contentType = contentType;
		this.fileName = fileName;
		this.bytes = bytes;
		this.name = name;
	}
	
	/**
	 * Unboxes the content of the file.
	 * @return the primitive bytes
	 */
	public byte[] getBytes() {
		
		int k = 0;
		byte[] result = new byte[this.bytes.length];
		
		for (Byte _byte : this.bytes) {
			result[k++] = _byte; 
		}
		return result;
	}
}
