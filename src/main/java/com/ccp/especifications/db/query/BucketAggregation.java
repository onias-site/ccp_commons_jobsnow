package com.ccp.especifications.db.query;

import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.CcpEntityField;

enum BucketAggregationConstants  implements CcpJsonFieldName{
	field
	
}

public final class BucketAggregation extends CcpDbQueryComponent {
	
	private final CcpEntityField fieldName;
	private final long size;
	
	BucketAggregation(CcpDbQueryComponent parent, String name, CcpEntityField fieldName, long size) {
		super(parent, name);
		this.fieldName = fieldName;
		this.size = size;
	}

	public CcpDbQueryAggregations endTermsBuckedAndBackToAggregations() {
		CcpDbQueryAggregations addChild = this.getStatisRequest("size", "terms");
		return addChild;
	}

	public CcpDbQueryAggregations endHistogramBuckedAndBackToAggregations() {
		CcpDbQueryAggregations addChild = this.getStatisRequest("interval", "histogram");
		return addChild;
	}

	private CcpDbQueryAggregations getStatisRequest(String p1, String p2) {
		CcpDbQueryComponent copy = this.copy();
		Map<String, Object> content = CcpOtherConstants.EMPTY_JSON.put(BucketAggregationConstants.field, this.fieldName)
				.getDynamicVersion().put(p1, this.size).getContent();
		copy.json = copy.json.getDynamicVersion().put(p2, content);
		CcpDbQueryAggregations addChild = this.parent.addChild(copy);
		return addChild;
	}
	
	public CcpDbQueryAggregations startAggregations() {
		return new CcpDbQueryAggregations(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new BucketAggregation(this.parent, this.name, this.fieldName, this.size);
	}

}
