package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.CcpEntityField;


public final class CcpDbQueryShould  extends CcpDbQueryBooleanOperator {
	enum JsonFieldNames implements CcpJsonFieldName{
		minimum_should_match
	}

	CcpDbQueryShould(CcpDbQueryComponent parent) {
		super(parent, "should");
		
	}
	
	@SuppressWarnings("unchecked")
	public CcpDbQueryShould prefix(CcpEntityField field, Object value) {
		return super.prefix(field, value);
	}

	CcpDbQueryShould setMinimumShouldMatch(int minimumShouldMatch) {
		CcpDbQueryShould copy = this.copy();
		copy.parent.json = copy.parent.json.put(JsonFieldNames.minimum_should_match, minimumShouldMatch);
		return copy;
	}
	
	public CcpDbQueryBool endShouldAndBackToBool() {
		CcpDbQueryComponent copy = this.parent.copy();
		CcpDbQueryBool addChild = copy.addChild(this);
		return addChild;
	}
	
	public CcpDbQueryShould matchPhrase2(CcpEntityField field, Object value) {
		return super.matchPhrase(field, value);
	}

	
	@SuppressWarnings("unchecked")
	public CcpDbQueryShould match(CcpEntityField field, Object value) {
		return super.match(field, value);
	}
	
	public CcpDbQueryShould matchPhrase(String field, Object value, double boost) {
		CcpDbQueryShould addCondition = this.addCondition(field, value, "match_phrase", boost, "");
		return addCondition;
	}
	
	public CcpDbQueryShould match(String field, Object value, double boost, String operator) {
		CcpDbQueryShould addCondition = this.addCondition(field, value, "match", boost, operator);
		return addCondition;
	}



	
	@SuppressWarnings("unchecked")
	public CcpDbQueryShould term(CcpEntityField field, Object value) {
		return super.term(field, value);
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQueryShould(this.parent);
	}
	
	@SuppressWarnings("unchecked")
	public CcpDbQueryShould exists(String field) {
		return super.exists(field);
	}	
	
	public CcpDbQueryBool startBool() {
		return new CcpDbQueryBool(this);
	}

}
