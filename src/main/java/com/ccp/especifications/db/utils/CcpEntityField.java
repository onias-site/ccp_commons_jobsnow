package com.ccp.especifications.db.utils;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public interface CcpEntityField extends CcpJsonFieldName {

	boolean isPrimaryKey();
	
	Function<CcpJsonRepresentation, CcpJsonRepresentation> getTransformer();
	
	CcpEntityField TIMESTAMP = new CcpEntityField() {
		
		public String name() {
			return "timestamp";
		}
		
		public boolean isPrimaryKey() {
			return false;
		}
		
		public Function<CcpJsonRepresentation, CcpJsonRepresentation> getTransformer() {
			return CcpOtherConstants.DO_NOTHING;
		}
	};

	CcpEntityField DATE = new CcpEntityField() {

		public Function<CcpJsonRepresentation, CcpJsonRepresentation> getTransformer() {
			return CcpOtherConstants.DO_NOTHING;
		}
		
		public String name() {
			return "date";
		}
		
		public boolean isPrimaryKey() {
			return false;
		}
	};
}
