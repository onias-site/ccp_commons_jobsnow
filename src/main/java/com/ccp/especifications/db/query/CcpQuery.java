package com.ccp.especifications.db.query;

/**
 * Representa o nó query dentro do builder fluent de queries do Elasticsearch.
 * Serve como ponto de entrada para construir o bloco de consulta principal de uma requisição.
 */
public final class CcpQuery  extends CcpQueryComponent {

	CcpQuery(CcpQueryComponent parent) {
		super(parent, "query");
	}

	/**
	 * Inicia um bloco de query booleana (bool) dentro do nó query.
	 */
	public CcpQueryBool startBool() {
		return new CcpQueryBool(this);
	}

	/**
	 * Finaliza o nó query e retorna ao nó raiz da requisição (CcpQueryOptions).
	 */
	public CcpQueryOptions endQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQuery(this.parent);
	}
	
}
