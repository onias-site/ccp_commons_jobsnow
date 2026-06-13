package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;



/**
 * Representa um bucket de agregação do Elasticsearch (terms ou histogram) dentro do builder fluent de queries.
 * Permite configurar um agrupamento por campo e tamanho, e encerrar voltando ao nó pai de agregações.
 */
public final class BucketAggregation extends CcpQueryComponent {
	enum JsonFieldNames implements CcpJsonFieldName{
		field
	}
	
	private final CcpEntityField fieldName;
	private final long size;
	
	BucketAggregation(CcpQueryComponent parent, String name, CcpEntityField fieldName, long size) {
		super(parent, name);
		this.fieldName = fieldName;
		this.size = size;
	}

	/**
	 * Finaliza o bucket como agregação do tipo terms (agrupamento por valor exato) e retorna ao contexto pai de agregações.
	 */
	public CcpQueryAggregations endTermsBuckedAndBackToAggregations() {
		CcpQueryAggregations addChild = this.getStatisRequest("size", "terms");
		return addChild;
	}

	/**
	 * Finaliza o bucket como agregação do tipo histogram (agrupamento por intervalo numérico) e retorna ao contexto pai de agregações.
	 */
	public CcpQueryAggregations endHistogramBuckedAndBackToAggregations() {
		CcpQueryAggregations addChild = this.getStatisRequest("interval", "histogram");
		return addChild;
	}

	private CcpQueryAggregations getStatisRequest(String p1, String p2) {
		CcpQueryComponent copy = this.copy();
		Map<String, Object> content = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.field, this.fieldName)
				.put(new CcpFieldName(p1), this.size).getContent();
		copy.json = copy.json.put(new CcpFieldName(p2), content);
		CcpQueryAggregations addChild = this.parent.addChild(copy);
		return addChild;
	}
	
	/**
	 * Inicia sub-agregações aninhadas dentro deste bucket.
	 */
	public CcpQueryAggregations startAggregations() {
		return new CcpQueryAggregations(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new BucketAggregation(this.parent, this.name, this.fieldName, this.size);
	}

}
