package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Exceção lançada quando nenhum dos itens da coleção de JSONs fornecida conseguiu produzir um id
 * válido para as entidades informadas, tornando a busca {@code multiGet} inviável.
 */
@SuppressWarnings("serial")
public class CcpErrorCrudMultiGetSearchUnfeasible extends RuntimeException {

	public CcpErrorCrudMultiGetSearchUnfeasible(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
		super(getMessage(jsons, entities));
	}

	private static String getMessage(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
		Stream<CcpEntity> stream = Arrays.asList(entities).stream();
		var streamMap = stream.map(entity -> entity.getEntityMetaData());
		var entitiesDetails = streamMap.collect(Collectors.toList());
		var valorMais = "No item in the following list '" + entitiesDetails;
		var valorMaisMais = valorMais + "' was able to produce a ";
		var valorMaisMaisMais = valorMaisMais
				+ "valid id to searching in the database. The list of items used to form ids to searching: ";
				var valorMaisMaisMaisMais = valorMaisMaisMais + jsons;
				var valorMaisMaisMaisMaisMais = valorMaisMaisMaisMais + " and ";
				return valorMaisMaisMaisMaisMais;
	}
}
