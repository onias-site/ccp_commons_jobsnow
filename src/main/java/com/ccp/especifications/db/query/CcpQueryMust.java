package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

public final class CcpQueryMust extends CcpQueryBooleanOperator{

	CcpQueryMust(CcpQueryComponent parent) {
		super(parent, "must");
	}
	
	public CcpQueryBool endMustAndBackToBool() {
		return this.parent.addChild(this);
	}
	
	@SuppressWarnings("unchecked")
	public CcpQueryMust matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	
	@SuppressWarnings("unchecked")
	public CcpQueryMust prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}
	
	@SuppressWarnings("unchecked")
	public CcpQueryMust term(CcpJsonFieldName field, Object value) {
		return super.term(field, value);
	}

	@SuppressWarnings("unchecked")
	public CcpQueryMust terms(CcpJsonFieldName field, Object value) {
		return super.terms(field, value);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryMust(this.parent);
	}
	

	@SuppressWarnings("unchecked")
	public CcpQueryMust exists(String field) {
		return super.exists(field);
	}
	public CcpQueryBool startBool() {
		return new CcpQueryBool(this);
	}

	public CcpQueryMust match(CcpEntityField field, Object value) {
		return super.match(field, value);
	}
}
