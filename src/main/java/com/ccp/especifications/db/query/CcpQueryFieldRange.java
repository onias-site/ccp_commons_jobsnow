package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpFieldName;

/**
 * Representa as condições de intervalo para um campo específico dentro de um bloco range do Elasticsearch.
 * Permite encadear operadores de comparação (lt, lte, gt, gte) de forma fluent.
 */
public class CcpQueryFieldRange extends CcpQueryComponent{

	CcpQueryFieldRange(CcpQueryComponent parent, String name) {
		super(parent, name);
	}

	@SuppressWarnings("unchecked")
	protected CcpQueryFieldRange getInstanceCopy() {
		return new CcpQueryFieldRange(this.parent, this.name);
	}

	
	private CcpQueryFieldRange putOperator(String operatorName, Object value) {
		CcpQueryFieldRange copy = this.copy();
		copy.json = copy.json.put(new CcpFieldName(operatorName), value);
		return copy;
	}
	
	/**
	 * Adiciona a condição lt (menor que) ao intervalo do campo.
	 */
	public CcpQueryFieldRange lessThan(Object value) {
		return this.putOperator("lt", value);
	}

	/**
	 * Adiciona a condição lte (menor ou igual a) ao intervalo do campo.
	 */
	public CcpQueryFieldRange lessThanEquals(Object value) {
		return this.putOperator("lte", value);
	}

	/**
	 * Adiciona a condição gt (maior que) ao intervalo do campo.
	 */
	public CcpQueryFieldRange greaterThan(Object value) {
		return this.putOperator("gt", value);
	}

	/**
	 * Adiciona a condição gte (maior ou igual a) ao intervalo do campo.
	 */
	public CcpQueryFieldRange greaterThanEquals(Object value) {
		return this.putOperator("gte", value);
	}
	
	/**
	 * Finaliza a definição do intervalo deste campo e retorna ao nó range pai.
	 */
	public CcpQueryRange endFieldRangeAndBackToRange() {
		return this.parent.addChild(this);
	}

}
