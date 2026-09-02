package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

public final class CcpQuerySimplifiedQuery extends CcpQueryBooleanOperator {
	CcpQuerySimplifiedQuery(CcpQueryComponent parent) {
		super(parent, "query");
	}

	public CcpQuerySimplifiedQuery terms(CcpEntityField field, Object value) {
		return super.terms(field, value);
	}

	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	public CcpQueryOptions endSimplifiedQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQuerySimplifiedQuery ccpQuerySimplifiedQuery = new CcpQuerySimplifiedQuery(this.parent);
		T t = (T) ccpQuerySimplifiedQuery;
		return t;
	}

	Object getValue() {
		return this.json.content;
	}

	@SuppressWarnings("unchecked")
	protected CcpQuerySimplifiedQuery copy() {
		CcpQuerySimplifiedQuery instanceCopy = this.getInstanceCopy();
		instanceCopy.name = this.name;
		instanceCopy.parent = this.parent.copy();
		instanceCopy.json = this.json.copy();
		return instanceCopy;
	}

	@SuppressWarnings("unchecked")
	CcpQuerySimplifiedQuery addChild(CcpQueryComponent child) {
		CcpQuerySimplifiedQuery instanceCopy = this.copy();
		Object value = child.getValue();
		CcpFieldName ccpFieldName = new CcpFieldName(child.name);
		instanceCopy.json = instanceCopy.json.put(ccpFieldName, value);
		return instanceCopy;
	}

	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	public CcpQuerySimplifiedQuery term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery match(CcpJsonFieldName field, Object value) {
		return super.match(field, value);
	}

	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery exists(String field) {
		return super.exists(field);
	}

	@SuppressWarnings("unchecked")
	protected CcpQuerySimplifiedQuery addCondition(String field, Object value, String key) {
		CcpFieldName ccpFieldName2 = new CcpFieldName(field);
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName2, value);
		Map<String, Object> map = put.getContent();
		CcpFieldName ccpFieldName3 = new CcpFieldName(key);
		CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName3, map);
		Map<String, Object> outerMap = put2.getContent();
		CcpQuerySimplifiedQuery clone = this.copy();
		clone.json = new CcpJsonRepresentation(outerMap);
		return clone;
	}

	public boolean hasChildreen() {
		boolean contentEmpty = this.json.content.isEmpty();
		boolean valorIgual = false == contentEmpty;
		return valorIgual;
	}
}
