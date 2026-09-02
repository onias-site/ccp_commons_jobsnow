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
		T addCondition = this.addCondition(fieldName, value, "term");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T terms(CcpJsonFieldName field, Object value) {
		String fieldName2 = field.name();
		T addCondition = this.addCondition(fieldName2, value, "terms");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T prefix(CcpEntityField field, Object value) {
		String fieldName3 = field.name();
		T addCondition = this.addCondition(fieldName3, value, "prefix");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T match(CcpJsonFieldName field, Object value) {
		String fieldName4 = field.name();
		T addCondition = this.addCondition(fieldName4, value, "match");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value) {
		String fieldName5 = field.name();
		T addCondition = this.addCondition(fieldName5, value, "match_phrase");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T match(CcpEntityField field, Object value, double boost, String operator) {
		String fieldName6 = field.name();
		T addCondition = this.addCondition(fieldName6, value, "match", boost, operator);
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value, double boost) {
		String fieldName7 = field.name();
		T addCondition = this.addCondition(fieldName7, value, "match_phrase", boost, "");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T exists(String field) {
		T addCondition = this.addCondition("field", field, "exists");
		return addCondition;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, String key) {
		CcpQueryBooleanOperator clone = this.copy();
		boolean valueIgual = value == null;
		if (valueIgual) {
			T t = (T) clone;
			return t;
		}
		CcpFieldName ccpFieldName = new CcpFieldName(field);
		CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName, value);
		Map<String, Object> map = put2.getContent();
		CcpFieldName ccpFieldName2 = new CcpFieldName(key);
		CcpJsonRepresentation put3 = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName2, map);
		Map<String, Object> outerMap = put3.getContent();
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		T t2 = (T) clone;
		return t2;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, String key, double boost, String operator) {
		CcpQueryBooleanOperator clone = this.copy();
		boolean valueIgual2 = value == null;
		if (valueIgual2) {
			T t3 = (T) clone;
			return t3;
		}
		CcpJsonRepresentation put4 = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.query, value);
		CcpJsonRepresentation put = put4.put(JsonFieldNames.boost, boost);
		boolean operatorDiferente = operator != null;
		boolean operatorDiferenteE = operatorDiferente && false == operator.trim().isEmpty();
		if (operatorDiferenteE) {
			put = put.put(JsonFieldNames.operator, operator);
		}
		Map<String, Object> map = put.getContent();
		CcpFieldName ccpFieldName3 = new CcpFieldName(field);
		CcpJsonRepresentation put5 = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName3, map);
		Map<String, Object> mapField = put5.getContent();
		CcpFieldName ccpFieldName4 = new CcpFieldName(key);
		CcpJsonRepresentation put6 = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName4, mapField);
		put6.getContent();
		CcpFieldName ccpFieldName5 = new CcpFieldName(key);
		CcpJsonRepresentation put7 = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName5, mapField);
		Map<String, Object> outerMap = put7.getContent();
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		T t4 = (T) clone;
		return t4;
	}

	Object getValue() {
		return new ArrayList<>(this.items);
	}

	@SuppressWarnings("unchecked")
	<T extends CcpQueryComponent> T addChild(CcpQueryComponent child) {
		CcpQueryBooleanOperator copy = this.copy();
		copy.items.addAll(this.items);
		Object childValue = child.getValue();
		CcpFieldName ccpFieldName6 = new CcpFieldName(child.name);
		CcpJsonRepresentation put8 = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName6, childValue);
		Map<String, Object> childContent = put8.getContent();
		copy.items.add(childContent);
		T t5 = (T) copy;
		return t5;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T copy() {
		CcpQueryBooleanOperator instanceCopy = this.getInstanceCopy();
		instanceCopy.name = this.name;
		instanceCopy.parent = this.parent.copy();
		instanceCopy.items.addAll(this.items);
		T t6 = (T) instanceCopy;
		return t6;
	}

	public CcpQueryRange startRange() {
		CcpQueryRange ccpQueryRange = new CcpQueryRange(this);
		return ccpQueryRange;
	}

	public boolean hasChildreen() {
		boolean itemsEmpty = this.items.isEmpty();
		boolean valorIgual = false == itemsEmpty;
		return valorIgual;
	}
}
