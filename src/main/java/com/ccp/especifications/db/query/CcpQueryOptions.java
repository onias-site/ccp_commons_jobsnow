package com.ccp.especifications.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Root node (the request) of the Elasticsearch fluent query builder: starts the query and the aggregations, sets
 * pagination, scroll and sorting, and binds the request to the target indexes. Every method returns a copy; start from
 * {@link #INSTANCE}.
 */
public class CcpQueryOptions extends CcpQueryComponent {
	/** Fields of the request. */
	enum JsonFieldNames implements CcpJsonFieldName {
		/** The sorting list. */
		sort,
		/** The query that matches every document. */
		match_all,
		/** The id of a scroll context being continued. */
		scroll_id,
		/** The maximum number of documents returned. */
		size,
		/** The offset of the first document returned. */
		from,
		/** The expiration of the scroll context. */
		scroll,
		/** The query block. */
		query
	}

	/** The empty request, starting point of every query. */
	public static final CcpQueryOptions INSTANCE = new CcpQueryOptions();

	/** Creates an empty request; use {@link #INSTANCE}. */
	private CcpQueryOptions() {
		super(null, "");
	}

	/**
	 * Starts the main query block of the request.
	 */
	public CcpQuery startQuery() {
		CcpQuery query = new CcpQuery(this);
		return query;
	}

	/**
	 * Starts a simplified query without the bool/filter wrapper.
	 */
	public CcpQuerySimplifiedQuery startSimplifiedQuery() {
		CcpQuerySimplifiedQuery simplifiedQuery = new CcpQuerySimplifiedQuery(this);
		return simplifiedQuery;
	}

	/**
	 * Starts the aggregations block of the request.
	 */
	public CcpQueryAggregations startAggregations() {
		CcpQueryAggregations aggregations = new CcpQueryAggregations(this);
		return aggregations;
	}

	/**
	 * Adds ascending sorting by the field.
	 * @param fields the field
	 * @return a copy of the request with the sorting
	 */
	public CcpQueryOptions addAscSorting(String fields) {
		CcpQueryOptions sort = this.addSorting("asc", fields);
		return sort;
	}

	/**
	 * Adds descending sorting by the fields, in order.
	 * @param fields the fields
	 * @return a copy of the request with the sorting
	 */
	public CcpQueryOptions addDescSorting(String... fields) {
		CcpQueryOptions sort = this.addSorting("desc", fields);
		return sort;
	}

	/**
	 * Adds sorting by the fields, in order, with the given direction.
	 * @param sortType {@code asc} or {@code desc}
	 * @param fields the fields
	 * @return a copy of the request with the sorting
	 */
	public CcpQueryOptions addSorting(String sortType, String... fields) {
		CcpQueryOptions sort = this;
		for (String field : fields) {
			sort = sort.sort(field, sortType);
		}
		return sort;
	}

	/**
	 * Appends {@code {fieldName: sortType}} to the sorting list of a copy of the request.
	 * @param fieldName the field
	 * @param sortType {@code asc} or {@code desc}
	 * @return the copy
	 */
	private CcpQueryOptions sort(String fieldName, String sortType) {
		CcpQueryOptions copy = this.copy();
		CcpFieldName fieldKey = new CcpFieldName(fieldName);
		CcpJsonRepresentation sortJson = CcpOtherConstants.EMPTY_JSON.put(fieldKey, sortType);
		Map<String, Object> content = sortJson.getContent();
		List<Object> sortList = Arrays.asList(content);
		boolean hasSort = copy.json.containsAllFields(JsonFieldNames.sort);
		if (hasSort) {
			List<Object> existingSort = copy.json.getAsObjectList(JsonFieldNames.sort);
			sortList = new ArrayList<>(existingSort);
			sortList.add(content);
		}
		copy.json = copy.json.put(JsonFieldNames.sort, sortList);
		return copy;
	}

	/**
	 * Creates an empty request.
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryOptions newInstance = new CcpQueryOptions();
		T typedInstance = (T) newInstance;
		return typedInstance;
	}

	/**
	 * Binds this query to indexes by name (string) and returns an executor ready to use.
	 */
	public CcpQueryExecutorDecorator selectFrom(String... resourcesNames) {
		CcpQueryExecutorDecorator executor = new CcpQueryExecutorDecorator(this, resourcesNames);
		return executor;
	}

	/**
	 * Binds this query to indexes taken from the metadata of the given entities.
	 */
	public CcpQueryExecutorDecorator selectFrom(CcpEntity... entities) {
		String[] resourcesNames = new String[entities.length];
		int resourceIndex = 0;
		for (CcpEntity entity : entities) {
			CcpEntityMetaData entityDetails = entity.getEntityMetaData();
			resourcesNames[resourceIndex++] = entityDetails.entityName;
		}
		CcpQueryExecutorDecorator executor = new CcpQueryExecutorDecorator(this, resourcesNames);
		return executor;
	}

	/**
	 * Sets the scroll ID to continue a paginated iteration.
	 */
	public CcpQueryOptions setScrollId(String scrollId) {
		CcpQueryOptions clone = super.putProperty(JsonFieldNames.scroll_id, scrollId);
		return clone;
	}

	/**
	 * Sets the maximum number of returned documents.
	 */
	public CcpQueryOptions setSize(int size) {
		CcpQueryOptions clone = super.putProperty(JsonFieldNames.size, size);
		return clone;
	}

	/**
	 * Sets the maximum size to 10,000 documents.
	 */
	public CcpQueryOptions maxResults() {
		CcpQueryOptions clone = super.putProperty(JsonFieldNames.size, 10000);
		return clone;
	}

	/**
	 * Sets the size to 0 (useful for queries that return only metadata or aggregations).
	 */
	public CcpQueryOptions zeroResults() {
		CcpQueryOptions clone = super.putProperty(JsonFieldNames.size, 0);
		return clone;
	}

	/**
	 * Sets the offset (offset pagination) of the results.
	 */
	public CcpQueryOptions setFrom(int from) {
		CcpQueryOptions clone = super.putProperty(JsonFieldNames.from, from);
		return clone;
	}

	/**
	 * Sets the expiration time of the scroll context (e.g. "1m", "5m").
	 */
	public CcpQueryOptions setScrollTime(String scrollTime) {
		CcpQueryOptions clone = super.putProperty(JsonFieldNames.scroll, scrollTime);
		return clone;
	}

	/**
	 * Adds a match_all clause that returns every document without filtering.
	 */
	public CcpQueryOptions matchAll() {
		CcpJsonRepresentation matchAllJson = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.match_all, CcpOtherConstants.EMPTY_JSON.content);
		CcpQueryOptions clone = super.putProperty(JsonFieldNames.query, matchAllJson.content);
		return clone;
	}

	/*
	 * Abstract base of every Elasticsearch boolean query operator (must, should, filter, must_not).
	 * Manages the collection of conditions and provides generic methods to add different kinds of filter.
	 */


	/*
	 * Represents the query node within the Elasticsearch fluent query builder.
	 * Serves as the entry point to build the main query block of a request.
	 */


	/*
	 * Represents the bool node within an Elasticsearch boolean query.
	 * It is the central point for composing boolean filters, allowing filter, must, should and must_not clauses to be created.
	 */


	/*
	 * Represents the filter node within an Elasticsearch boolean query.
	 * Unlike must, filter conditions do not affect the relevance score of the documents.
	 */


	/*
	 * Represents the must node within an Elasticsearch boolean query.
	 * The conditions added here are mandatory and affect the relevance score of the returned documents.
	 */


	/*
	 * Represents the must_not node within an Elasticsearch boolean query.
	 * The conditions present here exclude the documents that satisfy them.
	 */


	/*
	 * Represents the should node within an Elasticsearch boolean query.
	 * The conditions added here are optional and increase the relevance score of the documents that satisfy them.
	 * Supports the minimum_should_match parameter to require that at least N conditions are true.
	 */


	/*
	 * Simplified query component that extends {@code CcpQueryBooleanOperator}. Allows building queries
	 * with {@code term}, {@code terms}, {@code match}, {@code matchPhrase}, {@code prefix} and {@code exists} clauses
	 * fluently, returning to the parent {@code CcpQueryOptions} when {@code endSimplifiedQueryAndBackToRequest()} is called.
	 */


	/*
	 * Represents the aggs (aggregations) node in the Elasticsearch fluent query builder.
	 * Allows adding metric aggregations (min, max, avg, sum) and starting buckets (groupings).
	 */


	/*
	 * Represents an Elasticsearch aggregation bucket (terms or histogram) within the fluent query builder.
	 * Allows configuring a grouping by field and size, and ending it by going back to the parent aggregations node.
	 */


	/*
	 * Represents the range node in the Elasticsearch query builder.
	 * Serves as a container for the range definitions of one or more fields, allowing a return to the correct parent context after the definition.
	 */


	/*
	 * Represents the range conditions of a specific field within an Elasticsearch range block.
	 * Allows chaining comparison operators (lt, lte, gt, gte) fluently.
	 */


	/*
	 * Decorator over CcpQueryExecutor that captures the query and the index names in the constructor, simplifying the calls to the real executor.
	 * Each method delegates to the CcpQueryExecutor injected through DI without the caller having to pass these parameters again and again.
	 */

}
