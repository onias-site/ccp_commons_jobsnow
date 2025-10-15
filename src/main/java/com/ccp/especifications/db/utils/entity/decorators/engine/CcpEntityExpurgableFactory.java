package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.especifications.db.utils.entity.CcpEntity;

public interface CcpEntityExpurgableFactory {

	CcpEntity getEntity(CcpEntity entity, CcpEntityExpurgableOptions timeOption);
	
}
