package com.ccp.especifications.db.utils.decorators.engine;

import com.ccp.especifications.db.utils.CcpEntity;

public interface CcpEntityExpurgableFactory {

	CcpEntity getEntity(CcpEntity entity, CcpEntityExpurgableOptions timeOption);
	
}
