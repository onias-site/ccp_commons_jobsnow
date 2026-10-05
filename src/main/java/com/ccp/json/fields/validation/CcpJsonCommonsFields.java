package com.ccp.json.fields.validation;

import com.ccp.decorators.CcpJsonFieldName;

/**
 * JSON field names shared across the {@code ccp} cost center, that is, the ones that used to be declared in more than one
 * enum spread through the framework modules.
 * <p>
 * It is the centralizer of the lowest layer of the dependency hierarchy ({@code ccp -> jn -> [vis, jb]}): since every
 * module depends on {@code ccp_commons_jobsnow}, any cost center can refer to these constants. Field names restricted to
 * {@code jn}/{@code jb} live in {@code JnJsonCommonsFields} and the ones restricted to {@code vis} in
 * {@code VisJsonCommonsFields}.
 * <p>
 * The constants are declared without validation annotations on purpose: they only unify the key written in the JSON,
 * keeping the behavior the local enums had. Validation rules can be added later, field by field, deliberately.
 */
public enum CcpJsonCommonsFields implements CcpJsonFieldName{

	/* ---- headers and parameters of HTTP calls ---- */
	/** HTTP {@code Accept} header. */
	Accept,
	/** HTTP {@code Authorization} header. */
	Authorization,
	/** An authentication token. */
	token,
	/** The type of something (e.g. of an error returned by the database). */
	type,
	/** A generic value. */
	value,

	/* ---- fields of Elasticsearch responses ---- */
	/** Elasticsearch document id. */
	_id,
	/** Elasticsearch index name. */
	_index,
	/** Elasticsearch scroll context id. */
	_scroll_id,
	/** Elasticsearch document content. */
	_source,
	/** Elasticsearch search hits. */
	hits,
	/** Whether a document was found (Elasticsearch, and presence conditions of the search flow). */
	found,
	/** Result of an Elasticsearch write ({@code created}, {@code updated}, {@code deleted}, {@code not_found}). */
	result,
	/** HTTP status returned by Elasticsearch. */
	ElasticSearchHttpStatus,

	/* ---- results and diagnosis of operations ---- */
	/** An error. */
	error,
	/** Details of an error (message and status of a search flow interruption). */
	errorDetails,
	/** Reason of an error; also the message of a {@code CcpErrorFlowDisturb}. */
	reason,
	/** The statements of a search flow. */
	statements,
	/** Name of a process status. */
	statusName,
	/** Number of a process status. */
	statusNumber,
	/** A field (of an aggregation, of a validation). */
	field,

	/* ---- messaging and session ---- */
	/** E-mail addresses. */
	emails,
	/** Id of a message. */
	message_id,
	/** Success flag of a remote API response. */
	ok,
	/** Id of the message being answered. */
	replyTo,
	/** Session token of a user. */
	sessionToken,
	/** A text (e.g. of a message). */
	text,
	/** A messaging topic. */
	topic,
}
