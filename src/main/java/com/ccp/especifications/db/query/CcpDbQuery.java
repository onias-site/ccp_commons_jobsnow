package com.ccp.especifications.db.query;

public final class CcpDbQuery  extends CcpDbQueryComponent {

	CcpDbQuery(CcpDbQueryComponent parent) {
		super(parent, "query");
	}

	public CcpDbQueryBool startBool() {
		return new CcpDbQueryBool(this);
	}

	public CcpDbQueryOptions endQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQuery(this.parent);
	}
	
}
