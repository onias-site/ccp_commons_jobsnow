package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.decorators.CcpJsonRepresentation;

public final class CcpQueryAggregations extends CcpQueryComponent {
	enum JsonFieldNames implements CcpJsonFieldName {
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
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.field, fieldName);
		Map<String, Object> c1 = put.getContent();
		CcpFieldName ccpFieldName = new CcpFieldName(key);
		CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName, c1);
		Map<String, Object> c2 = put2.getContent();
		CcpFieldName ccpFieldName2 = new CcpFieldName(aggregationName);
		copy.json = copy.json.put(ccpFieldName2, c2);
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
		CcpQueryAggregations ccpQueryAggregations = new CcpQueryAggregations(this.parent);
		T t = (T) ccpQueryAggregations;
		return t;
	}

	public CcpQueryAggregations addSumAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "sum");
		return copy;
	}
}
