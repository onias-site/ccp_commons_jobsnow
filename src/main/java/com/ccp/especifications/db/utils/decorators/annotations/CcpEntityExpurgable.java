package com.ccp.especifications.db.utils.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.decorators.engine.CcpEntityExpurgableOptions;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityExpurgable {

	Class<?> expurgableEntityFactory();
	CcpEntityExpurgableOptions expurgTime();

}
