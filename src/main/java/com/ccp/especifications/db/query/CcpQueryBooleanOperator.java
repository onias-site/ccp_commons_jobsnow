package com.ccp.especifications.db.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

/**
 * Abstract base of the clauses of an Elasticsearch boolean query (must, should, filter, must_not): keeps
 * the list of conditions, in insertion order and without repetition, and offers the methods that add each kind of
 * condition. Every method returns a copy; a {@code null} value adds no condition.
 */
public abstract class CcpQueryBooleanOperator extends CcpQueryComponent {
	/** Fields inside the conditions. */
	enum JsonFieldNames implements CcpJsonFieldName {
		/** The boolean operator between the terms of a match ({@code and}/{@code or}). */
		operator,
		/** The weight of the condition in the score. */
		boost,
		/** The text searched. */
		query,
		/** The field that must exist, in an {@code exists} condition. */
		field
	}

	/** The conditions of the clause. */
	protected Set<Object> items = new LinkedHashSet<>();

	/**
	 * Starts a clause under its parent.
	 * @param parent the parent node
	 * @param name the clause name ({@code must}, {@code should}...)
	 */
	CcpQueryBooleanOperator(CcpQueryComponent parent, String name) {
		super(parent, name);
	}

	/**
	 * Adds {@code {"term": {field: value}}} (exact value).
	 * @param <T> the concrete clause type
	 * @param field the field
	 * @param value the exact value; {@code null} adds nothing
	 * @return a copy of the clause with the condition
	 */
	public <T extends CcpQueryBooleanOperator> T term(CcpJsonFieldName field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, CcpQueryConditionType.term);
		return operatorWithCondition;
	}

	/**
	 * Adds {@code {"terms": {field: value}}} (any of the values).
	 * @param <T> the concrete clause type
	 * @param field the field
	 * @param value the collection of accepted values; {@code null} adds nothing
	 * @return a copy of the clause with the condition
	 */
	public <T extends CcpQueryBooleanOperator> T terms(CcpJsonFieldName field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, CcpQueryConditionType.terms);
		return operatorWithCondition;
	}

	/**
	 * Adds {@code {"prefix": {field: value}}}.
	 * @param <T> the concrete clause type
	 * @param field the field
	 * @param value the prefix; {@code null} adds nothing
	 * @return a copy of the clause with the condition
	 */
	public <T extends CcpQueryBooleanOperator> T prefix(CcpEntityField field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, CcpQueryConditionType.prefix);
		return operatorWithCondition;
	}

	/**
	 * Adds {@code {"match": {field: value}}} (full-text).
	 * @param <T> the concrete clause type
	 * @param field the field
	 * @param value the text; {@code null} adds nothing
	 * @return a copy of the clause with the condition
	 */
	public <T extends CcpQueryBooleanOperator> T match(CcpJsonFieldName field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, CcpQueryConditionType.match);
		return operatorWithCondition;
	}

	/**
	 * Adds {@code {"match_phrase": {field: value}}}.
	 * @param <T> the concrete clause type
	 * @param field the field
	 * @param value the phrase; {@code null} adds nothing
	 * @return a copy of the clause with the condition
	 */
	public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, CcpQueryConditionType.match_phrase);
		return operatorWithCondition;
	}

	/**
	 * Adds {@code {"match": {field: {"query": value, "boost": boost, "operator": operator}}}}; a blank operator is omitted.
	 * @param <T> the concrete clause type
	 * @param field the field
	 * @param value the text; {@code null} adds nothing
	 * @param boost the weight of the condition
	 * @param operator {@code and}/{@code or}, or blank
	 * @return a copy of the clause with the condition
	 */
	public <T extends CcpQueryBooleanOperator> T match(CcpEntityField field, Object value, double boost, String operator) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, CcpQueryConditionType.match, boost, operator);
		return operatorWithCondition;
	}

	/**
	 * Adds {@code {"match_phrase": {field: {"query": value, "boost": boost}}}}.
	 * @param <T> the concrete clause type
	 * @param field the field
	 * @param value the phrase; {@code null} adds nothing
	 * @param boost the weight of the condition
	 * @return a copy of the clause with the condition
	 */
	public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value, double boost) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, CcpQueryConditionType.match_phrase, boost, "");
		return operatorWithCondition;
	}

	/**
	 * Adds {@code {"exists": {"field": field}}}.
	 * @param <T> the concrete clause type
	 * @param field the field that must exist
	 * @return a copy of the clause with the condition
	 */
	public <T extends CcpQueryBooleanOperator> T exists(String field) {
		T operatorWithCondition = this.addCondition(JsonFieldNames.field.name(), field, CcpQueryConditionType.exists);
		return operatorWithCondition;
	}

	/**
	 * Adds {@code {conditionType: {field: value}}} to a copy of the clause.
	 * @param <T> the concrete clause type
	 * @param field the field
	 * @param value the value; {@code null} returns an unchanged copy
	 * @param conditionType the condition type
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, CcpQueryConditionType conditionType) {
		CcpQueryBooleanOperator clone = this.copy();
		boolean valueIsNull = value == null;
		if (valueIsNull) {
			T unchangedCopy = (T) clone;
			return unchangedCopy;
		}
		CcpFieldName fieldKey = new CcpFieldName(field);
		CcpJsonRepresentation conditionJson = CcpOtherConstants.EMPTY_JSON.put(fieldKey, value);
		Map<String, Object> map = conditionJson.getContent();
		CcpJsonRepresentation outerJson = CcpOtherConstants.EMPTY_JSON.put(conditionType, map);
		Map<String, Object> outerMap = outerJson.getContent();
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		T typedClone = (T) clone;
		return typedClone;
	}

	/**
	 * Adds {@code {conditionType: {field: {"query": value, "boost": boost, "operator": operator}}}} to a copy of the clause; a blank
	 * operator is omitted.
	 * @param <T> the concrete clause type
	 * @param field the field
	 * @param value the text; {@code null} returns an unchanged copy
	 * @param conditionType the condition type
	 * @param boost the weight of the condition
	 * @param operator the boolean operator, or blank
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, CcpQueryConditionType conditionType, double boost, String operator) {
		CcpQueryBooleanOperator clone = this.copy();
		boolean valueIsNull = value == null;
		if (valueIsNull) {
			T unchangedCopy = (T) clone;
			return unchangedCopy;
		}
		CcpJsonRepresentation queryJson = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.query, value);
		CcpJsonRepresentation conditionParameters = queryJson.put(JsonFieldNames.boost, boost);
		boolean hasOperator = operator != null;
		boolean hasNonBlankOperator = hasOperator && false == operator.trim().isEmpty();
		if (hasNonBlankOperator) {
			conditionParameters = conditionParameters.put(JsonFieldNames.operator, operator);
		}
		Map<String, Object> map = conditionParameters.getContent();
		CcpFieldName fieldKey = new CcpFieldName(field);
		CcpJsonRepresentation fieldJson = CcpOtherConstants.EMPTY_JSON.put(fieldKey, map);
		Map<String, Object> mapField = fieldJson.getContent();
		CcpJsonRepresentation outerJson = CcpOtherConstants.EMPTY_JSON.put(conditionType, mapField);
		Map<String, Object> outerMap = outerJson.getContent();
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		T typedClone = (T) clone;
		return typedClone;
	}

	/**
	 * Returns the conditions as a list (the JSON value of a clause).
	 * @return the conditions
	 */
	Object getValue() {
		return new ArrayList<>(this.items);
	}

	/**
	 * Adds a nested node (e.g. a bool or a range) as one more condition, {@code {child.name: child.value}}.
	 * @param <T> the concrete clause type
	 * @param child the nested node
	 * @return a copy of the clause with the condition
	 */
	@SuppressWarnings("unchecked")
	<T extends CcpQueryComponent> T addChild(CcpQueryComponent child) {
		CcpQueryBooleanOperator copy = this.copy();
		copy.items.addAll(this.items);
		Object childValue = child.getValue();
		CcpFieldName childKey = new CcpFieldName(child.name);
		CcpJsonRepresentation childJson = CcpOtherConstants.EMPTY_JSON.put(childKey, childValue);
		Map<String, Object> childContent = childJson.getContent();
		copy.items.add(childContent);
		T typedCopy = (T) copy;
		return typedCopy;
	}

	/**
	 * Copies the clause, its parent chain and its conditions.
	 * @param <T> the concrete clause type
	 * @return the copy
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T copy() {
		CcpQueryBooleanOperator instanceCopy = this.getInstanceCopy();
		instanceCopy.name = this.name;
		instanceCopy.parent = this.parent.copy();
		instanceCopy.items.addAll(this.items);
		T typedCopy = (T) instanceCopy;
		return typedCopy;
	}

	/**
	 * Starts a {@code range} condition inside this clause.
	 * @return the range node
	 */
	public CcpQueryRange startRange() {
		CcpQueryRange range = new CcpQueryRange(this);
		return range;
	}

	/**
	 * Tells whether the clause has at least one condition.
	 * @return {@code true} when there are conditions
	 */
	public boolean hasChildreen() {
		boolean itemsEmpty = this.items.isEmpty();
		boolean hasItems = false == itemsEmpty;
		return hasItems;
	}
}
