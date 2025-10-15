package com.ccp.especifications.db.query;
   
import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;


public final class CcpQuerySimplifiedQuery  extends CcpQueryBooleanOperator {

	CcpQuerySimplifiedQuery(CcpQueryComponent parent) {  
		super(parent, "query");
	}

	public CcpQuerySimplifiedQuery  terms(CcpEntityField field, Object value) {
		return super.terms(field, value);
	}
	
	
	@SuppressWarnings("unchecked")
	public CcpQuerySimplifiedQuery prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	public CcpQueryOptions endSimplifiedQueryAndBackToRequest() {
		return this.parent.addChild(this);
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQuerySimplifiedQuery(this.parent);
	}
	
	Object getValue() {
		return this.json.content;
	}
	 @SuppressWarnings("unchecked")
	 protected CcpQuerySimplifiedQuery copy() {
		CcpQuerySimplifiedQuery instanceCopy = this.getInstanceCopy();
		 
		 instanceCopy.name = this.name;

		 instanceCopy.parent = this.parent.copy();
		 
		 instanceCopy.json = this.json.copy();
		
		 return instanceCopy;
	}
	
	 @SuppressWarnings("unchecked")
	CcpQuerySimplifiedQuery addChild(CcpQueryComponent child) {

		 CcpQuerySimplifiedQuery instanceCopy = this.copy();
		 
		 Object value = child.getValue();
		 
		 instanceCopy.json = instanceCopy.json.getDynamicVersion().put(child.name, value);
		 
		 return instanceCopy;
	 }
		
		@SuppressWarnings("unchecked")
		public CcpQuerySimplifiedQuery matchPhrase(CcpEntityField field, Object value) {
			return super.matchPhrase(field, value);
		}

		
		public CcpQuerySimplifiedQuery term(CcpEntityField field, Object value) {
			return super.term(field, value);
		}
		
		
		@SuppressWarnings("unchecked")
		public CcpQuerySimplifiedQuery match(CcpJsonFieldName field, Object value) {
			return super.match(field, value);
		}
		@SuppressWarnings("unchecked")
		public CcpQuerySimplifiedQuery exists(String field) {
			return super.exists(field);
		}
		
		@SuppressWarnings("unchecked")
		protected CcpQuerySimplifiedQuery addCondition(String field, Object value, String key) {
			Map<String, Object> map = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(field, value).getContent();
			Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(key, map).getContent();
			
			CcpQuerySimplifiedQuery clone = this.copy();
			clone.json = new CcpJsonRepresentation(outerMap);
			return clone;
		}
		
		
		public boolean hasChildreen() {
			return false == this.json.content.isEmpty();
		}


}
