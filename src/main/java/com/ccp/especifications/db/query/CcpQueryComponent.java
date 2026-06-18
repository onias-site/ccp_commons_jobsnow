package com.ccp.especifications.db.query;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;

/**
 * Classe base de todos os nós do builder fluent de queries do Elasticsearch.
 * Define a estrutura de árvore (parent/child), o conteúdo JSON do nó e o mecanismo de cópia imutável
 * que garante que cada operação retorne uma nova instância sem alterar o estado original.
 */
public abstract class CcpQueryComponent {

	public CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON;
	protected CcpQueryComponent parent;
	protected String name;

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
		instanceCopy.json = instanceCopy.json.put(new CcpFieldName(child.name), value);
		return (T) instanceCopy;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T copy() {
		CcpQueryComponent instanceCopy = this.getInstanceCopy();
		instanceCopy.name = this.name;
		if (this.parent != null) {
			instanceCopy.parent = this.parent.copy();
		}
		instanceCopy.json = this.json.copy();
		return (T) instanceCopy;
	}

	public final String toString() {
		Object value = this.getValue();
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String _json = json.toJson(value);
		return _json;
	}

	public boolean hasChildreen() {
		return false == this.json.content.isEmpty();
	}

	public <T extends CcpQueryComponent> T putProperty(String propertyName, Object propertyValue) {
		T clone = this.copy();
		clone.json = clone.json.put(new CcpFieldName(propertyName), propertyValue);
		return clone;
	}
}
