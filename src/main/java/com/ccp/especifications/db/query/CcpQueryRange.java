package com.ccp.especifications.db.query;

public class CcpQueryRange extends CcpQueryComponent {
	CcpQueryRange(CcpQueryComponent parent) {
		super(parent, "range");
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryRange ccpQueryRange = new CcpQueryRange(this.parent);
		T t = (T) ccpQueryRange;
		return t;
	}

	public CcpQueryFieldRange startFieldRange(String fieldName) {
		CcpQueryFieldRange ccpQueryFieldRange = new CcpQueryFieldRange(this, fieldName);
		return ccpQueryFieldRange;
	}

	public CcpQuerySimplifiedQuery endRangeAndBackToSimplifiedQuery() {
		return this.parent.addChild(this);
	}

	public CcpQueryShould endRangeAndBackToShould() {
		return this.parent.addChild(this);
	}

	public CcpQueryMust endRangeAndBackToMust() {
		return this.parent.addChild(this);
	}

	public CcpQueryShouldNot endRangeAndBackToShouldNot() {
		return this.parent.addChild(this);
	}

	public CcpQueryMustNot endRangeAndBackToMustNot() {
		return this.parent.addChild(this);
	}
}
