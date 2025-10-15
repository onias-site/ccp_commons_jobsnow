package com.ccp.especifications.db.query;

public class CcpQueryRange extends CcpQueryComponent {

	CcpQueryRange(CcpQueryComponent parent) {
		super(parent, "range"); 
	}
   
	@SuppressWarnings("unchecked")
	
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryRange(this.parent);
	}

	public CcpQueryFieldRange startFieldRange(String fieldName) {
		return new CcpQueryFieldRange(this, fieldName);
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
