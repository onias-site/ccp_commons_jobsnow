package com.ccp.especifications.db.query;

public class CcpQueryRange extends CcpQueryComponent {
	CcpQueryRange(CcpQueryComponent parent) {
		super(parent, "range");
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryRange newInstance = new CcpQueryRange(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	public CcpQueryFieldRange startFieldRange(String fieldName) {
		CcpQueryFieldRange fieldRange = new CcpQueryFieldRange(this, fieldName);
		return fieldRange;
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
