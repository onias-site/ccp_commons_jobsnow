package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;

//TODO TRANSFORMAR ESTA CLASSE EM JSONFIELDNAME E BUSINESS
/**
 * Etapa intermediária do fluent chain gerada por {@link CcpSelectProcedure#ifThisIdIsPresentInEntity}
 * ou {@link CcpSelectProcedure#ifThisIdIsNotPresentInEntity}. Permite definir o que fazer quando a
 * condição de presença for satisfeita: executar uma ação de negócio ou retornar um status de processo.
 */
public class CcpSelectFoundInEntity {
	public static enum JsonFieldNames implements CcpJsonFieldName{
		statements
	}

	private final Collection<CcpJsonRepresentation>	 parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectFoundInEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	/**
	 * Adiciona ao statement atual a ação de negócio a ser executada quando a condição se confirmar;
	 * avança para o próximo passo do fluent chain.
	 *
	 * @param action ação de negócio a executar
	 * @return próximo passo do chain
	 */
	public CcpSelectNextStep executeAction(CcpBusiness action) {
		return this.addStatement("action", action);
	}

	
	
	/**
	 * Adiciona ao statement atual o status de processo a ser retornado/lançado quando a condição se
	 * confirmar; avança para o próximo passo do fluent chain.
	 *
	 * @param status status de processo a retornar
	 * @return próximo passo do chain
	 */
	public CcpSelectNextStep returnStatus(CcpProcessStatus status) {
		return this.addStatement("status", status);
	}

	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> list = this.statements.getAsJsonList(JsonFieldNames.statements);
		CcpJsonRepresentation lastStatement = list.get(list.size() - 1);
		CcpJsonRepresentation put = lastStatement.put(new CcpFieldName(key), obj);
		List<CcpJsonRepresentation> subList = list.subList(0, list.size() - 1);
		subList.add(put);
		CcpJsonRepresentation newStatements = this.statements.put(JsonFieldNames.statements, subList);
		return new CcpSelectNextStep(this.parametersToSearch, newStatements);
	}
	
}
