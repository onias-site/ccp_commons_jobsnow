package com.ccp.especifications.db.query;

import com.ccp.especifications.db.utils.CcpEntityField;

public final class CcpDbQueryMust extends CcpDbQueryBooleanOperator{

	CcpDbQueryMust(CcpDbQueryComponent parent) {
		super(parent, "must");
	}
	
	public CcpDbQueryBool endMustAndBackToBool() {
		return this.parent.addChild(this);
	}
	
	@SuppressWarnings("unchecked")
	public CcpDbQueryMust matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	
	@SuppressWarnings("unchecked")
	public CcpDbQueryMust prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	
	@SuppressWarnings("unchecked")
	public CcpDbQueryMust term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	@SuppressWarnings("unchecked")
	public CcpDbQueryMust terms(CcpEntityField field, Object value) {
		return super.terms(field, value);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQueryMust(this.parent);
	}
	

	@SuppressWarnings("unchecked")
	public CcpDbQueryMust exists(String field) {
		return super.exists(field);
	}
	public CcpDbQueryBool startBool() {
		return new CcpDbQueryBool(this);
	}

	@SuppressWarnings("unchecked")
	
	public CcpDbQueryMust match(CcpEntityField field, Object value) {
		return super.match(field, value);
	}
}
