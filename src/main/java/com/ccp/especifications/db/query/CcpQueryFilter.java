package com.ccp.especifications.db.query;

public final class CcpQueryFilter  extends CcpQueryComponent {
	CcpQueryFilter(CcpQueryComponent parent) {
		super(parent, "filter");
	}

	 public CcpQueryBool startBool() {
		return new CcpQueryBool(this);
	}
	
	public CcpQueryBool endFilterAndBackToBool() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryFilter(this.parent);
	}
}