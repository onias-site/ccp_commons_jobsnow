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

	public CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON;
	protected CcpQueryComponent parent;
	protected String name;

	@CcpAllowNullParameter
	CcpQueryComponent(CcpQueryComponent parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	protected abstract <T extends CcpQueryComponent> T getInstanceCopy();

	Object getValue() {
		return this.json.content;
	}

	@SuppressWarnings("unchecked")
	<T extends CcpQueryComponent> T addChild(CcpQueryComponent child) {
		CcpQueryComponent instanceCopy = this.copy();
		Object value = child.getValue();
		CcpFieldName childKey = new CcpFieldName(child.name);
		instanceCopy.json = instanceCopy.json.put(childKey, value);
		T typedCopy = (T) instanceCopy;
		return typedCopy;
	}

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

	public final String toString() {
		Object value = this.getValue();
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String jsonText = jsonHandler.toJson(value);
		return jsonText;
	}

	public boolean hasChildreen() {
		boolean contentEmpty = this.json.content.isEmpty();
		boolean hasContent = false == contentEmpty;
		return hasContent;
	}

	public <T extends CcpQueryComponent> T putProperty(CcpJsonFieldName propertyName, Object propertyValue) {
		T clone = this.copy();
		clone.json = clone.json.put(propertyName, propertyValue);
		return clone;
	}
}
