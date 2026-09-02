package com.ccp.especifications.db.query;

public final class CcpQueryBool extends CcpQueryComponent {
	CcpQueryBool(CcpQueryComponent parent) {
		super(parent, "bool");
	}

	public CcpQueryFilter startFilter() {
		CcpQueryFilter ccpQueryFilter = new CcpQueryFilter(this);
		return ccpQueryFilter;
	}

	public CcpQueryMust startMust() {
		CcpQueryMust ccpQueryMust = new CcpQueryMust(this);
		return ccpQueryMust;
	}

	public CcpQueryShould startShould(int minimumShouldMatch) {
		CcpQueryShould ccpQueryShould = new CcpQueryShould(this);
		CcpQueryShould setMinimumShouldMatch = ccpQueryShould.setMinimumShouldMatch(minimumShouldMatch);
		return setMinimumShouldMatch;
	}

	public CcpQueryMustNot startMustNot() {
		CcpQueryMustNot ccpQueryMustNot = new CcpQueryMustNot(this);
		return ccpQueryMustNot;
	}

	public CcpQueryShouldNot startShouldNot() {
		CcpQueryShouldNot ccpQueryShouldNot = new CcpQueryShouldNot(this);
		return ccpQueryShouldNot;
	}

	public CcpQueryShould endBoolAndBackToShould() {
		return this.parent.addChild(this);
	}

	public CcpQueryMust endBoolAndBackToMust() {
		return this.parent.addChild(this);
	}

	public CcpQueryShouldNot endBoolAndBackToShouldNot() {
		return this.parent.addChild(this);
	}

	public CcpQueryMustNot endBoolAndBackToMustNot() {
		return this.parent.addChild(this);
	}

	public CcpQueryFilter endBoolAndBackToFilter() {
		return this.parent.addChild(this);
	}

	public CcpQuery endBoolAndBackToQuery() {
		return this.parent.addChild(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryBool ccpQueryBool = new CcpQueryBool(this.parent);
		T t = (T) ccpQueryBool;
		return t;
	}
}
