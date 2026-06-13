package com.ccp.especifications.db.query;

/**
 * Representa o nó range no builder de queries do Elasticsearch.
 * Serve como contêiner para definições de intervalo de um ou mais campos, permitindo retornar ao contexto pai correto após a definição.
 */
public class CcpQueryRange extends CcpQueryComponent {

	CcpQueryRange(CcpQueryComponent parent) {
		super(parent, "range"); 
	}
   
	@SuppressWarnings("unchecked")
	
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryRange(this.parent);
	}

	/**
	 * Inicia a definição de intervalo para um campo específico.
	 */
	public CcpQueryFieldRange startFieldRange(String fieldName) {
		return new CcpQueryFieldRange(this, fieldName);
	}
	
	/**
	 * Finaliza o range e retorna ao contexto de query simplificada.
	 */
	public CcpQuerySimplifiedQuery endRangeAndBackToSimplifiedQuery() {
		return this.parent.addChild(this);
	}
	/**
	 * Finaliza o range e retorna ao contexto should.
	 */
	public CcpQueryShould endRangeAndBackToShould() {
		return this.parent.addChild(this);
	}

	/**
	 * Finaliza o range e retorna ao contexto must.
	 */
	public CcpQueryMust endRangeAndBackToMust() {
		return this.parent.addChild(this);
	}

	/**
	 * Finaliza o range e retorna ao contexto should_not.
	 */
	public CcpQueryShouldNot endRangeAndBackToShouldNot() {
		return this.parent.addChild(this);
	}

	/**
	 * Finaliza o range e retorna ao contexto must_not.
	 */
	public CcpQueryMustNot endRangeAndBackToMustNot() {
		return this.parent.addChild(this);
	}

}
