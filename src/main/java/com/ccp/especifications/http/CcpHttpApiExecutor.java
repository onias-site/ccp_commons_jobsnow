package com.ccp.especifications.http;

import com.ccp.business.CcpBusiness;

/** Executor of calls to external HTTP APIs with a configurable retry policy, as a {@link CcpBusiness}. */
public interface CcpHttpApiExecutor extends CcpBusiness{

	/**
	 * Maximum number of attempts of the call.
	 * @return the maximum number of attempts (3 by default)
	 */
	default int getMaxTries() {
		return 3;
	}

	/**
	 * Waiting time between attempts.
	 * @return the waiting time in milliseconds (3000 by default)
	 */
	default int getSleepTimeToRetry() {
		return 3000;
	}
}
