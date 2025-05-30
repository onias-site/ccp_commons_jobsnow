package com.ccp.decorators;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import com.ccp.exceptions.inputstream.CcpErrorInputStreamMissing;


public class CcpInputStreamDecorator implements CcpDecorator<String> {
	
	private final String content;

	protected CcpInputStreamDecorator(String content) {
		this.content = content;
	}
	
	public String toString() {
		return this.content;
	}

	public InputStream environmentVariables() {
		
		String getenv = System.getenv(this.content);
		
		if(getenv == null) {
			throw new CcpErrorInputStreamMissing(this.content);
		}

		if(getenv.trim().isEmpty()) {
			throw new CcpErrorInputStreamMissing(this.content);
		}

		CcpStringDecorator csd = new CcpStringDecorator(getenv);
		CcpFileDecorator file = csd.file();
		
		if(file.isFile()) {
			CcpInputStreamDecorator inputStreamFrom = csd.inputStreamFrom();
			InputStream file2 = inputStreamFrom.file();
			return file2;
		}
		
		byte[] bytes = getenv.getBytes();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		return byteArrayInputStream;
	}
	
	public InputStream classLoader() {
		Class<? extends CcpInputStreamDecorator> class1 = this.getClass();
		ClassLoader classLoader = class1.getClassLoader();
		URL resource = classLoader.getResource(this.content);
		if(resource == null) {
			throw new CcpErrorInputStreamMissing(this.content);
		}
		try {
			InputStream stream = resource.openStream();
			return stream;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public InputStream file() {
		if(new CcpStringDecorator(this.content).file().exists() == false) {
			throw new CcpErrorInputStreamMissing(this.content);
		}
		try {
			FileInputStream fileInputStream = new FileInputStream(this.content);
			return fileInputStream;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public InputStream byteArray() {
		byte[] bytes = this.content.getBytes();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		return byteArrayInputStream;
	}
	
	public InputStream fromEnvironmentVariablesOrClassLoaderOrFile() {

		try {
			InputStream is = this.environmentVariables();
			return is;
		} catch (CcpErrorInputStreamMissing e) {

		}
		
		try {
			InputStream is = this.classLoader();
			return is;
		} catch (CcpErrorInputStreamMissing e) {

		}
		InputStream is = this.file();
		return is;
	}

	public String getContent() {
		return this.content;
	}
}
