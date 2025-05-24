package com.ccp.decorators;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CcpUrlDecorator implements CcpDecorator<String> {
	public final String content;

	protected CcpUrlDecorator(String content) {
		this.content = content;
	}
	
	public String toString() {
		return this.content;
	}

	public String asDecoded() {
		String decode;
		try {
			decode = URLDecoder.decode(this.content, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			return this.content;
		}
		return decode;

	}
	
	public String asEnconded() {
		String encode;
		try {
			encode = URLEncoder.encode(this.content, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			return this.content;
		}
		return encode;

	}

	public String getContent() {
		return this.content;
	}

	
}
