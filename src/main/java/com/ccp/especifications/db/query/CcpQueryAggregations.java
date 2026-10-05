package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.decorators.CcpJsonRepresentation;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/** The {@code aggs} node of the fluent query builder: holds metric aggregations (min, max, avg, sum) and buckets. */
public final class CcpQueryAggregations extends CcpQueryComponent {

	/**
	 * Starts the aggregations node under the request or under a bucket.
	 * @param parent the request or the bucket
	 */
	CcpQueryAggregations(CcpQueryComponent parent) {
		super(parent, "aggs");
	}

	/**
	 * Ends the aggregations and adds them to the request.
	 * @return a copy of the request with the aggregations
	 */
	public CcpQueryOptions endAggregationsAndBackToRequest() {
		return this.parent.addChild(this);
	}

	/**
	 * Ends the sub-aggregations and adds them to the bucket.
	 * @return a copy of the bucket with the sub-aggregations
	 */
	public BucketAggregation endAggregationsAndBackToBucket() {
		return this.parent.addChild(this);
	}

	/**
	 * Adds a {@code min} aggregation.
	 * @param aggregationName the name of the aggregation in the response
	 * @param fieldName the aggregated field
	 * @return a copy of this node with the aggregation
	 */
	public CcpQueryAggregations addMinAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "min");
		return copy;
	}

	/**
	 * Adds {@code {aggregationName: {key: {"field": fieldName}}}} to a copy of this node.
	 * @param aggregationName the name of the aggregation in the response
	 * @param fieldName the aggregated field
	 * @param key the metric ({@code min}, {@code max}, {@code avg} or {@code sum})
	 * @return a copy of this node with the aggregation
	 */
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

	/**
	 * Adds a {@code max} aggregation.
	 * @param aggregationName the name of the aggregation in the response
	 * @param fieldName the aggregated field
	 * @return a copy of this node with the aggregation
	 */
	public CcpQueryAggregations addMaxAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "max");
		return copy;
	}

	/**
	 * Adds an {@code avg} aggregation.
	 * @param aggregationName the name of the aggregation in the response
	 * @param fieldName the aggregated field
	 * @return a copy of this node with the aggregation
	 */
	public CcpQueryAggregations addAvgAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "avg");
		return copy;
	}

	/**
	 * Starts a bucket aggregation.
	 * @param bucketName the name of the bucket in the response
	 * @param fieldName the field the documents are grouped by
	 * @param size the number of buckets (terms) or the interval (histogram)
	 * @return the bucket node
	 */
	public BucketAggregation startBucket(String bucketName, CcpEntityField fieldName, long size) {
		BucketAggregation bucketAggregation = new BucketAggregation(this, bucketName, fieldName, size);
		return bucketAggregation;
	}

	/**
	 * Creates an empty aggregations node with the same parent.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryAggregations newInstance = new CcpQueryAggregations(this.parent);
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	/**
	 * Adds a {@code sum} aggregation.
	 * @param aggregationName the name of the aggregation in the response
	 * @param fieldName the aggregated field
	 * @return a copy of this node with the aggregation
	 */
	public CcpQueryAggregations addSumAggregation(String aggregationName, CcpEntityField fieldName) {
		CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "sum");
		return copy;
	}
}
