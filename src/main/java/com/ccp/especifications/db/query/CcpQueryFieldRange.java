package com.ccp.especifications.db.query;

public class CcpQueryFieldRange extends CcpQueryComponent{

	CcpQueryFieldRange(CcpQueryComponent parent, String name) {
		super(parent, name);
	}

	@SuppressWarnings("unchecked")
	protected CcpQueryFieldRange getInstanceCopy() {
		return new CcpQueryFieldRange(this.parent, this.name);
	}

	
	private CcpQueryFieldRange putOperator(String operatorName, Object value) {
		CcpQueryFieldRange copy = this.copy();
		copy.json = copy.json.getDynamicVersion().put(operatorName, value);
		return copy;
	}
	
	public CcpQueryFieldRange lessThan(Object value) {
		return this.putOperator("lt", value);
	}

	public CcpQueryFieldRange lessThanEquals(Object value) {
		return this.putOperator("lte", value);
	}

	public CcpQueryFieldRange greaterThan(Object value) {
		return this.putOperator("gt", value);
	}

	public CcpQueryFieldRange greaterThanEquals(Object value) {
		return this.putOperator("gte", value);
	}
	
	public CcpQueryRange endFieldRangeAndBackToRange() {
		return this.parent.addChild(this);
	}

}
