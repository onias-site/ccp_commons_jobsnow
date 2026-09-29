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

public abstract class CcpQueryBooleanOperator extends CcpQueryComponent {
	enum JsonFieldNames implements CcpJsonFieldName {
		operator, boost, query
	}

	protected Set<Object> items = new LinkedHashSet<>();

	CcpQueryBooleanOperator(CcpQueryComponent parent, String name) {
		super(parent, name);
	}

	public <T extends CcpQueryBooleanOperator> T term(CcpJsonFieldName field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, "term");
		return operatorWithCondition;
	}

	public <T extends CcpQueryBooleanOperator> T terms(CcpJsonFieldName field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, "terms");
		return operatorWithCondition;
	}

	public <T extends CcpQueryBooleanOperator> T prefix(CcpEntityField field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, "prefix");
		return operatorWithCondition;
	}

	public <T extends CcpQueryBooleanOperator> T match(CcpJsonFieldName field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, "match");
		return operatorWithCondition;
	}

	public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, "match_phrase");
		return operatorWithCondition;
	}

	public <T extends CcpQueryBooleanOperator> T match(CcpEntityField field, Object value, double boost, String operator) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, "match", boost, operator);
		return operatorWithCondition;
	}

	public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value, double boost) {
		String fieldName = field.name();
		T operatorWithCondition = this.addCondition(fieldName, value, "match_phrase", boost, "");
		return operatorWithCondition;
	}

	public <T extends CcpQueryBooleanOperator> T exists(String field) {
		T operatorWithCondition = this.addCondition("field", field, "exists");
		return operatorWithCondition;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, String key) {
		CcpQueryBooleanOperator clone = this.copy();
		boolean valueIsNull = value == null;
		if (valueIsNull) {
			T unchangedCopy = (T) clone;
			return unchangedCopy;
		}
		CcpFieldName fieldKey = new CcpFieldName(field);
		CcpJsonRepresentation conditionJson = CcpOtherConstants.EMPTY_JSON.put(fieldKey, value);
		Map<String, Object> map = conditionJson.getContent();
		CcpFieldName conditionTypeKey = new CcpFieldName(key);
		CcpJsonRepresentation outerJson = CcpOtherConstants.EMPTY_JSON.put(conditionTypeKey, map);
		Map<String, Object> outerMap = outerJson.getContent();
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		T typedClone = (T) clone;
		return typedClone;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, String key, double boost, String operator) {
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
		CcpFieldName conditionTypeKey = new CcpFieldName(key);
		CcpJsonRepresentation conditionJson = CcpOtherConstants.EMPTY_JSON.put(conditionTypeKey, mapField);
		conditionJson.getContent();
		CcpFieldName outerConditionTypeKey = new CcpFieldName(key);
		CcpJsonRepresentation outerJson = CcpOtherConstants.EMPTY_JSON.put(outerConditionTypeKey, mapField);
		Map<String, Object> outerMap = outerJson.getContent();
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		T typedClone = (T) clone;
		return typedClone;
	}

	Object getValue() {
		return new ArrayList<>(this.items);
	}

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

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T copy() {
		CcpQueryBooleanOperator instanceCopy = this.getInstanceCopy();
		instanceCopy.name = this.name;
		instanceCopy.parent = this.parent.copy();
		instanceCopy.items.addAll(this.items);
		T typedCopy = (T) instanceCopy;
		return typedCopy;
	}

	public CcpQueryRange startRange() {
		CcpQueryRange range = new CcpQueryRange(this);
		return range;
	}

	public boolean hasChildreen() {
		boolean itemsEmpty = this.items.isEmpty();
		boolean hasItems = false == itemsEmpty;
		return hasItems;
	}
}
