package com.ccp.especifications.http;

import com.ccp.business.CcpBusiness;

public interface CcpHttpApiExecutor extends CcpBusiness{

	default int getMaxTries() {
		return 3;
	}

	default int getSleepTimeToRetry() {
		return 3000;
	}
}
