package com.ccp.flow;


/**
 * Ponto de entrada da API fluente de controle de fluxo condicional do framework.
 * Permite construir pipelines do tipo "tente executar X; se retornar status Y, execute Z;
 * ao final, encerre o statement". A API modela cenários de fluxo com tratamento de
 * exceções de negócio ({@code CcpErrorFlowDisturb}) de forma declarativa.
 */
public final class CcpTreeFlow {

	/**
	 * Inicia a construção de um statement de fluxo. É o único ponto de entrada;
	 * os demais passos seguem a cadeia fluente.
	 */
	public static CcpBeginThisStatement beginThisStatement() {
		CcpBeginThisStatement ccpBeginThisStatement = new CcpBeginThisStatement();
		return ccpBeginThisStatement;
	}

	/**
	 * Primeiro elo da cadeia fluente de fluxo.
	 * Recebe o processo principal que se deseja tentar executar.
	 */


	/**
	 * Segundo elo da cadeia fluente. Recebe o JSON de entrada que será passado ao processo principal.
	 */


	/**
	 * Terceiro elo da cadeia. Permite declarar o primeiro tratamento condicional:
	 * "mas se esta execução retornar o status X, então...".
	 */


	/**
	 * Quarto elo da cadeia. Associa um status de processo a uma lista de {@code CcpBusiness}
	 * que devem ser executados como tratamento alternativo quando aquele status ocorrer.
	 */


	/**
	 * Quinto elo da cadeia. Permite adicionar mais ramificações condicionais (via {@code and()}) ou encerrar o statement.
	 */


	/**
	 * Sexto e último elo da cadeia fluente de fluxo. Contém a lógica real de execução: tenta executar o processo
	 * principal e, se uma {@code CcpErrorFlowDisturb} for lançada, localiza no mapa de fluxo os processos de
	 * tratamento para aquele status e os executa recursivamente.
	 */

}
