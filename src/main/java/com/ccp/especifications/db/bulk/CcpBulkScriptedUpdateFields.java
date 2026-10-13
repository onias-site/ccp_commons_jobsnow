package com.ccp.especifications.db.bulk;

import com.ccp.decorators.CcpJsonFieldName;

/**
 * Fields of the json of a {@link CcpBulkEntityOperationType#scriptedUpdate} item: what the database runs over the
 * stored record. The script language is the one of the database implementation (Painless, in Elasticsearch).
 */
public enum CcpBulkScriptedUpdateFields implements CcpJsonFieldName {
	/** The source of the script; it reads the stored record in {@code ctx._source} and its parameters in {@code params}. */
	script,
	/** The parameters of the script (a json). */
	params,
	/** The record created as is when there is none yet (the script does not run then); may be empty. */
	upsert,
	/**
	 * When {@code true}, the script also runs when there is no record, over the {@code upsert}: it may then decide not
	 * to create anything ({@code ctx.op = 'none'}).
	 */
	scriptedUpsert
}
