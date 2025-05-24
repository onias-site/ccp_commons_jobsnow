package com.ccp.especifications.db.query;

public final class CcpDbQueryFilter  extends CcpDbQueryComponent {
	CcpDbQueryFilter(CcpDbQueryComponent parent) {
		super(parent, "filter");
	}

	 public CcpDbQueryBool startBool() {
		return new CcpDbQueryBool(this);
	}
	
	public CcpDbQueryBool endFilterAndBackToBool() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQueryFilter(this.parent);
	}
}