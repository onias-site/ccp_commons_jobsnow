package com.ccp.especifications.http;

import com.ccp.business.CcpBusiness;

/**
 * Interface para executores de chamadas a APIs HTTP externas com política de retentativa configurável.
 * Estende {@link CcpBusiness} para integração com o fluxo de negócio.
 */
public interface CcpHttpApiExecutor extends CcpBusiness{

	/**
	 * Número máximo de tentativas de execução da chamada HTTP.
	 * @return número máximo de tentativas (padrão: 3)
	 */
	default int getMaxTries() {
		return 3;
	}

	/**
	 * Tempo de espera em milissegundos entre tentativas.
	 * @return tempo de espera em ms (padrão: 3000)
	 */
	default int getSleepTimeToRetry() {
		return 3000;
	}
}
