package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;



public final class BucketAggregation extends CcpQueryComponent {
	enum JsonFieldNames implements CcpJsonFieldName{
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
		Map<String, Object> content = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.field, this.fieldName)
				.getDynamicVersion().put(p1, this.size).getContent();
		copy.json = copy.json.getDynamicVersion().put(p2, content);
		CcpQueryAggregations addChild = this.parent.addChild(copy);
		return addChild;
	}
	
	public CcpQueryAggregations startAggregations() {
		return new CcpQueryAggregations(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new BucketAggregation(this.parent, this.name, this.fieldName, this.size);
	}

}
