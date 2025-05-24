package com.ccp.especifications.db.query;

public class CcpDbQueryFieldRange extends CcpDbQueryComponent{

	CcpDbQueryFieldRange(CcpDbQueryComponent parent, String name) {
		super(parent, name);
	}

	@SuppressWarnings("unchecked")
	protected CcpDbQueryFieldRange getInstanceCopy() {
		return new CcpDbQueryFieldRange(this.parent, this.name);
	}

	
	private CcpDbQueryFieldRange putOperator(String operatorName, Object value) {
		CcpDbQueryFieldRange copy = this.copy();
		copy.json = copy.json.put(operatorName, value);
		return copy;
	}
	
	public CcpDbQueryFieldRange lessThan(Object value) {
		return this.putOperator("lt", value);
	}

	public CcpDbQueryFieldRange lessThanEquals(Object value) {
		return this.putOperator("lte", value);
	}

	public CcpDbQueryFieldRange greaterThan(Object value) {
		return this.putOperator("gt", value);
	}

	public CcpDbQueryFieldRange greaterThanEquals(Object value) {
		return this.putOperator("gte", value);
	}
	
	public CcpDbQueryRange endFieldRangeAndBackToRange() {
		return this.parent.addChild(this);
	}

}
