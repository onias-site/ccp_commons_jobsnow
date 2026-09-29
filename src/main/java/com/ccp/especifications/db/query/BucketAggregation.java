package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.decorators.CcpJsonRepresentation;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

public final class BucketAggregation extends CcpQueryComponent {

	private final CcpEntityField fieldName;
	private final long size;

	BucketAggregation(CcpQueryComponent parent, String name, CcpEntityField fieldName, long size) {
		super(parent, name);
		this.fieldName = fieldName;
		this.size = size;
	}

	public CcpQueryAggregations endTermsBuckedAndBackToAggregations() {
		CcpQueryAggregations aggregations = this.getStatisRequest("size", "terms");
		return aggregations;
	}

	public CcpQueryAggregations endHistogramBuckedAndBackToAggregations() {
		CcpQueryAggregations aggregations = this.getStatisRequest("interval", "histogram");
		return aggregations;
	}

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

	public CcpQueryAggregations startAggregations() {
		CcpQueryAggregations aggregations = new CcpQueryAggregations(this);
		return aggregations;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		BucketAggregation bucketAggregation = new BucketAggregation(this.parent, this.name, this.fieldName, this.size);
		T typedCopy = (T) bucketAggregation;
		return typedCopy;
	}
}
