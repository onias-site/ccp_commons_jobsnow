package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

public final class CcpQueryShould extends CcpQueryBooleanOperator {
	enum JsonFieldNames implements CcpJsonFieldName {
		minimum_should_match
	}

	CcpQueryShould(CcpQueryComponent parent) {
		super(parent, "should");
	}

	@SuppressWarnings("unchecked")
	public CcpQueryShould prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	/**
	 * Stores the value as text. As a number it went through the component copy via Gson, which returns
	 * every number as {@code Double}, and reached Elasticsearch as {@code 1.0} — a value it rejects
	 * ({@code number_format_exception}), bringing down the whole query with a 400. The parameter accepts text
	 * ({@code "1"}, {@code "75%"}) precisely for these cases.
	 */
	CcpQueryShould setMinimumShouldMatch(int minimumShouldMatch) {
		CcpQueryShould copy = this.copy();
		String minimumShouldMatchAsText = String.valueOf(minimumShouldMatch);
		copy.parent.json = copy.parent.json.put(JsonFieldNames.minimum_should_match, minimumShouldMatchAsText);
		return copy;
	}

	public CcpQueryBool endShouldAndBackToBool() {
		CcpQueryComponent copy = this.parent.copy();
		CcpQueryBool boolQuery = copy.addChild(this);
		return boolQuery;
	}

	public CcpQueryShould matchPhrase2(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	public CcpQueryShould match(CcpEntityField field, Object value) {
		return super.match(field, value);
	}

	public CcpQueryShould matchPhrase(String field, Object value, double boost) {
		CcpQueryShould shouldWithCondition = this.addCondition(field, value, "match_phrase", boost, "");
		return shouldWithCondition;
	}

	public CcpQueryShould match(String field, Object value, double boost, String operator) {
		CcpQueryShould shouldWithCondition = this.addCondition(field, value, "match", boost, operator);
		return shouldWithCondition;
	}

	public CcpQueryShould term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryShould newInstance = new CcpQueryShould(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	@SuppressWarnings("unchecked")
	public CcpQueryShould exists(String field) {
		return super.exists(field);
	}

	public CcpQueryBool startBool() {
		CcpQueryBool boolQuery = new CcpQueryBool(this);
		return boolQuery;
	}
}
