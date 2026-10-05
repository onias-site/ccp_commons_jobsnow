package com.ccp.especifications.db.query;

import com.ccp.aop.CcpAllowNullParameter;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;

/**
 * Base class of every node of the Elasticsearch fluent query builder.
 * Defines the tree structure (parent/child), the node's JSON content and the immutable copy mechanism
 * that ensures each operation returns a new instance without changing the original state.
 */
public abstract class CcpQueryComponent {

	/** The JSON content of the node. */
	public CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON;
	/** The parent node; {@code null} for the request (root). */
	protected CcpQueryComponent parent;
	/** The key of the node in the JSON of its parent. */
	protected String name;

	/**
	 * Creates a node.
	 * @param parent the parent node; {@code null} for the root
	 * @param name the key of the node in its parent
	 */
	@CcpAllowNullParameter
	CcpQueryComponent(CcpQueryComponent parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	/**
	 * Creates an empty node of the same concrete type, with the same parent (and constructor arguments).
	 * @param <T> the concrete type
	 * @return the new instance
	 */
	protected abstract <T extends CcpQueryComponent> T getInstanceCopy();

	/**
	 * Returns the JSON value of the node inside its parent.
	 * @return the map of the node
	 */
	Object getValue() {
		return this.json.content;
	}

	/**
	 * Returns a copy of this node with {@code {child.name: child.value}} added.
	 * @param <T> the concrete type
	 * @param child the child node
	 * @return the copy with the child
	 */
	@SuppressWarnings("unchecked")
	<T extends CcpQueryComponent> T addChild(CcpQueryComponent child) {
		CcpQueryComponent instanceCopy = this.copy();
		Object value = child.getValue();
		CcpFieldName childKey = new CcpFieldName(child.name);
		instanceCopy.json = instanceCopy.json.put(childKey, value);
		T typedCopy = (T) instanceCopy;
		return typedCopy;
	}

	/**
	 * Returns a deep copy of the node: name, content and the whole parent chain, so that the original is never changed.
	 * @param <T> the concrete type
	 * @return the copy
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T copy() {
		CcpQueryComponent instanceCopy = this.getInstanceCopy();
		instanceCopy.name = this.name;
		boolean hasParent = this.parent != null;
		if (hasParent) {
			instanceCopy.parent = this.parent.copy();
		}
		instanceCopy.json = this.json.copy();
		T typedCopy = (T) instanceCopy;
		return typedCopy;
	}

	/**
	 * Returns the compact JSON of the node.
	 * @return the JSON text
	 */
	public final String toString() {
		Object value = this.getValue();
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String jsonText = jsonHandler.toJson(value);
		return jsonText;
	}

	/**
	 * Tells whether the node has content.
	 * @return {@code true} when the JSON is not empty
	 */
	public boolean hasChildreen() {
		boolean contentEmpty = this.json.content.isEmpty();
		boolean hasContent = false == contentEmpty;
		return hasContent;
	}

	/**
	 * Returns a copy of the node with the property set.
	 * @param <T> the concrete type
	 * @param propertyName the property
	 * @param propertyValue the value
	 * @return the copy
	 */
	public <T extends CcpQueryComponent> T putProperty(CcpJsonFieldName propertyName, Object propertyValue) {
		T clone = this.copy();
		clone.json = clone.json.put(propertyName, propertyValue);
		return clone;
	}
}
