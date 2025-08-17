package com.ccp.especifications.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public class CcpDbQueryOptions extends CcpDbQueryComponent{
	enum JsonFieldNames implements CcpJsonFieldName{
		sort, match_all
	}
	
	public static final CcpDbQueryOptions INSTANCE = new CcpDbQueryOptions();
	
	private CcpDbQueryOptions() { 
		super(null, "");
	}

	public CcpDbQuery startQuery() {
		return new CcpDbQuery(this);
	}
	
	public CcpDbQuerySimplifiedQuery startSimplifiedQuery() {
		return new CcpDbQuerySimplifiedQuery(this); 
	}
	
	public CcpDbQueryAggregations startAggregations() {
		return new CcpDbQueryAggregations(this);
	}
	
	public CcpDbQueryOptions addAscSorting(String fields) {
		CcpDbQueryOptions sort = this.addSorting("asc", fields);
		return sort;
	}

	public CcpDbQueryOptions addDescSorting(String... fields) {
		CcpDbQueryOptions sort = this.addSorting("desc", fields);
		return sort;
	}
	
	public CcpDbQueryOptions addSorting(String sortType, String... fields) {
		CcpDbQueryOptions sort = this;
		
		for (String field : fields) {
			sort = sort.sort(field, sortType);
		}
		
		return sort;
		
	}

	private CcpDbQueryOptions sort(String fieldName, String sortType) {
		CcpDbQueryOptions copy = this.copy();
		Map<String, Object> content = CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(fieldName, sortType).getContent();
		
		List<Object> asList = Arrays.asList(content);
		//ATTENTION PRESTAR ATENNCAO NA LINHA ABAIXO, POIS ERA ASSIM ANTES: if(copy.json.content.containsKey('sort')) {
		if(copy.json.containsAllFields(JsonFieldNames.sort)) {
			List<Object> sort = copy.json.getAsObjectList(JsonFieldNames.sort);
			asList = new ArrayList<>(sort);
			asList.add(content);
		}
		copy.json = copy.json.put(JsonFieldNames.sort, asList);
		return copy;
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpDbQueryComponent> T getInstanceCopy() {
		return (T)new CcpDbQueryOptions();
	}

	public CcpQueryExecutorDecorator selectFrom(String... resourcesNames) {
		return new CcpQueryExecutorDecorator(this, resourcesNames);
	}
	
	public CcpDbQueryOptions setScrollId(String scrollId) {
		CcpDbQueryOptions clone = super.putProperty("scroll_id", scrollId);
		return clone;
		
	}
	
	public CcpDbQueryOptions setSize(int size) {
		CcpDbQueryOptions clone = super.putProperty("size", size);
		return clone;
	}
	
	public CcpDbQueryOptions maxResults() {
		CcpDbQueryOptions clone = super.putProperty("size", 10000);
		return clone;
	}

	public CcpDbQueryOptions zeroResults() {
		CcpDbQueryOptions clone = super.putProperty("size", 0);
		return clone;
	}
	

	public CcpDbQueryOptions setFrom(int from) {
		CcpDbQueryOptions clone = super.putProperty("from", from);
		return clone;
	}

	public CcpDbQueryOptions setScrollTime(String scrollTime) {
		CcpDbQueryOptions clone = super.putProperty("scroll", scrollTime);
		return clone;
	}
	
	public CcpDbQueryOptions matchAll() {
		CcpDbQueryOptions clone = super.putProperty("query", CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.match_all, CcpOtherConstants.EMPTY_JSON.content).content);
		return clone;
	}

	
}
