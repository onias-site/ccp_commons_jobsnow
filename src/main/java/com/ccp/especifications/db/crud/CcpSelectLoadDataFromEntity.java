package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

/**
 * Etapa intermediária do fluent chain gerada por {@link CcpSelectProcedure#loadThisIdFromEntity}.
 * Representa o ponto após declarar que os dados de uma entidade devem ser carregados, oferecendo
 * duas saídas: adicionar mais condições ao fluxo ou finalizar declarando os campos de retorno.
 */
public class CcpSelectLoadDataFromEntity {
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectLoadDataFromEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;

	}

	/**
	 * Retorna ao {@link CcpSelectProcedure} para adicionar mais condições ou entidades ao fluxo de busca.
	 *
	 * @return o {@link CcpSelectProcedure} atual
	 */
	public CcpSelectProcedure and() {
		return new CcpSelectProcedure(this.parametersToSearch, this.statements);
	}
	
	/**
	 * Encerra a declaração de statements e inicia a etapa final ({@link CcpSelectFinally})
	 * especificando quais campos devem compor o resultado retornado.
	 *
	 * @param fields campos a incluir no resultado final
	 * @return etapa final do fluent chain
	 */
	public CcpSelectFinally andFinally(CcpJsonFieldName... fields) {
		return new CcpSelectFinally(this.parametersToSearch, this.statements, fields);
	}
	


}
