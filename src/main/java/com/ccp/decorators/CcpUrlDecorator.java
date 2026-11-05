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

	/** Texts of the path decoding. */
	enum Constants implements CcpJsonFieldName {
		PLUS_SIGN { public String getValue() { return "+"; } },
		ENCODED_PLUS_SIGN { public String getValue() { return "%2B"; } },
		;
	}

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
	 * Decodes a URL path: percent-encoded characters are decoded and a literal {@code +} stays a {@code +}, as RFC 3986
	 * says for paths ({@link #asDecoded()} follows the form encoding, where {@code +} is a space). Until 2026-11-04 an
	 * e-mail with {@code +} in the path ({@code name+alias@gmail.com}) was refused with 400, and its encoded form
	 * ({@code %2B}) was saved without the {@code %}.
	 * @return the decoded path
	 */
	public String asPathDecoded() {
		String plusSign = Constants.PLUS_SIGN.getValue();
		String encodedPlusSign = Constants.ENCODED_PLUS_SIGN.getValue();
		String plusKept = this.content.replace(plusSign, encodedPlusSign);
		CcpUrlDecorator plusKeptUrl = new CcpUrlDecorator(plusKept);
		String decoded = plusKeptUrl.asDecoded();
		return decoded;
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
