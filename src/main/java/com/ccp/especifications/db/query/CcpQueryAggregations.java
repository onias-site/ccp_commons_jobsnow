package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;


/**
 * Representa o nó aggs (agregações) no builder fluent de queries do Elasticsearch.
 * Permite adicionar agregações métricas (min, max, avg, sum) e iniciar buckets (agrupamentos).
 */
public final class CcpQueryAggregations extends CcpQueryComponent{
	enum JsonFieldNames implements CcpJsonFieldName{
		field
	}
	
	 CcpQueryAggregations(CcpQueryComponent parent) {
		super(parent, "aggs");
	}

	/**
	 * Finaliza o bloco de agregações e retorna ao nó raiz da requisição.
	 */
	public CcpQueryOptions endAggregationsAndBackToRequest() {
		return this.parent.addChild(this);
	}

	/**
	 * Finaliza o bloco de agregações e retorna ao contexto de bucket pai.
	 */
	public BucketAggregation endAggregationsAndBackToBucket() {
		return this.parent.addChild(this);
	}

	/**
	 * Adiciona uma agregação de valor mínimo (min) sobre o campo informado.
	 * @param aggregationName nome da agregação
	 * @param fieldName campo sobre o qual calcular o mínimo
	 */
	public CcpQueryAggregations addMinAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "min");
		return copy;
	}

	private CcpQueryAggregations createAggregation(String aggregationName, CcpEntityField fieldName, String key) {
		CcpQueryAggregations copy = this.copy();
		Map<String, Object> c1 = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.field, fieldName).getContent();
		Map<String, Object> c2 = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(key), c1).getContent();
		copy.json = copy.json.put(new CcpFieldName(aggregationName), c2);
		return copy;
	}
	/**
	 * Adiciona uma agregação de valor máximo (max) sobre o campo informado.
	 * @param aggregationName nome da agregação
	 * @param fieldName campo sobre o qual calcular o máximo
	 */
	public CcpQueryAggregations addMaxAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "max");
		return copy;
	}

	/**
	 * Adiciona uma agregação de média (avg) sobre o campo informado.
	 * @param aggregationName nome da agregação
	 * @param fieldName campo sobre o qual calcular a média
	 */
	public CcpQueryAggregations addAvgAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "avg");
		return copy;
	}

	/**
	 * Inicia um bucket de agregação (terms ou histogram) com nome, campo e tamanho definidos.
	 * @param bucketName nome do bucket
	 * @param fieldName campo para agrupar
	 * @param size número máximo de buckets
	 */
	public BucketAggregation startBucket(String bucketName, CcpEntityField fieldName, long size) {
		BucketAggregation bucketAggregation = new BucketAggregation(this, bucketName, fieldName, size);
		return bucketAggregation;
				
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryAggregations(this.parent);
	}

	/**
	 * Adiciona uma agregação de soma (sum) sobre o campo informado.
	 * @param aggregationName nome da agregação
	 * @param fieldName campo sobre o qual calcular a soma
	 */
	public CcpQueryAggregations addSumAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "sum");
		return copy;
	}

}
