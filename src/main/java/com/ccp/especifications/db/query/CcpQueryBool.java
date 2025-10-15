package com.ccp.especifications.db.query;

public final class CcpQueryBool  extends CcpQueryComponent{

	 CcpQueryBool(CcpQueryComponent parent) {
		super(parent, "bool");
	}

	public CcpQueryFilter startFilter() {
		return new CcpQueryFilter(this);
	}
	
	public CcpQueryMust startMust() {
		return new CcpQueryMust(this);
	}

	public CcpQueryShould startShould(int minimumShouldMatch) {
		return new CcpQueryShould(this).setMinimumShouldMatch(minimumShouldMatch);
	}

	public CcpQueryMustNot startMustNot() {
		return new CcpQueryMustNot(this);
	}

	public CcpQueryShouldNot startShouldNot() {
		return new CcpQueryShouldNot(this);
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
		return (T)new CcpQueryBool(this.parent);
	}

}
