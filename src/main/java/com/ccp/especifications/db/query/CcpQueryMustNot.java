package com.ccp.especifications.db.query;

import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

/** The {@code must_not} clause of a boolean query: documents matching any of its conditions are excluded. */
public final class CcpQueryMustNot extends CcpQueryBooleanOperator {
	/**
	 * Starts the must_not clause under a bool node.
	 * @param parent the bool node
	 */
	CcpQueryMustNot(CcpQueryComponent parent) {
		super(parent, "must_not");
	}

	/**
	 * Ends the clause and adds it to the bool node.
	 * @return a copy of the bool node with the clause
	 */
	public CcpQueryBool endMustNotAndBackToBool() {
		return this.parent.addChild(this);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#prefix(CcpEntityField, Object)}.
	 * @param field the field
	 * @param value the prefix
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryMustNot prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#matchPhrase(CcpEntityField, Object)}.
	 * @param field the field
	 * @param value the phrase
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryMustNot matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#term(CcpJsonFieldName, Object)}.
	 * @param field the field
	 * @param value the exact value
	 * @return a copy of the clause with the condition
	 */
	public CcpQueryMustNot term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	/**
	 * Creates an empty must_not clause with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryMustNot newInstance = new CcpQueryMustNot(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#exists(String)}.
	 * @param field the field that must exist
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryMustNot exists(String field) {
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
