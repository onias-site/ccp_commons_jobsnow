package com.ccp.especifications.db.query;

public final class CcpQueryBool extends CcpQueryComponent {
	CcpQueryBool(CcpQueryComponent parent) {
		super(parent, "bool");
	}

	public CcpQueryFilter startFilter() {
		CcpQueryFilter filter = new CcpQueryFilter(this);
		return filter;
	}

	public CcpQueryMust startMust() {
		CcpQueryMust must = new CcpQueryMust(this);
		return must;
	}

	public CcpQueryShould startShould(int minimumShouldMatch) {
		CcpQueryShould should = new CcpQueryShould(this);
		CcpQueryShould shouldWithMinimumMatch = should.setMinimumShouldMatch(minimumShouldMatch);
		return shouldWithMinimumMatch;
	}

	public CcpQueryMustNot startMustNot() {
		CcpQueryMustNot mustNot = new CcpQueryMustNot(this);
		return mustNot;
	}

	public CcpQueryShouldNot startShouldNot() {
		CcpQueryShouldNot shouldNot = new CcpQueryShouldNot(this);
		return shouldNot;
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
		CcpQueryBool newInstance = new CcpQueryBool(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}
}
