package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

/**
 * Componente de query simplificada que estende {@code CcpQueryBooleanOperator}. Permite construir consultas
 * com cláusulas {@code term}, {@code terms}, {@code match}, {@code matchPhrase}, {@code prefix} e {@code exists}
 * de forma fluente, retornando ao {@code CcpQueryOptions} pai ao chamar {@code endSimplifiedQueryAndBackToRequest()}.
 */
public final class CcpQuerySimplifiedQuery  extends CcpQueryBooleanOperator {

	/**
	 * Cria um componente de query simplificada associado ao componente pai.
	 */
	CcpQuerySimplifiedQuery(CcpQueryComponent parent) {
		super(parent, "query");
	}

	/**
	 * Adiciona uma cláusula {@code terms} para o campo e valor fornecidos.
	 */
	public CcpQuerySimplifiedQuery  terms(CcpEntityField field, Object value) {
		return super.terms(field, value);
	}

	/**
	 * Adiciona uma cláusula {@code prefix} para o campo e valor fornecidos.
	 */
	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	/**
	 * Finaliza a query simplificada e retorna ao {@code CcpQueryOptions} pai.
	 */
	public CcpQueryOptions endSimplifiedQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQuerySimplifiedQuery(this.parent);
	}

	Object getValue() {
		return this.json.content;
	}

	@SuppressWarnings("unchecked")
	protected CcpQuerySimplifiedQuery copy() {
		CcpQuerySimplifiedQuery instanceCopy = this.getInstanceCopy();

		instanceCopy.name = this.name;

		instanceCopy.parent = this.parent.copy();

		instanceCopy.json = this.json.copy();

		return instanceCopy;
	}

	@SuppressWarnings("unchecked")
	CcpQuerySimplifiedQuery addChild(CcpQueryComponent child) {

		CcpQuerySimplifiedQuery instanceCopy = this.copy();

		Object value = child.getValue();

		instanceCopy.json = instanceCopy.json.put(new CcpFieldName(child.name), value);

		return instanceCopy;
	}

	/**
	 * Adiciona uma cláusula {@code matchPhrase} para o campo e valor fornecidos.
	 */
	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	/**
	 * Adiciona uma cláusula {@code term} para o campo e valor fornecidos.
	 */
	public CcpQuerySimplifiedQuery term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	/**
	 * Adiciona uma cláusula {@code match} para o campo e valor fornecidos.
	 */
	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery match(CcpJsonFieldName field, Object value) {
		return super.match(field, value);
	}

	/**
	 * Adiciona uma cláusula {@code exists} para o campo fornecido.
	 */
	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery exists(String field) {
		return super.exists(field);
	}

	@SuppressWarnings("unchecked")
	protected CcpQuerySimplifiedQuery addCondition(String field, Object value, String key) {
		Map<String, Object> map = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(field), value).getContent();
		Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(key), map).getContent();

		CcpQuerySimplifiedQuery clone = this.copy();
		clone.json = new CcpJsonRepresentation(outerMap);
		return clone;
	}

	/**
	 * Retorna {@code true} se este componente já possui pelo menos uma condição adicionada.
	 */
	public boolean hasChildreen() {
		return false == this.json.content.isEmpty();
	}


}
