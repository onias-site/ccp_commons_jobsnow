package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;

public final class CcpQueryMust extends CcpQueryBooleanOperator {
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
		CcpQueryMust ccpQueryMust = new CcpQueryMust(this.parent);
		T t = (T) ccpQueryMust;
		return t;
	}

	@SuppressWarnings("unchecked")
	public CcpQueryMust exists(String field) {
		return super.exists(field);
	}

	public CcpQueryBool startBool() {
		CcpQueryBool ccpQueryBool = new CcpQueryBool(this);
		return ccpQueryBool;
	}

	public CcpQueryMust match(CcpEntityField field, Object value) {
		return super.match(field, value);
	}
}
