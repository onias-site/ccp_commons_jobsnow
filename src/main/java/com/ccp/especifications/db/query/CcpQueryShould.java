package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

/**
 * The {@code should} clause of a boolean query: optional conditions that raise the score; the
 * {@code minimum_should_match} of the parent bool node tells how many must hold.
 */
public final class CcpQueryShould extends CcpQueryBooleanOperator {
	/** Fields of the parent bool node set by this clause. */
	enum JsonFieldNames implements CcpJsonFieldName {
		/** How many should conditions must hold. */
		minimum_should_match
	}

	/**
	 * Starts the should clause under a bool node.
	 * @param parent the bool node
	 */
	CcpQueryShould(CcpQueryComponent parent) {
		super(parent, "should");
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#prefix(CcpEntityField, Object)}.
	 * @param field the field
	 * @param value the prefix
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryShould prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	/**
	 * Stores {@code minimum_should_match} in the parent bool node, as text. As a number it went through the component
	 * copy via Gson, which returns every number as {@code Double}, and reached Elasticsearch as {@code 1.0}, a value it
	 * rejects ({@code number_format_exception}), bringing down the whole query with a 400. The parameter accepts text
	 * ({@code "1"}, {@code "75%"}) precisely for these cases.
	 * @param minimumShouldMatch how many should conditions must hold
	 * @return a copy of the clause
	 */
	CcpQueryShould setMinimumShouldMatch(int minimumShouldMatch) {
		CcpQueryShould copy = this.copy();
		String minimumShouldMatchAsText = String.valueOf(minimumShouldMatch);
		copy.parent.json = copy.parent.json.put(JsonFieldNames.minimum_should_match, minimumShouldMatchAsText);
		return copy;
	}

	/**
	 * Ends the clause and adds it to the bool node.
	 * @return a copy of the bool node with the clause
	 */
	public CcpQueryBool endShouldAndBackToBool() {
		CcpQueryComponent copy = this.parent.copy();
		CcpQueryBool boolQuery = copy.addChild(this);
		return boolQuery;
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#matchPhrase(CcpEntityField, Object)}.
	 * @param field the field
	 * @param value the phrase
	 * @return a copy of the clause with the condition
	 */
	public CcpQueryShould matchPhrase2(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#match(CcpJsonFieldName, Object)}.
	 * @param field the field
	 * @param value the text
	 * @return a copy of the clause with the condition
	 */
	public CcpQueryShould match(CcpEntityField field, Object value) {
		return super.match(field, value);
	}

	/**
	 * Adds {@code {"match_phrase": {field: {"query": value, "boost": boost}}}} for a field given by name.
	 * @param field the field name
	 * @param value the phrase; {@code null} adds nothing
	 * @param boost the weight of the condition
	 * @return a copy of the clause with the condition
	 */
	public CcpQueryShould matchPhrase(String field, Object value, double boost) {
		CcpQueryShould shouldWithCondition = this.addCondition(field, value, CcpQueryConditionType.match_phrase, boost, "");
		return shouldWithCondition;
	}

	/**
	 * Adds {@code {"match": {field: {"query": value, "boost": boost, "operator": operator}}}} for a field given by name.
	 * @param field the field name
	 * @param value the text; {@code null} adds nothing
	 * @param boost the weight of the condition
	 * @param operator {@code and}/{@code or}, or blank
	 * @return a copy of the clause with the condition
	 */
	public CcpQueryShould match(String field, Object value, double boost, String operator) {
		CcpQueryShould shouldWithCondition = this.addCondition(field, value, CcpQueryConditionType.match, boost, operator);
		return shouldWithCondition;
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#term(CcpJsonFieldName, Object)}.
	 * @param field the field
	 * @param value the exact value
	 * @return a copy of the clause with the condition
	 */
	public CcpQueryShould term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	/**
	 * Creates an empty should clause with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryShould newInstance = new CcpQueryShould(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	/**
	 * Typed variant of {@link CcpQueryBooleanOperator#exists(String)}.
	 * @param field the field that must exist
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQueryShould exists(String field) {
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
