package com.ccp.especifications.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public class CcpQueryOptions extends CcpQueryComponent{
	enum JsonFieldNames implements CcpJsonFieldName{
		sort, match_all
	}
	
	public static final CcpQueryOptions INSTANCE = new CcpQueryOptions();
	
	private CcpQueryOptions() { 
		super(null, "");
	}

	public CcpQuery startQuery() {
		return new CcpQuery(this);
	}
	
	public CcpQuerySimplifiedQuery startSimplifiedQuery() {
		return new CcpQuerySimplifiedQuery(this); 
	}
	
	public CcpQueryAggregations startAggregations() {
		return new CcpQueryAggregations(this);
	}
	
	public CcpQueryOptions addAscSorting(String fields) {
		CcpQueryOptions sort = this.addSorting("asc", fields);
		return sort;
	}

	public CcpQueryOptions addDescSorting(String... fields) {
		CcpQueryOptions sort = this.addSorting("desc", fields);
		return sort;
	}
	
	public CcpQueryOptions addSorting(String sortType, String... fields) {
		CcpQueryOptions sort = this;
		
		for (String field : fields) {
			sort = sort.sort(field, sortType);
		}
		
		return sort;
		
	}

	private CcpQueryOptions sort(String fieldName, String sortType) {
		CcpQueryOptions copy = this.copy();
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
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryOptions();
	}

	public CcpQueryExecutorDecorator selectFrom(String... resourcesNames) {
		return new CcpQueryExecutorDecorator(this, resourcesNames);
	}
	
	public CcpQueryOptions setScrollId(String scrollId) {
		CcpQueryOptions clone = super.putProperty("scroll_id", scrollId);
		return clone;
		
	}
	
	public CcpQueryOptions setSize(int size) {
		CcpQueryOptions clone = super.putProperty("size", size);
		return clone;
	}
	
	public CcpQueryOptions maxResults() {
		CcpQueryOptions clone = super.putProperty("size", 10000);
		return clone;
	}

	public CcpQueryOptions zeroResults() {
		CcpQueryOptions clone = super.putProperty("size", 0);
		return clone;
	}
	

	public CcpQueryOptions setFrom(int from) {
		CcpQueryOptions clone = super.putProperty("from", from);
		return clone;
	}

	public CcpQueryOptions setScrollTime(String scrollTime) {
		CcpQueryOptions clone = super.putProperty("scroll", scrollTime);
		return clone;
	}
	
	public CcpQueryOptions matchAll() {
		CcpQueryOptions clone = super.putProperty("query", CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.match_all, CcpOtherConstants.EMPTY_JSON.content).content);
		return clone;
	}

	
}
