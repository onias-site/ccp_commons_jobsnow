package com.ccp.especifications.db.utils;

import com.ccp.especifications.mensageria.receiver.CcpBusiness;

public interface CcpJsonTransformersDefaultEntityField extends CcpBusiness{
	boolean canBePrimaryKey();
	String name();
}
