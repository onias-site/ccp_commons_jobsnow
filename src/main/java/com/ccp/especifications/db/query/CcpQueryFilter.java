package com.ccp.especifications.db.query;

public final class CcpQueryFilter extends CcpQueryComponent {
	CcpQueryFilter(CcpQueryComponent parent) {
		super(parent, "filter");
	}

	public CcpQueryBool startBool() {
		CcpQueryBool boolQuery = new CcpQueryBool(this);
		return boolQuery;
	}

	public CcpQueryBool endFilterAndBackToBool() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryFilter newInstance = new CcpQueryFilter(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}
}
