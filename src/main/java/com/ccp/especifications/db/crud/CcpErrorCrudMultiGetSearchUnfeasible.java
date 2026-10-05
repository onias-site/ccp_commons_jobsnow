package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Exception thrown when none of the items of the given JSON collection was able to produce a valid
 * id for the given entities, making the {@code multiGet} search unfeasible.
 */
@SuppressWarnings("serial")
public class CcpErrorCrudMultiGetSearchUnfeasible extends RuntimeException {

	/**
	 * Builds the message listing the entities and the JSONs that could not produce an id.
	 * @param jsons the search parameters
	 * @param entities the entities searched
	 */
	public CcpErrorCrudMultiGetSearchUnfeasible(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
		super(getMessage(jsons, entities));
	}

	/**
	 * Builds the message listing the entities and the JSONs that could not produce an id.
	 * @param jsons the search parameters
	 * @param entities the entities searched
	 * @return the message
	 */
	private static String getMessage(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
		Stream<CcpEntity> entitiesStream = Arrays.asList(entities).stream();
		var entitiesMetaDataStream = entitiesStream.map(entity -> entity.getEntityMetaData());
		var entitiesDetails = entitiesMetaDataStream.collect(Collectors.toList());
		var messageWithEntities = "No item in the following list '" + entitiesDetails;
		var messageWithProduceClause = messageWithEntities + "' was able to produce a ";
		var messageWithItemsLabel = messageWithProduceClause
				+ "valid id to searching in the database. The list of items used to form ids to searching: ";
				var messageWithItems = messageWithItemsLabel + jsons;
				var errorMessage = messageWithItems + " and ";
				return errorMessage;
	}
}
