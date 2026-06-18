package com.ccp.process;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.flow.CcpErrorFlowDisturb;

/**
 * Contrato para representação de status de processo (análogo a códigos HTTP de resposta). Estende {@code CcpJsonFieldName},
 * permitindo uso direto em JSONs como chave de campo. Oferece métodos de verificação de status para uso em testes e de
 * lançamento de exceção de fluxo.
 */
public interface CcpProcessStatus extends CcpJsonFieldName{
	/** Retorna o código numérico do status (ex.: 200, 404, 409). */
	int asNumber();

	/**
	 * Compara {@code actualStatus} com o esperado; retorna o nome do status se correto ou lança
	 * {@code RuntimeException} com mensagem descritiva se incorreto. Usado em testes de integração.
	 * @param actualStatus o código de status recebido
	 * @param message mensagem adicional para diagnóstico
	 */
	default String verifyStatus(int actualStatus, String message) {
		int expectedStatus = this.asNumber();
		
		boolean correctStatus = expectedStatus == actualStatus;
		
		String testName = this.name();
		
		if(correctStatus) {
			return testName;
		}
		throw new UnexpectedProcessStatus(message, testName, expectedStatus, actualStatus);
	}
	
	/**
	 * Verifica tanto o código numérico quanto o nome do status. Retorna {@code this} se correto ou lança
	 * {@code RuntimeException} se houver divergência.
	 * @param actualStatus o código de status recebido
	 * @param actualStatusName o nome do status recebido
	 */
	default CcpProcessStatus verifyStatusNames(int actualStatus, String actualStatusName) {
		String expectedStatusName = this.verifyStatus(actualStatus, "");
		
		boolean empty = actualStatusName.trim().isEmpty();
		if(empty) {
			return this;
		}
		
		boolean correctStatusNumberAndCorrectStatusName = actualStatusName.equals(expectedStatusName);
		
		if(correctStatusNumberAndCorrectStatusName) {
			return this;
		}
		throw new UnexpectedProcessStatus(expectedStatusName, actualStatusName);
	}
	
	
	/**
	 * Lança uma {@code CcpErrorFlowDisturb} com o JSON fornecido e este status.
	 * Usado para interromper o fluxo de forma controlada.
	 * @param json o JSON de contexto da exceção
	 */
	default CcpJsonRepresentation throwException(CcpJsonRepresentation json) {
		throw new CcpErrorFlowDisturb(json, this);
	}

	@SuppressWarnings("serial")
	public static class UnexpectedProcessStatus extends RuntimeException {
		private UnexpectedProcessStatus(String message, String testName, int expectedStatus, int actualStatus) {
			super(String.format("In the test '%s' it was expected the status '%s', but status '%s' was received. Message: " + message, testName, expectedStatus, actualStatus));
		}
		private UnexpectedProcessStatus(String expectedStatusName, String actualStatusName) {
			super(String.format("It was expected the status name '%s' but status name '%s' was received insted", expectedStatusName, actualStatusName));
		}
	}
}
