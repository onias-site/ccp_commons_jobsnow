package com.ccp.especifications.db.query;

import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

public final class CcpQueryMustNot extends CcpQueryBooleanOperator {
	CcpQueryMustNot(CcpQueryComponent parent) {
		super(parent, "must_not");
	}

	public CcpQueryBool endMustNotAndBackToBool() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	public CcpQueryMustNot prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	@SuppressWarnings("unchecked")
	public CcpQueryMustNot matchPhrase(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	public CcpQueryMustNot term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryMustNot ccpQueryMustNot = new CcpQueryMustNot(this.parent);
		T t = (T) ccpQueryMustNot;
		return t;
	}

	@SuppressWarnings("unchecked")
	public CcpQueryMustNot exists(String field) {
		return super.exists(field);
	}

	public CcpQueryBool startBool() {
		CcpQueryBool ccpQueryBool = new CcpQueryBool(this);
		return ccpQueryBool;
	}
}
