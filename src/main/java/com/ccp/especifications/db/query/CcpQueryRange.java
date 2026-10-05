package com.ccp.especifications.db.query;

/** The {@code range} node of a query: holds the range of one or more fields and goes back to the right parent context. */
public class CcpQueryRange extends CcpQueryComponent {
	/**
	 * Starts a range node under a clause.
	 * @param parent the clause
	 */
	CcpQueryRange(CcpQueryComponent parent) {
		super(parent, "range");
	}

	/**
	 * Creates an empty range node with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryRange newInstance = new CcpQueryRange(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	/**
	 * Starts the range of one field.
	 * @param fieldName the field
	 * @return the field range node
	 */
	public CcpQueryFieldRange startFieldRange(String fieldName) {
		CcpQueryFieldRange fieldRange = new CcpQueryFieldRange(this, fieldName);
		return fieldRange;
	}

	/**
	 * Ends the range and adds it to the parent simplified query.
	 * @return a copy of the parent with the range
	 */
	public CcpQuerySimplifiedQuery endRangeAndBackToSimplifiedQuery() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends the range and adds it to the parent should clause.
	 * @return a copy of the parent with the range
	 */
	public CcpQueryShould endRangeAndBackToShould() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends the range and adds it to the parent must clause.
	 * @return a copy of the parent with the range
	 */
	public CcpQueryMust endRangeAndBackToMust() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends the range and adds it to the parent should_not clause.
	 * @return a copy of the parent with the range
	 */
	public CcpQueryShouldNot endRangeAndBackToShouldNot() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends the range and adds it to the parent must_not clause.
	 * @return a copy of the parent with the range
	 */
	public CcpQueryMustNot endRangeAndBackToMustNot() {
		return this.parent.addChild(this);
	}
}
