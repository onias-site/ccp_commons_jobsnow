package com.ccp.especifications.db.query;

import com.ccp.especifications.db.utils.CcpEntityField;

public final class CcpDbQueryShouldNot  extends CcpDbQueryBooleanOperator {

	CcpDbQueryShouldNot(CcpDbQueryComponent parent) {
		super(parent, "should_not");
	}
	
	@SuppressWarnings("unchecked")
	public CcpDbQueryShouldNot prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	public CcpDbQueryBool endShouldNotAndBackToBool() {
		CcpDbQueryComponent copy = this.parent.copy();
		CcpDbQueryBool addChild = copy.addChild(this);
		return addChild;
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQueryShouldNot(this.parent);
	}
	
	
	@SuppressWarnings("unchecked")
	public CcpDbQueryShouldNot matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	
	@SuppressWarnings("unchecked")
	public CcpDbQueryShouldNot term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}
	
	@SuppressWarnings("unchecked")
	public CcpDbQueryShouldNot exists(String field) {
		return super.exists(field);
	}

	public CcpDbQueryBool startBool() {
		return new CcpDbQueryBool(this);
	}

}
