package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Exceção lançada quando nenhum dos itens da coleção de JSONs fornecida conseguiu produzir um id
 * válido para as entidades informadas, tornando a busca {@code multiGet} inviável. A mensagem lista
 * as entidades e os JSONs usados na tentativa.
 */
@SuppressWarnings("serial")
public class CcpErrorCrudMultiGetSearchUnfeasible extends RuntimeException{

	/**
	 * Inicializa a exceção com mensagem detalhada contendo os metadados das entidades e os JSONs
	 * que não produziram ids válidos.
	 *
	 * @param jsons coleção de JSONs que não geraram ids válidos
	 * @param entities entidades envolvidas na tentativa de busca
	 */
	public CcpErrorCrudMultiGetSearchUnfeasible(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
		super(getMessage(jsons, entities));
	}

	private static String getMessage(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
		
		var entitiesDetails = Arrays.asList(entities)
				.stream().map(entity -> entity.getEntityMetaData()).collect(Collectors.toList());
		
		return "No item in the following list '" + entitiesDetails + "' was able to produce a "
				+ "valid id to searching in the database. The list of items used to form ids to searching: " + jsons + " and ";
	}
	
}
