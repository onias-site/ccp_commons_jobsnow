package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonFieldName;

/** The range conditions of one field inside a {@code range} block: chains the comparison operators lt, lte, gt and gte. */
public class CcpQueryFieldRange extends CcpQueryComponent {
	/** The range operators. */
	enum JsonFieldNames implements CcpJsonFieldName {
		/** Less than. */
		lt,
		/** Less than or equal to. */
		lte,
		/** Greater than. */
		gt,
		/** Greater than or equal to. */
		gte
	}

	/**
	 * Starts the range of a field.
	 * @param parent the range node
	 * @param name the field name
	 */
	CcpQueryFieldRange(CcpQueryComponent parent, String name) {
		super(parent, name);
	}

	/**
	 * Creates an empty field range with the same parent and field.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected CcpQueryFieldRange getInstanceCopy() {
		CcpQueryFieldRange newInstance = new CcpQueryFieldRange(this.parent, this.name);
		return newInstance;
	}

	/**
	 * Returns a copy with the operator set.
	 * @param operatorName the operator
	 * @param value the bound
	 * @return the copy
	 */
	private CcpQueryFieldRange putOperator(CcpJsonFieldName operatorName, Object value) {
		CcpQueryFieldRange copy = this.copy();
		copy.json = copy.json.put(operatorName, value);
		return copy;
	}

	/**
	 * Sets the {@code lt} bound.
	 * @param value the bound
	 * @return a copy with the bound
	 */
	public CcpQueryFieldRange lessThan(Object value) {
		CcpQueryFieldRange fieldRange = this.putOperator(JsonFieldNames.lt, value);
		return fieldRange;
	}

	/**
	 * Sets the {@code lte} bound.
	 * @param value the bound
	 * @return a copy with the bound
	 */
	public CcpQueryFieldRange lessThanEquals(Object value) {
		CcpQueryFieldRange fieldRange = this.putOperator(JsonFieldNames.lte, value);
		return fieldRange;
	}

	/**
	 * Sets the {@code gt} bound.
	 * @param value the bound
	 * @return a copy with the bound
	 */
	public CcpQueryFieldRange greaterThan(Object value) {
		CcpQueryFieldRange fieldRange = this.putOperator(JsonFieldNames.gt, value);
		return fieldRange;
	}

	/**
	 * Sets the {@code gte} bound.
	 * @param value the bound
	 * @return a copy with the bound
	 */
	public CcpQueryFieldRange greaterThanEquals(Object value) {
		CcpQueryFieldRange fieldRange = this.putOperator(JsonFieldNames.gte, value);
		return fieldRange;
	}

	/**
	 * Ends the field range and adds it to the range node.
	 * @return a copy of the range node with the field
	 */
	public CcpQueryRange endFieldRangeAndBackToRange() {
		return this.parent.addChild(this);
	}
}
