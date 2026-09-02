package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.decorators.CcpJsonRepresentation;

public final class BucketAggregation extends CcpQueryComponent {
	enum JsonFieldNames implements CcpJsonFieldName {
		field
	}

	private final CcpEntityField fieldName;
	private final long size;

	BucketAggregation(CcpQueryComponent parent, String name, CcpEntityField fieldName, long size) {
		super(parent, name);
		this.fieldName = fieldName;
		this.size = size;
	}

	public CcpQueryAggregations endTermsBuckedAndBackToAggregations() {
		CcpQueryAggregations addChild = this.getStatisRequest("size", "terms");
		return addChild;
	}

	public CcpQueryAggregations endHistogramBuckedAndBackToAggregations() {
		CcpQueryAggregations addChild = this.getStatisRequest("interval", "histogram");
		return addChild;
	}

	private CcpQueryAggregations getStatisRequest(String p1, String p2) {
		CcpQueryComponent copy = this.copy();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.field, this.fieldName);
		CcpFieldName ccpFieldName = new CcpFieldName(p1);
		var put2 = put
				.put(ccpFieldName, this.size);
				Map<String, Object> content = put2.getContent();
				CcpFieldName ccpFieldName2 = new CcpFieldName(p2);
				copy.json = copy.json.put(ccpFieldName2, content);
		CcpQueryAggregations addChild = this.parent.addChild(copy);
		return addChild;
	}

	public CcpQueryAggregations startAggregations() {
		CcpQueryAggregations ccpQueryAggregations = new CcpQueryAggregations(this);
		return ccpQueryAggregations;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		BucketAggregation bucketAggregation = new BucketAggregation(this.parent, this.name, this.fieldName, this.size);
		T t = (T) bucketAggregation;
		return t;
	}
}
