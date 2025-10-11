package com.ccp.especifications.db.query;
   
import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.CcpEntityField;


public final class CcpDbQuerySimplifiedQuery  extends CcpDbQueryBooleanOperator {

	CcpDbQuerySimplifiedQuery(CcpDbQueryComponent parent) {  
		super(parent, "query");
	}

	public CcpDbQuerySimplifiedQuery  terms(CcpEntityField field, Object value) {
		return super.terms(field, value);
	}
	
	
	@SuppressWarnings("unchecked")
	public CcpDbQuerySimplifiedQuery prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	public CcpDbQueryOptions endSimplifiedQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQuerySimplifiedQuery(this.parent);
	}
	
	Object getValue() {
		return this.json.content;
	}
	 @SuppressWarnings("unchecked")
	 protected CcpDbQuerySimplifiedQuery copy() {
		CcpDbQuerySimplifiedQuery instanceCopy = this.getInstanceCopy();
		 
		 instanceCopy.name = this.name;

		 instanceCopy.parent = this.parent.copy();
		 
		 instanceCopy.json = this.json.copy();
		
		 return instanceCopy;
	}
	
	 @SuppressWarnings("unchecked")
	CcpDbQuerySimplifiedQuery addChild(CcpDbQueryComponent child) {

		 CcpDbQuerySimplifiedQuery instanceCopy = this.copy();
		 
		 Object value = child.getValue();
		 
		 instanceCopy.json = instanceCopy.json.getDynamicVersion().put(child.name, value);
		 
		 return instanceCopy;
	 }
		
		@SuppressWarnings("unchecked")
		public CcpDbQuerySimplifiedQuery matchPhrase(CcpEntityField field, Object value) {
			return super.matchPhrase(field, value);
		}

		
		public CcpDbQuerySimplifiedQuery term(CcpEntityField field, Object value) {
			return super.term(field, value);
		}
		
		
		@SuppressWarnings("unchecked")
		public CcpDbQuerySimplifiedQuery match(CcpJsonFieldName field, Object value) {
			return super.match(field, value);
		}
		@SuppressWarnings("unchecked")
		public CcpDbQuerySimplifiedQuery exists(String field) {
			return super.exists(field);
		}
		
		@SuppressWarnings("unchecked")
		protected CcpDbQuerySimplifiedQuery addCondition(String field, Object value, String key) {
			Map<String, Object> map = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(field, value).getContent();
			Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(key, map).getContent();
			
			CcpDbQuerySimplifiedQuery clone = this.copy();
			clone.json = new CcpJsonRepresentation(outerMap);
			return clone;
		}
		
		
		public boolean hasChildreen() {
			return this.json.content.isEmpty() == false;
		}


}
