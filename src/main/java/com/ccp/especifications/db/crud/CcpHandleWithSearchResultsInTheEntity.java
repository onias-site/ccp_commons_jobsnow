package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contrato de callback para tratamento de resultados de busca em uma entidade. Define dois
 * caminhos — registro encontrado e registro não encontrado — e identifica a entidade alvo da
 * busca.
 *
 * @param <T> tipo do resultado retornado pelos callbacks
 */
public interface CcpHandleWithSearchResultsInTheEntity<T> {

	T whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound);

	T whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter);

	CcpEntity getEntityToSearch();
}
