package com.ccp.especifications.db.query;

import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

/**
 * A {@code should_not} clause. Elasticsearch has no such clause (only {@code must}, {@code filter}, {@code should} and
 * {@code must_not}), so a request using it is rejected; it is not used anywhere in the workspace.
 */
public final class CcpQueryShouldNot extends CcpQueryBooleanOperator {
	/**
	 * Starts the clause under a bool node.
	 * @param parent the bool node
	 */
	CcpQueryShouldNot(CcpQueryComponent parent) {
		super(parent, "should_not");
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#prefix(CcpEntityField, Object)}.
	 * @param field the field
	 * @param value the prefix
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryShouldNot prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	/**
	 * Ends the clause and adds it to the bool node.
	 * @return a copy of the bool node with the clause
	 */
	public CcpQueryBool endShouldNotAndBackToBool() {
		CcpQueryComponent copy = this.parent.copy();
		CcpQueryBool boolQuery = copy.addChild(this);
		return boolQuery;
	}

	/**
	 * Creates an empty clause with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryShouldNot newInstance = new CcpQueryShouldNot(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#matchPhrase(CcpEntityField, Object)}.
	 * @param field the field
	 * @param value the phrase
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryShouldNot matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#term(CcpJsonFieldName, Object)}.
	 * @param field the field
	 * @param value the exact value
	 * @return a copy of the clause with the condition
	 */
	public CcpQueryShouldNot term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#exists(String)}.
	 * @param field the field that must exist
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryShouldNot exists(String field) {
		return super.exists(field);
	}

	/**
	 * Starts a bool query inside the clause.
	 * @return the bool node
	 */
	public CcpQueryBool startBool() {
		CcpQueryBool boolQuery = new CcpQueryBool(this);
		return boolQuery;
	}
}
