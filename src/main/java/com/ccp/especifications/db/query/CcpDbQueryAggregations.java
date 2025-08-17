package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.CcpEntityField;

public final class CcpDbQueryAggregations extends CcpDbQueryComponent{
	enum JsonFieldNames implements CcpJsonFieldName{
		field
	}
	
	 CcpDbQueryAggregations(CcpDbQueryComponent parent) {
		super(parent, "aggs");
	}

	public CcpDbQueryOptions endAggregationsAndBackToRequest() {
		return this.parent.addChild(this);
	}

	public BucketAggregation endAggregationsAndBackToBucket() {
		return this.parent.addChild(this);
	}

	public CcpDbQueryAggregations addMinAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpDbQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "min");
		return copy;
	}

	private CcpDbQueryAggregations createAggregation(String aggregationName, CcpEntityField fieldName, String key) {
		CcpDbQueryAggregations copy = this.copy();
		Map<String, Object> c1 = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.field, fieldName).getContent();
		Map<String, Object> c2 = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(key, c1).getContent();
		copy.json = copy.json.getDynamicVersion().put(aggregationName, c2);
		return copy;
	}
	public CcpDbQueryAggregations addMaxAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpDbQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "max");
		return copy;
	}

	public CcpDbQueryAggregations addAvgAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpDbQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "avg");
		return copy;
	}

	public BucketAggregation startBucket(String bucketName, CcpEntityField fieldName, long size) {
		BucketAggregation bucketAggregation = new BucketAggregation(this, bucketName, fieldName, size);
		return bucketAggregation;
				
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQueryAggregations(this.parent);
	}

	public CcpDbQueryAggregations addSumAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpDbQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "sum");
		return copy;
	}

}
