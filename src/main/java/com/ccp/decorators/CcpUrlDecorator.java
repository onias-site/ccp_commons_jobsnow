package com.ccp.decorators;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Decorator over a URL text that encodes and decodes special characters with UTF-8 ({@code java.net.URLEncoder} /
 * {@code URLDecoder}). Useful to build and read query string parameters.
 */
public class CcpUrlDecorator implements CcpDecorator<String> {
	/** The URL or URL fragment. */
	public final String content;

	/**
	 * Wraps the URL or URL fragment.
	 * @param content the URL text
	 */
	protected CcpUrlDecorator(String content) {
		this.content = content;
	}

	/**
	 * Returns the original text.
	 * @return the original text
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Decodes percent-encoded characters (e.g. {@code %40} becomes {@code @}, {@code +} becomes a space).
	 * @return the decoded text, or the original text if the encoding is not supported
	 */
	public String asDecoded() {
		try {
			String toString = StandardCharsets.UTF_8.toString();
			String decode = URLDecoder.decode(this.content, toString);
			return decode;
		} catch (UnsupportedEncodingException e) {
			return this.content;
		}

	}

	/**
	 * Encodes special characters for use in query strings (e.g. {@code @} becomes {@code %40}, a space becomes {@code +}).
	 * @return the encoded text, or the original text if the encoding is not supported
	 */
	public String asEnconded() {
		try {
			String toString2 = StandardCharsets.UTF_8.toString();
			String encode = URLEncoder.encode(this.content, toString2);
			return encode;
		} catch (UnsupportedEncodingException e) {
			return this.content;
		}
	}

	/**
	 * Returns the original text.
	 * @return the original text
	 */
	public String getContent() {
		return this.content;
	}

}
