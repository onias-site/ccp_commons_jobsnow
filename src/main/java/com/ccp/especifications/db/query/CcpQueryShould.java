package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;



/**
 * Representa o nó should dentro de uma query booleana do Elasticsearch.
 * As condições adicionadas aqui são opcionais e incrementam a pontuação de relevância dos documentos que as satisfaçam.
 * Suporta o parâmetro minimum_should_match para exigir que pelo menos N condições sejam verdadeiras.
 */
public final class CcpQueryShould  extends CcpQueryBooleanOperator {
	enum JsonFieldNames implements CcpJsonFieldName{
		minimum_should_match
	}

	CcpQueryShould(CcpQueryComponent parent) {
		super(parent, "should");
		
	}
	
	@SuppressWarnings("unchecked")
	public CcpQueryShould prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	CcpQueryShould setMinimumShouldMatch(int minimumShouldMatch) {
		CcpQueryShould copy = this.copy();
		copy.parent.json = copy.parent.json.put(JsonFieldNames.minimum_should_match, minimumShouldMatch);
		return copy;
	}
	
	/**
	 * Finaliza o should e retorna ao nó bool pai.
	 */
	public CcpQueryBool endShouldAndBackToBool() {
		CcpQueryComponent copy = this.parent.copy();
		CcpQueryBool addChild = copy.addChild(this);
		return addChild;
	}
	
	public CcpQueryShould matchPhrase2(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	public CcpQueryShould match(CcpEntityField field, Object value) {
		return super.match(field, value);
	}
	
	public CcpQueryShould matchPhrase(String field, Object value, double boost) {
		CcpQueryShould addCondition = this.addCondition(field, value, "match_phrase", boost, "");
		return addCondition;
	}
	
	public CcpQueryShould match(String field, Object value, double boost, String operator) {
		CcpQueryShould addCondition = this.addCondition(field, value, "match", boost, operator);
		return addCondition;
	}

	public CcpQueryShould term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryShould(this.parent);
	}
	
	@SuppressWarnings("unchecked")
	public CcpQueryShould exists(String field) {
		return super.exists(field);
	}	
	
	public CcpQueryBool startBool() {
		return new CcpQueryBool(this);
	}

}
