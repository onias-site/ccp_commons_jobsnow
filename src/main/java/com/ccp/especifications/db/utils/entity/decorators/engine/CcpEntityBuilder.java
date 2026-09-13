package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.especifications.db.utils.entity.CcpEntity;

public interface CcpEntityBuilder {
	CcpEntity getEntity(Class<?> configurationClass, CcpEntity entity);
}
