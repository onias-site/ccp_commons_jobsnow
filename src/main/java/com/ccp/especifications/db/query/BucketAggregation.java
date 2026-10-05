package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.decorators.CcpJsonRepresentation;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * An Elasticsearch aggregation bucket (terms or histogram) within the fluent query builder: groups the documents by a
 * field and is ended by going back to the parent aggregations node.
 */
public final class BucketAggregation extends CcpQueryComponent {

	/** The field the documents are grouped by. */
	private final CcpEntityField fieldName;
	/** The number of buckets (terms) or the interval (histogram). */
	private final long size;

	/**
	 * Starts a bucket under an aggregations node.
	 * @param parent the aggregations node
	 * @param name the bucket name
	 * @param fieldName the field the documents are grouped by
	 * @param size the number of buckets or the interval
	 */
	BucketAggregation(CcpQueryComponent parent, String name, CcpEntityField fieldName, long size) {
		super(parent, name);
		this.fieldName = fieldName;
		this.size = size;
	}

	/**
	 * Ends the bucket as a {@code terms} aggregation ({@code {"terms": {"field": ..., "size": ...}}}).
	 * @return the parent aggregations node with the bucket
	 */
	public CcpQueryAggregations endTermsBuckedAndBackToAggregations() {
		CcpQueryAggregations aggregations = this.getStatisRequest("size", "terms");
		return aggregations;
	}

	/**
	 * Ends the bucket as a {@code histogram} aggregation ({@code {"histogram": {"field": ..., "interval": ...}}}).
	 * @return the parent aggregations node with the bucket
	 */
	public CcpQueryAggregations endHistogramBuckedAndBackToAggregations() {
		CcpQueryAggregations aggregations = this.getStatisRequest("interval", "histogram");
		return aggregations;
	}

	/**
	 * Writes the aggregation settings into a copy of the bucket and adds it to the parent aggregations node.
	 * @param sizeParameterName {@code size} or {@code interval}
	 * @param aggregationType {@code terms} or {@code histogram}
	 * @return the parent aggregations node with the bucket
	 */
	private CcpQueryAggregations getStatisRequest(String sizeParameterName, String aggregationType) {
		CcpQueryComponent copy = this.copy();
		CcpJsonRepresentation fieldJson = CcpOtherConstants.EMPTY_JSON.put(CcpJsonCommonsFields.field, this.fieldName);
		CcpFieldName sizeParameterKey = new CcpFieldName(sizeParameterName);
		var aggregationSettings = fieldJson
				.put(sizeParameterKey, this.size);
				Map<String, Object> content = aggregationSettings.getContent();
				CcpFieldName aggregationTypeKey = new CcpFieldName(aggregationType);
				copy.json = copy.json.put(aggregationTypeKey, content);
		CcpQueryAggregations aggregations = this.parent.addChild(copy);
		return aggregations;
	}

	/**
	 * Starts sub-aggregations inside the bucket.
	 * @return the sub-aggregations node
	 */
	public CcpQueryAggregations startAggregations() {
		CcpQueryAggregations aggregations = new CcpQueryAggregations(this);
		return aggregations;
	}

	/**
	 * Creates an empty bucket with the same parent, name, field and size.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		BucketAggregation bucketAggregation = new BucketAggregation(this.parent, this.name, this.fieldName, this.size);
		T typedCopy = (T) bucketAggregation;
		return typedCopy;
	}
}
