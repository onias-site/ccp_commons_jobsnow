package com.ccp.especifications.db.query;

public class CcpDbQueryRange extends CcpDbQueryComponent {

	CcpDbQueryRange(CcpDbQueryComponent parent) {
		super(parent, "range"); 
	}
   
	@SuppressWarnings("unchecked")
	
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQueryRange(this.parent);
	}

	public CcpDbQueryFieldRange startFieldRange(String fieldName) {
		return new CcpDbQueryFieldRange(this, fieldName);
	}
	
	public CcpDbQuerySimplifiedQuery endRangeAndBackToSimplifiedQuery() {
		return this.parent.addChild(this);
	}
	public CcpDbQueryShould endRangeAndBackToShould() {
		return this.parent.addChild(this);
	}

	public CcpDbQueryMust endRangeAndBackToMust() {
		return this.parent.addChild(this);
	}

	public CcpDbQueryShouldNot endRangeAndBackToShouldNot() {
		return this.parent.addChild(this);
	}

	public CcpDbQueryMustNot endRangeAndBackToMustNot() {
		return this.parent.addChild(this);
	}

}
