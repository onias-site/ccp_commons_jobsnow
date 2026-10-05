package com.ccp.especifications.text.extractor;

/** Contract for extracting text from documents (Apache Tika). */
public interface CcpTextExtractor {

	/**
	 * Extracts the plain text of the content.
	 * @param content the document content
	 * @return the plain text
	 */
	String extractText(String content);
}
