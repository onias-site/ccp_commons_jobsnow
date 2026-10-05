package com.ccp.especifications.db.query;

/** The {@code filter} node of a boolean query: its conditions must hold but do not affect the score. */
public final class CcpQueryFilter extends CcpQueryComponent {
	/**
	 * Starts the filter under a bool node.
	 * @param parent the bool node
	 */
	CcpQueryFilter(CcpQueryComponent parent) {
		super(parent, "filter");
	}

	/**
	 * Starts a bool query inside the filter.
	 * @return the bool node
	 */
	public CcpQueryBool startBool() {
		CcpQueryBool boolQuery = new CcpQueryBool(this);
		return boolQuery;
	}

	/**
	 * Ends the filter and adds it to the bool node.
	 * @return a copy of the bool node with the filter
	 */
	public CcpQueryBool endFilterAndBackToBool() {
		return this.parent.addChild(this);
	}

	/**
	 * Creates an empty filter with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryFilter newInstance = new CcpQueryFilter(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}
}
