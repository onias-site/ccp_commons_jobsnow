package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.Collection;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Ponto de entrada do fluent API de busca. Recebe um ou mais {@link CcpJsonRepresentation} como
 * parâmetros de busca e inicia a construção de um {@link CcpSelectProcedure}, permitindo encadear
 * condições de busca de forma legível.
 */
public class CcpGetEntityId {

	private final Collection<CcpJsonRepresentation> parametersToSearch;

	/**
	 * Recebe os parâmetros de busca (JSONs) que serão usados nas consultas ao banco ao longo do fluent chain.
	 *
	 * @param parametersToSearch parâmetros de busca
	 */
	public CcpGetEntityId(CcpJsonRepresentation... parametersToSearch) {
		this.parametersToSearch = Arrays.asList(parametersToSearch);
	}
	/**
	 * Inicializa e retorna um {@link CcpSelectProcedure} com os parâmetros de busca fornecidos e um
	 * JSON de statements vazio, dando início ao encadeamento de condições de busca.
	 *
	 * @return novo {@link CcpSelectProcedure} inicializado
	 */
	public CcpSelectProcedure toBeginProcedureAnd() {
		return new CcpSelectProcedure(this.parametersToSearch, CcpOtherConstants.EMPTY_JSON);
	}


}
