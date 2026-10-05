package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

/** The {@code must} clause of a boolean query: its conditions must hold and affect the score. */
public final class CcpQueryMust extends CcpQueryBooleanOperator {
	/**
	 * Starts the must clause under a bool node.
	 * @param parent the bool node
	 */
	CcpQueryMust(CcpQueryComponent parent) {
		super(parent, "must");
	}

	/**
	 * Ends the clause and adds it to the bool node.
	 * @return a copy of the bool node with the clause
	 */
	public CcpQueryBool endMustAndBackToBool() {
		return this.parent.addChild(this);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#matchPhrase(CcpEntityField, Object)}.
	 * @param field the field
	 * @param value the phrase
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryMust matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#prefix(CcpEntityField, Object)}.
	 * @param field the field
	 * @param value the prefix
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryMust prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#term(CcpJsonFieldName, Object)}.
	 * @param field the field
	 * @param value the exact value
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryMust term(CcpJsonFieldName field, Object value) {
		return super.term(field, value);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#terms(CcpJsonFieldName, Object)}.
	 * @param field the field
	 * @param value the accepted values
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryMust terms(CcpJsonFieldName field, Object value) {
		return super.terms(field, value);
	}

	/**
	 * Creates an empty must clause with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryMust newInstance = new CcpQueryMust(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#exists(String)}.
	 * @param field the field that must exist
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryMust exists(String field) {
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

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#match(CcpJsonFieldName, Object)}.
	 * @param field the field
	 * @param value the text
	 * @return a copy of the clause with the condition
	 */
	public CcpQueryMust match(CcpEntityField field, Object value) {
		return super.match(field, value);
	}
}
