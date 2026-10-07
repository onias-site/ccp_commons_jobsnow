package com.ccp.especifications.http;

/** The supported content types of e-mails and multipart parts. */
public enum CcpHttpContentType {
	/** Plain text. */
	TEXT_PLAIN("text/plain"),
	/** HTML. */
	TEXT_HTML("text/html")
	;

	/** The MIME type, as the providers expect it (the constant name is not one). */
	public final String mimeType;

	/**
	 * Associates the constant with its MIME type.
	 * @param mimeType the MIME type
	 */
	private CcpHttpContentType(String mimeType) {
		this.mimeType = mimeType;
	}
}
