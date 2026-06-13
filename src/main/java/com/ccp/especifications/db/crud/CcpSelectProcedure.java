package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
/**
 * Núcleo do fluent API de busca. Permite declarar de forma encadeada e legível quais entidades
 * consultar, quais condições de presença verificar e quais ações executar em cada caso. Cada
 * chamada adiciona um statement à lista interna, que será processada em bloco pelo {@link CcpSelectFinally}.
 */
public class CcpSelectProcedure {
	enum JsonFieldNames implements CcpJsonFieldName{
		statements, entity, found
	}
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;
	
	CcpSelectProcedure(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	/**
	 * Adiciona um statement para carregar os dados do id na entidade informada; retorna a etapa de
	 * continuação correspondente.
	 *
	 * @param entity entidade cujos dados devem ser carregados
	 * @return etapa para continuar o chain após o carregamento
	 */
	public CcpSelectLoadDataFromEntity loadThisIdFromEntity(CcpEntity entity) {
		CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.entity, entity));
		return new CcpSelectLoadDataFromEntity(this.parametersToSearch, addToList);
	}

	/**
	 * Adiciona um statement condicional com {@code found = true}; o bloco seguinte define o que
	 * fazer se o registro <b>existir</b> na entidade.
	 *
	 * @param entity entidade a verificar
	 * @return etapa para definir a ação quando o registro for encontrado
	 */
	public CcpSelectFoundInEntity ifThisIdIsPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.found, true).put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, put);
		return new CcpSelectFoundInEntity(this.parametersToSearch, addToList);
	}
	
	/**
	 * Adiciona um statement condicional com {@code found = false}; o bloco seguinte define o que
	 * fazer se o registro <b>não existir</b> na entidade.
	 *
	 * @param entity entidade a verificar
	 * @return etapa para definir a ação quando o registro não for encontrado
	 */
	public CcpSelectFoundInEntity ifThisIdIsNotPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.found, false).put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, put);
		return new CcpSelectFoundInEntity(this.parametersToSearch, addToList);
	}
	
	/**
	 * Adiciona um statement de ação livre (não vinculado a presença em entidade) que será executado
	 * sempre, recebendo o JSON acumulado até aquele ponto.
	 *
	 * @param action ação de negócio a executar
	 * @return próximo passo do chain
	 */
	public CcpSelectNextStep executeAction(CcpBusiness action) {
		return this.addStatement("action", action);
	}
	
	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> list = this.statements.getAsJsonList(JsonFieldNames.statements);
		list.add(CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(key), obj));
		CcpJsonRepresentation newStatements = this.statements.put(JsonFieldNames.statements, list);
		return new CcpSelectNextStep(this.parametersToSearch, newStatements);
	}

 }
