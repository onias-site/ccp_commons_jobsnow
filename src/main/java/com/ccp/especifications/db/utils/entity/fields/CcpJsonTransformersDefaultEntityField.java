package com.ccp.especifications.db.utils.entity.fields
;

import com.ccp.business.CcpBusiness;

public interface CcpJsonTransformersDefaultEntityField extends CcpBusiness{
	boolean canBePrimaryKey();
	String name();
}
