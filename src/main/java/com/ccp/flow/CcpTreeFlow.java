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
		return new CcpBeginThisStatement();
	}
	
}
