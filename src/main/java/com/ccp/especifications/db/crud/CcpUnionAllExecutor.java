package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

public interface CcpUnionAllExecutor {
	CcpSelectUnionAll unionAll(Collection<CcpJsonRepresentation> values, CcpEntity... entities);

}
