package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonFieldName;

public class CcpQueryFieldRange extends CcpQueryComponent {
	enum JsonFieldNames implements CcpJsonFieldName {
		lt, lte, gt, gte
	}

	CcpQueryFieldRange(CcpQueryComponent parent, String name) {
		super(parent, name);
	}

	@SuppressWarnings("unchecked")
	protected CcpQueryFieldRange getInstanceCopy() {
		CcpQueryFieldRange ccpQueryFieldRange = new CcpQueryFieldRange(this.parent, this.name);
		return ccpQueryFieldRange;
	}

	private CcpQueryFieldRange putOperator(CcpJsonFieldName operatorName, Object value) {
		CcpQueryFieldRange copy = this.copy();
		copy.json = copy.json.put(operatorName, value);
		return copy;
	}

	public CcpQueryFieldRange lessThan(Object value) {
		CcpQueryFieldRange putOperator = this.putOperator(JsonFieldNames.lt, value);
		return putOperator;
	}

	public CcpQueryFieldRange lessThanEquals(Object value) {
		CcpQueryFieldRange putOperator2 = this.putOperator(JsonFieldNames.lte, value);
		return putOperator2;
	}

	public CcpQueryFieldRange greaterThan(Object value) {
		CcpQueryFieldRange putOperator3 = this.putOperator(JsonFieldNames.gt, value);
		return putOperator3;
	}

	public CcpQueryFieldRange greaterThanEquals(Object value) {
		CcpQueryFieldRange putOperator4 = this.putOperator(JsonFieldNames.gte, value);
		return putOperator4;
	}

	public CcpQueryRange endFieldRangeAndBackToRange() {
		return this.parent.addChild(this);
	}
}
