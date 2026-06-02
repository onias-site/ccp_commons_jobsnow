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
		try {
			String decode = URLDecoder.decode(this.content, StandardCharsets.UTF_8.toString());
			return decode;
		} catch (UnsupportedEncodingException e) {
			return this.content;
		}

	}
	
	public String asEnconded() {
		try {
			String encode = URLEncoder.encode(this.content, StandardCharsets.UTF_8.toString());
			return encode;
		} catch (UnsupportedEncodingException e) {
			return this.content;
		}
	}

	public String getContent() {
		return this.content;
	}

	
}
