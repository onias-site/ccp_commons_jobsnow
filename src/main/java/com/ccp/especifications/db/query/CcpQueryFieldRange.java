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
		CcpQueryFieldRange newInstance = new CcpQueryFieldRange(this.parent, this.name);
		return newInstance;
	}

	private CcpQueryFieldRange putOperator(CcpJsonFieldName operatorName, Object value) {
		CcpQueryFieldRange copy = this.copy();
		copy.json = copy.json.put(operatorName, value);
		return copy;
	}

	public CcpQueryFieldRange lessThan(Object value) {
		CcpQueryFieldRange fieldRange = this.putOperator(JsonFieldNames.lt, value);
		return fieldRange;
	}

	public CcpQueryFieldRange lessThanEquals(Object value) {
		CcpQueryFieldRange fieldRange = this.putOperator(JsonFieldNames.lte, value);
		return fieldRange;
	}

	public CcpQueryFieldRange greaterThan(Object value) {
		CcpQueryFieldRange fieldRange = this.putOperator(JsonFieldNames.gt, value);
		return fieldRange;
	}

	public CcpQueryFieldRange greaterThanEquals(Object value) {
		CcpQueryFieldRange fieldRange = this.putOperator(JsonFieldNames.gte, value);
		return fieldRange;
	}

	public CcpQueryRange endFieldRangeAndBackToRange() {
		return this.parent.addChild(this);
	}
}
