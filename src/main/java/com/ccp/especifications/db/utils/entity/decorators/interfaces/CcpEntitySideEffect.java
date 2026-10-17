package com.ccp.especifications.db.utils.entity.decorators.interfaces;

import java.lang.annotation.Annotation;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationPhase;

/**
 * What a side effect annotation of an entity does once its write or transfer happens (running businesses, sending a
 * message to the user): the decorator of the annotation decides when, this decides what.
 */
public interface CcpEntitySideEffect {

	/**
	 * Runs the side effect configured by the annotation.
	 * @param json the record
	 * @param annotation the annotation instance found on the configurator class
	 * @param phase the phase of the side effect, which tells whether a handled failure cancels the operation
	 * @return the JSON produced by the side effect
	 */
	CcpJsonRepresentation execute(CcpJsonRepresentation json, Annotation annotation, CcpEntityOperationPhase phase);
}
