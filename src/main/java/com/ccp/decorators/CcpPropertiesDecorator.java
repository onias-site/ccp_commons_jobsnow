package com.ccp.decorators;

import java.io.InputStream;

public class CcpPropertiesDecorator implements CcpDecorator<CcpInputStreamDecorator> {

	private final CcpInputStreamDecorator content;

	protected CcpPropertiesDecorator(String content) {
		this.content = new CcpInputStreamDecorator(content);
	}

	private CcpJsonRepresentation getMapInInputStream(InputStream is) {
		CcpJsonRepresentation response = new CcpJsonRepresentation(is);
		return response;

	}
	
	public CcpJsonRepresentation environmentVariables() {
		InputStream is = this.content.environmentVariables();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	public CcpJsonRepresentation classLoader() {
		InputStream is = this.content.classLoader();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	public CcpJsonRepresentation file() {
		InputStream is = this.content.file();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	public CcpJsonRepresentation environmentVariablesOrClassLoaderOrFile() {
		InputStream is = this.content.fromEnvironmentVariablesOrClassLoaderOrFile();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	public CcpInputStreamDecorator getContent() {
		return this.content;
	}
	
	
	
}
