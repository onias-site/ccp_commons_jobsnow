package com.ccp.especifications.db.utils.entity.decorators.enums;

/** When the side effects of an entity operation run, relative to the operation itself. */
public enum CcpEntityOperationPhase {
	/** After the operation. */
	_after,
	/** Before the operation. */
	_before
}
