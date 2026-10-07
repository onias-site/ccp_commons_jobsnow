package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

/**
 * A {@code query} node without the bool wrapper, holding a single condition: each new condition replaces the previous
 * one. Ended by going back to the request with {@link #endSimplifiedQueryAndBackToRequest()}.
 */
public final class CcpQuerySimplifiedQuery extends CcpQueryBooleanOperator {
	/**
	 * Starts the simplified query under the request.
	 * @param parent the request
	 */
	CcpQuerySimplifiedQuery(CcpQueryComponent parent) {
		super(parent, "query");
	}

	/**
	 * Sets the condition to {@code {"terms": {field: value}}}.
	 * @param field the field
	 * @param value the accepted values
	 * @return a copy with the condition
	 */
	public CcpQuerySimplifiedQuery terms(CcpEntityField field, Object value) {
		return super.terms(field, value);
	}

	/**
	 * Sets the condition to {@code {"prefix": {field: value}}}.
	 * @param field the field
	 * @param value the prefix
	 * @return a copy with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	/**
	 * Ends the query and adds it to the request.
	 * @return a copy of the request with the query
	 */
	public CcpQueryOptions endSimplifiedQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}

	/**
	 * Creates an empty simplified query with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQuerySimplifiedQuery newInstance = new CcpQuerySimplifiedQuery(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	/**
	 * Returns the condition map (not a list, unlike the other clauses).
	 * @return the condition
	 */
	Object getValue() {
		return this.json.content;
	}

	/**
	 * Copies the node, its parent chain and its condition.
	 * @return the copy
	 */
	@SuppressWarnings("unchecked")
	protected CcpQuerySimplifiedQuery copy() {
		CcpQuerySimplifiedQuery instanceCopy = this.getInstanceCopy();
		instanceCopy.name = this.name;
		instanceCopy.parent = this.parent.copy();
		instanceCopy.json = this.json.copy();
		return instanceCopy;
	}

	/**
	 * Returns a copy with {@code {child.name: child.value}} added to the condition.
	 * @param child the child node (e.g. a range)
	 * @return the copy
	 */
	@SuppressWarnings("unchecked")
	CcpQuerySimplifiedQuery addChild(CcpQueryComponent child) {
		CcpQuerySimplifiedQuery instanceCopy = this.copy();
		Object value = child.getValue();
		CcpFieldName childKey = new CcpFieldName(child.name);
		instanceCopy.json = instanceCopy.json.put(childKey, value);
		return instanceCopy;
	}

	/**
	 * Sets the condition to {@code {"match_phrase": {field: value}}}.
	 * @param field the field
	 * @param value the phrase
	 * @return a copy with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	/**
	 * Sets the condition to {@code {"term": {field: value}}}.
	 * @param field the field
	 * @param value the exact value
	 * @return a copy with the condition
	 */
	public CcpQuerySimplifiedQuery term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	/**
	 * Sets the condition to {@code {"match": {field: value}}}.
	 * @param field the field
	 * @param value the text
	 * @return a copy with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery match(CcpJsonFieldName field, Object value) {
		return super.match(field, value);
	}

	/**
	 * Sets the condition to {@code {"exists": {"field": field}}}.
	 * @param field the field that must exist
	 * @return a copy with the condition
	 */
	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery exists(String field) {
		return super.exists(field);
	}

	/**
	 * Replaces the whole content of a copy with {@code {conditionType: {field: value}}}; unlike the other clauses, a {@code null}
	 * value is stored as is.
	 * @param field the field
	 * @param value the value
	 * @param conditionType the condition type
	 * @return the copy
	 */
	@SuppressWarnings("unchecked")
	protected CcpQuerySimplifiedQuery addCondition(String field, Object value, CcpQueryConditionType conditionType) {
		CcpFieldName fieldKey = new CcpFieldName(field);
		CcpJsonRepresentation conditionJson = CcpOtherConstants.EMPTY_JSON.put(fieldKey, value);
		Map<String, Object> map = conditionJson.getContent();
		CcpJsonRepresentation outerJson = CcpOtherConstants.EMPTY_JSON.put(conditionType, map);
		Map<String, Object> outerMap = outerJson.getContent();
		CcpQuerySimplifiedQuery clone = this.copy();
		clone.json = new CcpJsonRepresentation(outerMap);
		return clone;
	}

	/**
	 * Tells whether the query has a condition.
	 * @return {@code true} when the content is not empty
	 */
	public boolean hasChildreen() {
		boolean contentEmpty = this.json.content.isEmpty();
		boolean hasContent = false == contentEmpty;
		return hasContent;
	}
}
