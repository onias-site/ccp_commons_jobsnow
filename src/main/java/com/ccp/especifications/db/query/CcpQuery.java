package com.ccp.especifications.db.query;

/** The {@code query} node of a request in the fluent query builder: the entry point of the main query block. */
public final class CcpQuery extends CcpQueryComponent {
	/**
	 * Starts the {@code query} node under the request.
	 * @param parent the request node
	 */
	CcpQuery(CcpQueryComponent parent) {
		super(parent, "query");
	}

	/**
	 * Starts a {@code bool} query inside this node.
	 * @return the bool node
	 */
	public CcpQueryBool startBool() {
		CcpQueryBool boolQuery = new CcpQueryBool(this);
		return boolQuery;
	}

	/**
	 * Ends the query block and adds it to the request.
	 * @return a copy of the request with the query
	 */
	public CcpQueryOptions endQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}

	/**
	 * Creates an empty query node with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQuery newInstance = new CcpQuery(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}
}
