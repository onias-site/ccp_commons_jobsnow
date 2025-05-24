package com.ccp.especifications.db.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntityField;

abstract class CcpDbQueryBooleanOperator extends CcpDbQueryComponent{

	protected Set<Object> items = new LinkedHashSet<>();

	CcpDbQueryBooleanOperator(CcpDbQueryComponent parent, String name) {
		super(parent, name);
	}

	public <T extends CcpDbQueryBooleanOperator> T term(CcpEntityField field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "term");
		return addCondition;
	}

	public <T extends CcpDbQueryBooleanOperator> T terms(CcpEntityField field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "terms");
		return addCondition;
	}

	public <T extends CcpDbQueryBooleanOperator> T prefix(CcpEntityField field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "prefix");
		return addCondition;
	}

	public <T extends CcpDbQueryBooleanOperator> T match(CcpEntityField field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "match");
		return addCondition;
	}

	public <T extends CcpDbQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value) {
		T addCondition = this.addCondition(field.name(), value, "match_phrase");
		return addCondition;
	}

	public <T extends CcpDbQueryBooleanOperator> T match(CcpEntityField field, Object value, double boost, String operator) {
		T addCondition = this.addCondition(field.name(), value, "match", boost, operator);
		return addCondition;
	}

	public <T extends CcpDbQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value, double boost) {
		T addCondition = this.addCondition(field.name(), value, "match_phrase", boost, "");
		return addCondition;
	}

	public <T extends CcpDbQueryBooleanOperator> T exists(String field) {
		
		
		T addCondition = this.addCondition("field", field, "exists");
		return addCondition;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryBooleanOperator> T addCondition(String field, Object value, String key) {
		CcpDbQueryBooleanOperator clone = this.copy();
		if(value == null) {
			return (T)clone;
		}
		
		Map<String, Object> map = CcpOtherConstants.EMPTY_JSON.put(field, value).getContent();
		Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.put(key, map).getContent();
		
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		return (T)clone;
	}

	
	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryBooleanOperator> T addCondition(String field, Object value, String key, double boost, String operator) {
		CcpDbQueryBooleanOperator clone = this.copy();
		if(value == null) {
			return (T)clone;
		}
		
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put("query", value).put("boost", boost);
		if(operator != null && operator.trim().isEmpty() == false) {
			put = put.put("operator", operator);
		}
		Map<String, Object> map = put.getContent();
		Map<String, Object> mapField = CcpOtherConstants.EMPTY_JSON.put(field, map).getContent();	
		CcpOtherConstants.EMPTY_JSON.put(key, mapField).getContent();
		Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.put(key, mapField).getContent();
		
		clone.items.addAll(this.items);
		clone.items.add(outerMap);
		return (T)clone;
	}

	
	Object getValue() {
		return new ArrayList<>(this.items);
	}

	@SuppressWarnings("unchecked")
	<T extends CcpDbQueryComponent> T addChild(CcpDbQueryComponent child) {
		
		CcpDbQueryBooleanOperator copy = this.copy();
		copy.items.addAll(this.items);
		Object childValue = child.getValue();
		Map<String, Object> childContent = CcpOtherConstants.EMPTY_JSON.put(child.name, childValue).getContent();
		copy.items.add(childContent);
		return (T)copy;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T copy() {
		CcpDbQueryBooleanOperator instanceCopy = this.getInstanceCopy();

		instanceCopy.name = this.name;
		
		instanceCopy.parent = this.parent.copy();
		
		instanceCopy.items.addAll(this.items);
		
		return (T)instanceCopy;
	}

	public CcpDbQueryRange startRange() {
		return new CcpDbQueryRange(this);
	}

	
	
	public boolean hasChildreen() {
		return this.items.isEmpty() == false;
	}
}
