package com.ccp.especifications.db.query;

public final class CcpQuery extends CcpQueryComponent {
	CcpQuery(CcpQueryComponent parent) {
		super(parent, "query");
	}

	public CcpQueryBool startBool() {
		CcpQueryBool boolQuery = new CcpQueryBool(this);
		return boolQuery;
	}

	public CcpQueryOptions endQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQuery newInstance = new CcpQuery(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}
}
