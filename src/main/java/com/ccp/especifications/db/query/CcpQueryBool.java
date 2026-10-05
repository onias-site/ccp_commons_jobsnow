package com.ccp.especifications.db.query;

/**
 * The {@code bool} node of an Elasticsearch boolean query: composes the {@code filter}, {@code must}, {@code should},
 * {@code must_not} and {@code should_not} clauses.
 */
public final class CcpQueryBool extends CcpQueryComponent {
	/**
	 * Starts a bool node under its parent.
	 * @param parent the parent node
	 */
	CcpQueryBool(CcpQueryComponent parent) {
		super(parent, "bool");
	}

	/**
	 * Starts the {@code filter} clause (conditions that must hold without affecting the score).
	 * @return the filter node
	 */
	public CcpQueryFilter startFilter() {
		CcpQueryFilter filter = new CcpQueryFilter(this);
		return filter;
	}

	/**
	 * Starts the {@code must} clause (conditions that must hold and affect the score).
	 * @return the must node
	 */
	public CcpQueryMust startMust() {
		CcpQueryMust must = new CcpQueryMust(this);
		return must;
	}

	/**
	 * Starts the {@code should} clause and sets {@code minimum_should_match} on this bool node.
	 * @param minimumShouldMatch how many should conditions must hold
	 * @return the should node
	 */
	public CcpQueryShould startShould(int minimumShouldMatch) {
		CcpQueryShould should = new CcpQueryShould(this);
		CcpQueryShould shouldWithMinimumMatch = should.setMinimumShouldMatch(minimumShouldMatch);
		return shouldWithMinimumMatch;
	}

	/**
	 * Starts the {@code must_not} clause (conditions that exclude documents).
	 * @return the must_not node
	 */
	public CcpQueryMustNot startMustNot() {
		CcpQueryMustNot mustNot = new CcpQueryMustNot(this);
		return mustNot;
	}

	/**
	 * Starts a {@code should_not} clause. Elasticsearch has no such clause, so a request using it is rejected.
	 * @return the should_not node
	 */
	public CcpQueryShouldNot startShouldNot() {
		CcpQueryShouldNot shouldNot = new CcpQueryShouldNot(this);
		return shouldNot;
	}

	/**
	 * Ends this bool node and adds it to the parent should clause.
	 * @return a copy of the parent with this node
	 */
	public CcpQueryShould endBoolAndBackToShould() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends this bool node and adds it to the parent must clause.
	 * @return a copy of the parent with this node
	 */
	public CcpQueryMust endBoolAndBackToMust() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends this bool node and adds it to the parent should_not clause.
	 * @return a copy of the parent with this node
	 */
	public CcpQueryShouldNot endBoolAndBackToShouldNot() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends this bool node and adds it to the parent must_not clause.
	 * @return a copy of the parent with this node
	 */
	public CcpQueryMustNot endBoolAndBackToMustNot() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends this bool node and adds it to the parent filter.
	 * @return a copy of the parent with this node
	 */
	public CcpQueryFilter endBoolAndBackToFilter() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends this bool node and adds it to the parent query node.
	 * @return a copy of the parent with this node
	 */
	public CcpQuery endBoolAndBackToQuery() {
		return this.parent.addChild(this);
	}

	/**
	 * Creates an empty bool node with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryBool newInstance = new CcpQueryBool(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}
}
