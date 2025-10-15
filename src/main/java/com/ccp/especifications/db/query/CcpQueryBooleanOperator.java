package com.ccp.especifications.db.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

abstract class CcpQueryBooleanOperator extends CcpQueryComponent{
	enum JsonFieldNames implements CcpJsonFieldName{
		operator, boost, query
	}

	protected Set<Object> items = new LinkedHashSet<>();

	CcpQueryBooleanOperator(CcpQueryComponent parent, String name) {
		super(parent, name);
	}

	public <T extends CcpQueryBooleanOperator> T term(CcpJsonFieldName field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "term");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T terms(CcpJsonFieldName field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "terms");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T prefix(CcpEntityField field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "prefix");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T match(CcpJsonFieldName field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "match");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "match_phrase");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T match(CcpEntityField field, Object value, double boost, String operator) {
		T addCondition = this.addCondition(field.name(), value, "match", boost, operator);
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value, double boost) {
		T addCondition = this.addCondition(field.name(), value, "match_phrase", boost, "");
		return addCondition;
	}

	public <T extends CcpQueryBooleanOperator> T exists(String field) {
		
		
		T addCondition = this.addCondition("field", field, "exists");
		return addCondition;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, String key) {
		CcpQueryBooleanOperator clone = this.copy();
		if(value == null) {
			return (T)clone;
		}
		
		Map<String, Object> map = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(field, value).getContent();
		Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(key, map).getContent();
		
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		return (T)clone;
	}

	
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, String key, double boost, String operator) {
		CcpQueryBooleanOperator clone = this.copy();
		if(value == null) {
			return (T)clone;
		}
		
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.query, value).put(JsonFieldNames.boost, boost);
		if(operator != null && false == operator.trim().isEmpty()) {
			put = put.put(JsonFieldNames.operator, operator);
		}
		Map<String, Object> map = put.getContent();
		Map<String, Object> mapField = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(field, map).getContent();	
		CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(key, mapField).getContent();
		Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(key, mapField).getContent();
		
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		return (T)clone;
	}

	
	Object getValue() {
		return new ArrayList<>(this.items);
	}

	@SuppressWarnings("unchecked")
	<T extends CcpQueryComponent> T addChild(CcpQueryComponent child) {
		
		CcpQueryBooleanOperator copy = this.copy();
		copy.items.addAll(this.items);
		Object childValue = child.getValue();
		Map<String, Object> childContent = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(child.name, childValue).getContent();
		copy.items.add(childContent);
		return (T)copy;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T copy() {
		CcpQueryBooleanOperator instanceCopy = this.getInstanceCopy();

		instanceCopy.name = this.name;
		
		instanceCopy.parent = this.parent.copy();
		
		instanceCopy.items.addAll(this.items);
		
		return (T)instanceCopy;
	}

	public CcpQueryRange startRange() {
		return new CcpQueryRange(this);
	}

	
	
	public boolean hasChildreen() {
		return false == this.items.isEmpty();
	}
}
