package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpFieldName;

public class CcpQueryFieldRange extends CcpQueryComponent {
	CcpQueryFieldRange(CcpQueryComponent parent, String name) {
		super(parent, name);
	}

	@SuppressWarnings("unchecked")
	protected CcpQueryFieldRange getInstanceCopy() {
		CcpQueryFieldRange ccpQueryFieldRange = new CcpQueryFieldRange(this.parent, this.name);
		return ccpQueryFieldRange;
	}

	private CcpQueryFieldRange putOperator(String operatorName, Object value) {
		CcpQueryFieldRange copy = this.copy();
		CcpFieldName ccpFieldName = new CcpFieldName(operatorName);
		copy.json = copy.json.put(ccpFieldName, value);
		return copy;
	}

	public CcpQueryFieldRange lessThan(Object value) {
		CcpQueryFieldRange putOperator = this.putOperator("lt", value);
		return putOperator;
	}

	public CcpQueryFieldRange lessThanEquals(Object value) {
		CcpQueryFieldRange putOperator2 = this.putOperator("lte", value);
		return putOperator2;
	}

	public CcpQueryFieldRange greaterThan(Object value) {
		CcpQueryFieldRange putOperator3 = this.putOperator("gt", value);
		return putOperator3;
	}

	public CcpQueryFieldRange greaterThanEquals(Object value) {
		CcpQueryFieldRange putOperator4 = this.putOperator("gte", value);
		return putOperator4;
	}

	public CcpQueryRange endFieldRangeAndBackToRange() {
		return this.parent.addChild(this);
	}
}
