package com.ccp.json.fields.validation;

import com.ccp.decorators.CcpJsonFieldName;

/**
 * Centraliza os nomes de campo JSON comuns ao centro de custo {@code ccp}, isto é, aqueles que
 * apareciam declarados em mais de um enum espalhado pelos módulos do framework.
 *
 * <p>É o centralizador da camada mais baixa da hierarquia de dependências
 * ({@code ccp -> jn -> [vis, jb]}): como todo módulo depende de {@code ccp_commons_jobsnow},
 * qualquer centro de custo pode referenciar estas constantes. Nomes de campo restritos a
 * {@code jn}/{@code jb} ficam em {@code JnJsonCommonsFields} e os restritos a {@code vis} em
 * {@code VisJsonCommonsFields}.
 *
 * <p>As constantes são declaradas sem anotações de validação de propósito: elas apenas unificam o
 * nome da chave gravada no JSON, preservando o comportamento que os enums locais tinham. Regras de
 * validação podem ser acrescentadas depois, campo a campo, de forma deliberada.
 */
public enum CcpJsonCommonsFields implements CcpJsonFieldName{

	/* ---- cabeçalhos e parâmetros de chamadas HTTP ---- */
	Accept,
	Authorization,
	token,
	type,
	value,

	/* ---- campos de resposta do Elasticsearch ---- */
	_id,
	_index,
	_scroll_id,
	_source,
	hits,
	found,
	result,
	ElasticSearchHttpStatus,

	/* ---- resultado e diagnóstico de operações ---- */
	error,
	errorDetails,
	reason,
	statements,
	statusName,
	statusNumber,
	field,

	/* ---- mensageria e sessão ---- */
	emails,
	message_id,
	ok,
	replyTo,
	sessionToken,
	text,
	topic,
}
