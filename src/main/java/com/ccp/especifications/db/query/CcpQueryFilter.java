package com.ccp.especifications.db.query;

public final class CcpQueryFilter extends CcpQueryComponent {
	CcpQueryFilter(CcpQueryComponent parent) {
		super(parent, "filter");
	}

	public CcpQueryBool startBool() {
		CcpQueryBool ccpQueryBool = new CcpQueryBool(this);
		return ccpQueryBool;
	}

	public CcpQueryBool endFilterAndBackToBool() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryFilter ccpQueryFilter = new CcpQueryFilter(this.parent);
		T t = (T) ccpQueryFilter;
		return t;
	}
}
