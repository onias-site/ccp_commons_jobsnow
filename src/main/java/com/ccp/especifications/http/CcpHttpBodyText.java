package com.ccp.especifications.http;

/** A text part of a multipart HTTP request. */
public class CcpHttpBodyText {
	/** The content type of the part. */
	public final CcpHttpContentType contentType;
	/** The form field name. */
	public final String name;
	/** The text content. */
	public final String text;

	/**
	 * Builds the text part.
	 * @param contentType the content type of the text
	 * @param name the form field name
	 * @param text the text content
	 */
	public CcpHttpBodyText(CcpHttpContentType contentType, String name, String text) {
		this.contentType = contentType;
		this.name = name;
		this.text = text;
	}
	
	
}
