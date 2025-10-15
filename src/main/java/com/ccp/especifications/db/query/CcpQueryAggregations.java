package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;


public final class CcpQueryAggregations extends CcpQueryComponent{
	enum JsonFieldNames implements CcpJsonFieldName{
		field
	}
	
	 CcpQueryAggregations(CcpQueryComponent parent) {
		super(parent, "aggs");
	}

	public CcpQueryOptions endAggregationsAndBackToRequest() {
		return this.parent.addChild(this);
	}

	public BucketAggregation endAggregationsAndBackToBucket() {
		return this.parent.addChild(this);
	}

	public CcpQueryAggregations addMinAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "min");
		return copy;
	}

	private CcpQueryAggregations createAggregation(String aggregationName, CcpEntityField fieldName, String key) {
		CcpQueryAggregations copy = this.copy();
		Map<String, Object> c1 = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.field, fieldName).getContent();
		Map<String, Object> c2 = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(key, c1).getContent();
		copy.json = copy.json.getDynamicVersion().put(aggregationName, c2);
		return copy;
	}
	public CcpQueryAggregations addMaxAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "max");
		return copy;
	}

	public CcpQueryAggregations addAvgAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "avg");
		return copy;
	}

	public BucketAggregation startBucket(String bucketName, CcpEntityField fieldName, long size) {
		BucketAggregation bucketAggregation = new BucketAggregation(this, bucketName, fieldName, size);
		return bucketAggregation;
				
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryAggregations(this.parent);
	}

	public CcpQueryAggregations addSumAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "sum");
		return copy;
	}

}
