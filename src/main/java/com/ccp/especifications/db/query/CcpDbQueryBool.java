package com.ccp.especifications.db.query;

public final class CcpDbQueryBool  extends CcpDbQueryComponent{

	 CcpDbQueryBool(CcpDbQueryComponent parent) {
		super(parent, "bool");
	}

	public CcpDbQueryFilter startFilter() {
		return new CcpDbQueryFilter(this);
	}
	
	public CcpDbQueryMust startMust() {
		return new CcpDbQueryMust(this);
	}

	public CcpDbQueryShould startShould(int minimumShouldMatch) {
		return new CcpDbQueryShould(this).setMinimumShouldMatch(minimumShouldMatch);
	}

	public CcpDbQueryMustNot startMustNot() {
		return new CcpDbQueryMustNot(this);
	}

	public CcpDbQueryShouldNot startShouldNot() {
		return new CcpDbQueryShouldNot(this);
	}

	public CcpDbQueryShould endBoolAndBackToShould() {
		return this.parent.addChild(this);
	}

	public CcpDbQueryMust endBoolAndBackToMust() {
		return this.parent.addChild(this);
	}

	public CcpDbQueryShouldNot endBoolAndBackToShouldNot() {
		return this.parent.addChild(this);
	}

	public CcpDbQueryMustNot endBoolAndBackToMustNot() {
		return this.parent.addChild(this);
	}
	
	public CcpDbQueryFilter endBoolAndBackToFilter() {
		return this.parent.addChild(this);
	}
	
	public CcpDbQuery endBoolAndBackToQuery() {
		return this.parent.addChild(this);
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQueryBool(this.parent);
	}

}
