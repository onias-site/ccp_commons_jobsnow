package com.ccp.especifications.db.query;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;

abstract class CcpQueryComponent {

	public CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON;
	protected CcpQueryComponent parent;
	protected String name;
	
	CcpQueryComponent(CcpQueryComponent parent, String name) {
		this.parent = parent;
		this.name = name;
	}


	 protected abstract <T extends CcpQueryComponent> T getInstanceCopy() ;
	
	 Object getValue() {
		return this.json.content;
	}
	 
	 @SuppressWarnings("unchecked")
	<T extends CcpQueryComponent> T addChild(CcpQueryComponent child) {

		 CcpQueryComponent instanceCopy = this.copy();
		 
		 Object value = child.getValue();
		 
		 instanceCopy.json = instanceCopy.json.getDynamicVersion().put(child.name, value);
		 
		 return (T)instanceCopy;
	 }

	 @SuppressWarnings("unchecked")
	 protected <T extends CcpQueryComponent> T copy() {
		CcpQueryComponent instanceCopy = this.getInstanceCopy();
		 
		 instanceCopy.name = this.name;

		 if(this.parent != null) {
			 instanceCopy.parent = this.parent.copy();
		 }
		 
		 instanceCopy.json = this.json.copy();
		
		 return (T)instanceCopy;
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
	
	public <T extends CcpQueryComponent> T putProperty(String propertyName, Object propertyValue){
		T clone = this.copy();
		clone.json = clone.json.getDynamicVersion().put(propertyName, propertyValue);
		return clone;

	}
}
