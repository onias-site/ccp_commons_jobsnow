package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.decorators.CcpJsonRepresentation;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

public final class CcpQueryAggregations extends CcpQueryComponent {

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
		CcpJsonRepresentation fieldJson = CcpOtherConstants.EMPTY_JSON.put(CcpJsonCommonsFields.field, fieldName);
		Map<String, Object> fieldContent = fieldJson.getContent();
		CcpFieldName aggregationTypeKey = new CcpFieldName(key);
		CcpJsonRepresentation aggregationJson = CcpOtherConstants.EMPTY_JSON.put(aggregationTypeKey, fieldContent);
		Map<String, Object> aggregationContent = aggregationJson.getContent();
		CcpFieldName aggregationNameKey = new CcpFieldName(aggregationName);
		copy.json = copy.json.put(aggregationNameKey, aggregationContent);
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
		CcpQueryAggregations newInstance = new CcpQueryAggregations(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	public CcpQueryAggregations addSumAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "sum");
		return copy;
	}
}
