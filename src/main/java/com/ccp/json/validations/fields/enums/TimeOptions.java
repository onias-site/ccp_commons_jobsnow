package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;

enum TimeOptions{
	_before {
		long subtractNumber(long time) {
			long currentTimeMillis = System.currentTimeMillis();
			long currentTimeMillisMenos = currentTimeMillis - time;
			return currentTimeMillisMenos;
		}
	},
	_after {
		long subtractNumber(long time) {
			long currentTimeMillis = System.currentTimeMillis();
			long timeMenos = time - currentTimeMillis;
			return timeMenos;
		}
	}
	;
	abstract long subtractNumber(long time);

	public Long getEnlapsedTime(CcpJsonRepresentation json, Field field) {
		String fieldName = field.getName();
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName);

		Long time = json.getAsLongNumber(ccpFieldName);
		;
		long providedValue = this.subtractNumber(time);
		
		return providedValue;
	}
	
	public Long getEnlapsedInterval(CcpJsonRepresentation json, Field field) {
		Long enlapsedTime = this.getEnlapsedTime(json, field);
		CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
		var intervalType = annotation.intervalType();
		long currentTimeMillis2 = System.currentTimeMillis();
		var milliseconds = intervalType.getMilliseconds(currentTimeMillis2);
		var enlapsedTimeDividido = enlapsedTime / milliseconds;
		long enlapsedInterval = (enlapsedTimeDividido) + 1;
		return enlapsedInterval;
	}
}
