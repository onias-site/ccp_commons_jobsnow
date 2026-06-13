package com.ccp.decorators;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Decorator sobre uma string de URL que oferece encode e decode de caracteres especiais usando o padrão
 * UTF-8 ({@code java.net.URLEncoder} / {@code URLDecoder}). Útil para montar e interpretar parâmetros de
 * query string.
 */
public class CcpUrlDecorator implements CcpDecorator<String> {
	public final String content;

	/**
	 * Encapsula a URL ou fragmento de URL.
	 */
	protected CcpUrlDecorator(String content) {
		this.content = content;
	}

	/**
	 * Retorna a string original.
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Decodifica os caracteres percent-encoded (ex.: {@code %40} → {@code @}).
	 * Retorna a string original em caso de codificação não suportada.
	 */
	public String asDecoded() {
		try {
			String decode = URLDecoder.decode(this.content, StandardCharsets.UTF_8.toString());
			return decode;
		} catch (UnsupportedEncodingException e) {
			return this.content;
		}

	}

	/**
	 * Codifica caracteres especiais para uso em query strings (ex.: {@code @} → {@code %40}).
	 * Retorna a string original em caso de codificação não suportada.
	 */
	public String asEnconded() {
		try {
			String encode = URLEncoder.encode(this.content, StandardCharsets.UTF_8.toString());
			return encode;
		} catch (UnsupportedEncodingException e) {
			return this.content;
		}
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna a string.
	 */
	public String getContent() {
		return this.content;
	}

}
