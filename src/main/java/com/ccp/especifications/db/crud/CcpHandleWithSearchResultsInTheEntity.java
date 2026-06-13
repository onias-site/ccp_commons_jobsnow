package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contrato de callback para tratamento de resultados de busca em uma entidade. Define dois caminhos
 * — registro encontrado e registro não encontrado — e identifica a entidade alvo da busca. É o
 * mecanismo central do padrão "found/not found" utilizado nos handlers bulk e no {@link CcpSelectUnionAll}.
 *
 * @param <T> tipo do resultado retornado pelos callbacks
 */
public interface CcpHandleWithSearchResultsInTheEntity<T> {

	/**
	 * Chamado quando o registro correspondente ao {@code searchParameter} foi encontrado na entidade.
	 *
	 * @param searchParameter parâmetros da busca
	 * @param recordFound dados do registro encontrado
	 * @return resultado do tratamento do registro encontrado
	 */
	T whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound);

	/**
	 * Chamado quando o registro correspondente ao {@code searchParameter} não foi encontrado na entidade.
	 *
	 * @param searchParameter parâmetros da busca
	 * @return resultado do tratamento de registro não encontrado
	 */
	T whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter);

	/**
	 * Retorna a entidade ({@link CcpEntity}) sobre a qual a busca deve ser realizada.
	 *
	 * @return entidade alvo da busca
	 */
	CcpEntity getEntityToSearch();

}
