package com.ccp.especifications.email;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpEmailSender {

	CcpJsonRepresentation send(CcpJsonRepresentation emailApiParameters) ;
}
