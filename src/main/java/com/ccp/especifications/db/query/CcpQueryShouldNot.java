package com.ccp.especifications.db.query;

import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

public final class CcpQueryShouldNot extends CcpQueryBooleanOperator {
	CcpQueryShouldNot(CcpQueryComponent parent) {
		super(parent, "should_not");
	}

	@SuppressWarnings("unchecked")
	public CcpQueryShouldNot prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	public CcpQueryBool endShouldNotAndBackToBool() {
		CcpQueryComponent copy = this.parent.copy();
		CcpQueryBool addChild = copy.addChild(this);
		return addChild;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryShouldNot ccpQueryShouldNot = new CcpQueryShouldNot(this.parent);
		T t = (T) ccpQueryShouldNot;
		return t;
	}

	@SuppressWarnings("unchecked")
	public CcpQueryShouldNot matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	public CcpQueryShouldNot term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	@SuppressWarnings("unchecked")
	public CcpQueryShouldNot exists(String field) {
		return super.exists(field);
	}

	public CcpQueryBool startBool() {
		CcpQueryBool ccpQueryBool = new CcpQueryBool(this);
		return ccpQueryBool;
	}
}
