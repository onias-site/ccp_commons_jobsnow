package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

/**
 * Etapa intermediária do fluent chain que aparece após a definição de uma ação ou status em um
 * statement. Permite continuar adicionando condições ao fluxo ou encerrar a cadeia especificando
 * os campos de retorno.
 */
public class CcpSelectNextStep {
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectNextStep(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}
	
	/**
	 * Encerra o chain convertendo a coleção de nomes de campos para array e criando o {@link CcpSelectFinally}.
	 *
	 * @param fields coleção de campos a incluir no resultado final
	 * @return etapa final do fluent chain
	 */
	public CcpSelectFinally andFinallyReturningTheseFields(Collection<CcpJsonFieldName> fields) {
		CcpJsonFieldName[] array = fields.toArray(new CcpJsonFieldName[fields.size()]);
		CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.parametersToSearch, this.statements, array);
		return ccpSelectFinally;
	}
	
	
	/**
	 * Versão varargs do método acima; encerra o chain criando o {@link CcpSelectFinally} com os campos informados.
	 *
	 * @param fields campos a incluir no resultado final
	 * @return etapa final do fluent chain
	 */
	public CcpSelectFinally andFinallyReturningTheseFields(CcpJsonFieldName... fields) {
		CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.parametersToSearch, this.statements, fields);
		return ccpSelectFinally;
	}
	
	/**
	 * Retorna ao {@link CcpSelectProcedure} para adicionar mais condições ao fluxo sem encerrá-lo.
	 *
	 * @return o {@link CcpSelectProcedure} atual
	 */
	public CcpSelectProcedure and() {
		CcpSelectProcedure ccpSelectProcedure = new CcpSelectProcedure(this.parametersToSearch, this.statements);
		return ccpSelectProcedure;
	}
	
}
