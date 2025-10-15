package com.ccp.especifications.db.query;

public final class CcpQuery  extends CcpQueryComponent {

	CcpQuery(CcpQueryComponent parent) {
		super(parent, "query");
	}

	public CcpQueryBool startBool() {
		return new CcpQueryBool(this);
	}

	public CcpQueryOptions endQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQuery(this.parent);
	}
	
}
