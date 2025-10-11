package com.ccp.especifications.db.query;

import com.ccp.especifications.db.utils.CcpEntityField;

public final class CcpDbQueryMustNot extends CcpDbQueryBooleanOperator{

	 CcpDbQueryMustNot(CcpDbQueryComponent parent) {
		super(parent, "must_not");
	}
	
	public CcpDbQueryBool endMustNotAndBackToBool() {
		return this.parent.addChild(this);
	}
	
	
	@SuppressWarnings("unchecked")
	public CcpDbQueryMustNot prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	
	
	@SuppressWarnings("unchecked")
	public CcpDbQueryMustNot matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	public CcpDbQueryMustNot term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQueryMustNot(this.parent);
	}
	
	@SuppressWarnings("unchecked")
	public CcpDbQueryMustNot exists(String field) {
		return super.exists(field);
	}
	public CcpDbQueryBool startBool() {
		return new CcpDbQueryBool(this);
	}


}
