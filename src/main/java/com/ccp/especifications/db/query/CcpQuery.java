package com.ccp.especifications.db.query;

public final class CcpQuery extends CcpQueryComponent {
	CcpQuery(CcpQueryComponent parent) {
		super(parent, "query");
	}

	public CcpQueryBool startBool() {
		CcpQueryBool ccpQueryBool = new CcpQueryBool(this);
		return ccpQueryBool;
	}

	public CcpQueryOptions endQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQuery ccpQuery = new CcpQuery(this.parent);
		T t = (T) ccpQuery;
		return t;
	}
}
