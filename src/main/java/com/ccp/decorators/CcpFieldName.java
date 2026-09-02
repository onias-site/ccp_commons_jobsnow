package com.ccp.decorators;


/**
 * Implementação concreta de {@code CcpJsonFieldName} que encapsula qualquer valor como nome de campo JSON.
 * Permite criar identificadores de campo de forma dinâmica a partir de strings ou outros objetos.
 */
public final class CcpFieldName implements CcpJsonFieldName{

	private final Object name;

	public CcpFieldName(Object name) {
		this.name = name;
	}
	
	public CcpFieldName(String name) {
		this((Object)name);
	}

	public String name() {
		String valorMais = "" + this.name;
		return valorMais;
	}

	public String toString() {
		String name = this.name();
		return name;
	}
}
