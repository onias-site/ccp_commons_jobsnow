package com.ccp.exceptions.db.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

@SuppressWarnings("serial")
public class CcpMultiGetSearchUnfeasible extends RuntimeException{

	public CcpMultiGetSearchUnfeasible(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
		super(getMessage(jsons, entities));
	}

	private static String getMessage(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
		
		List<CcpJsonRepresentation> entitiesDetails = Arrays.asList(entities)
				.stream().map(entity -> entity.getEntityDetails()).collect(Collectors.toList());
		
		return "No item in the following list '" + entitiesDetails + "' was able to produce a "
				+ "valid id to searching in the database. The list of items used to form ids to searching: " + jsons + " and ";
	}
	
}
