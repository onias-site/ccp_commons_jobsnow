package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contrato para execução de consultas do tipo UNION ALL no banco de dados. Permite buscar registros de
 * múltiplas entidades em uma única operação, retornando um {@code CcpSelectUnionAll} com os resultados.
 */
public interface CcpUnionAllExecutor {
	/**
	 * Executa uma consulta UNION ALL sobre as {@code entities} fornecidas, usando os {@code values} como filtros,
	 * e retorna os resultados agregados.
	 */
	CcpSelectUnionAll unionAll(Collection<CcpJsonRepresentation> values, CcpEntity... entities);

}
