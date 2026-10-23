package com.ccp.especifications.db.bulk;

import java.util.function.Function;

/** Handler of the {@code NOT_FOUND} of {@link CcpBulkEntityOperationType#delete}: throws {@link CcpErrorBulkEntityRecordNotFound} for the item. */
public class CcpBulkItemThrowNotFound implements Function<CcpBulkItem, CcpBulkItem> {

	/** The single instance. */
	public static final CcpBulkItemThrowNotFound INSTANCE = new CcpBulkItemThrowNotFound();

	private CcpBulkItemThrowNotFound() {}

	/**
	 * Throws the error of the item not found.
	 * @param x the item whose record was not found
	 * @return never returns
	 */
	public CcpBulkItem apply(CcpBulkItem x) {
		throw new CcpErrorBulkEntityRecordNotFound(x.entity, x.json);
	}
}
