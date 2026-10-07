package com.ccp.especifications.db.query;

import com.ccp.decorators.CcpJsonFieldName;

/**
 * Types of condition of an Elasticsearch boolean query. The name of each item is the key that opens the condition in
 * the query, as in {@code {"term": {field: value}}}.
 */
public enum CcpQueryConditionType implements CcpJsonFieldName {
	/** Exact value. */
	term,
	/** Any of the values. */
	terms,
	/** Value starting with a prefix. */
	prefix,
	/** Full-text search. */
	match,
	/** Full-text search of a whole phrase. */
	match_phrase,
	/** The field exists in the document. */
	exists
}
