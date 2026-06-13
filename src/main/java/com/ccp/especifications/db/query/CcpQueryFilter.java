package com.ccp.especifications.db.query;

/**
 * Representa o nó filter dentro de uma query booleana do Elasticsearch.
 * Diferentemente de must, as condições de filtro não afetam a pontuação de relevância dos documentos.
 */
public final class CcpQueryFilter  extends CcpQueryComponent {
	CcpQueryFilter(CcpQueryComponent parent) {
		super(parent, "filter");
	}

	/**
	 * Inicia um bloco bool aninhado dentro do filter.
	 */
	 public CcpQueryBool startBool() {
		return new CcpQueryBool(this);
	}
	
	/**
	 * Finaliza o filter e retorna ao nó bool pai.
	 */
	public CcpQueryBool endFilterAndBackToBool() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryFilter(this.parent);
	}
}