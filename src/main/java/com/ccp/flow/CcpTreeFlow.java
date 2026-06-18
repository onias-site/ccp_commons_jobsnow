package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpErrorJsonFieldNotFound;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

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

	/**
	 * Primeiro elo da cadeia fluente de fluxo.
	 * Recebe o processo principal que se deseja tentar executar.
	 */
	public static final class CcpBeginThisStatement {
		protected CcpBeginThisStatement() {
		}

		/**
		 * Registra o processo alvo principal e avança para o próximo passo da cadeia (informar o JSON de entrada).
		 * @param givenFinalTargetProcess o processo principal a ser executado
		 * @return o próximo passo da cadeia fluente
		 */
		public CcpTryToExecuteTheGivenFinalTargetProcess tryToExecuteTheGivenFinalTargetProcess(CcpBusiness givenFinalTargetProcess) {
			return new CcpTryToExecuteTheGivenFinalTargetProcess(givenFinalTargetProcess);
		}
	}

	/**
	 * Segundo elo da cadeia fluente. Recebe o JSON de entrada que será passado ao processo principal.
	 */
	public static final class CcpTryToExecuteTheGivenFinalTargetProcess {
		protected final CcpBusiness givenFinalTargetProcess;

		protected CcpTryToExecuteTheGivenFinalTargetProcess(CcpBusiness givenFinalTargetProcess) {
			this.givenFinalTargetProcess = givenFinalTargetProcess;
		}

		/**
		 * Registra o JSON de entrada e avança para o passo onde se declaram os tratamentos condicionais por status.
		 * @param givenJson o JSON de entrada para o processo principal
		 * @return o próximo passo da cadeia fluente
		 */
		public CcpUsingTheGivenJson usingTheGivenJson(CcpJsonRepresentation givenJson) {
			return new CcpUsingTheGivenJson(this.givenFinalTargetProcess, givenJson);
		}
	}

	/**
	 * Terceiro elo da cadeia. Permite declarar o primeiro tratamento condicional:
	 * "mas se esta execução retornar o status X, então...".
	 */
	public static final class CcpUsingTheGivenJson {
		protected final CcpBusiness givenFinalTargetProcess;
		protected final CcpJsonRepresentation givenJson;

		protected CcpUsingTheGivenJson(CcpBusiness givenFinalTargetProcess, CcpJsonRepresentation givenJson) {
			this.givenFinalTargetProcess = givenFinalTargetProcess;
			this.givenJson = givenJson;
		}

		/**
		 * Registra um status de processo a ser tratado e avança para declarar quais processos executar naquele caso.
		 * @param processStatus o status que dispara o tratamento alternativo
		 * @return o próximo passo da cadeia fluente
		 */
		public CcpIfThisExecutionReturns butIfThisExecutionReturns(CcpProcessStatus processStatus) {
			return new CcpIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, CcpOtherConstants.EMPTY_JSON);
		}
	}

	/**
	 * Quarto elo da cadeia. Associa um status de processo a uma lista de {@code CcpBusiness}
	 * que devem ser executados como tratamento alternativo quando aquele status ocorrer.
	 */
	public static final class CcpIfThisExecutionReturns {
		protected final CcpBusiness givenFinalTargetProcess;
		protected final CcpJsonRepresentation givenJson;
		protected final CcpProcessStatus processStatus;
		protected final CcpJsonRepresentation flow;

		protected CcpIfThisExecutionReturns(CcpBusiness givenFinalTargetProcess,
				CcpJsonRepresentation givenJson, CcpProcessStatus processStatus, CcpJsonRepresentation flow) {
			this.givenFinalTargetProcess = givenFinalTargetProcess;
			this.processStatus = processStatus;
			this.givenJson = givenJson;
			this.flow = flow;
		}

		/**
		 * Registra os processos de tratamento para o status declarado e avança para o passo de encadeamento adicional.
		 * @param givenProcess os processos a executar quando o status ocorrer
		 * @return o próximo passo da cadeia fluente
		 */
		public CcpExecuteTheGivenProcess thenExecuteTheGivenProcesses(CcpBusiness... givenProcess) {
			CcpJsonRepresentation nextFlow = this.flow.put(this.processStatus, givenProcess);
			return new CcpExecuteTheGivenProcess(this.givenFinalTargetProcess, this.givenJson, nextFlow);
		}
	}

	/**
	 * Quinto elo da cadeia. Permite adicionar mais ramificações condicionais (via {@code and()}) ou encerrar o statement.
	 */
	public static final class CcpExecuteTheGivenProcess {
		protected final CcpBusiness givenFinalTargetProcess;
		protected final CcpJsonRepresentation givenJson;
		protected final CcpJsonRepresentation flow;

		public CcpExecuteTheGivenProcess(CcpBusiness givenFinalTargetProcess,
				CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {
			this.givenFinalTargetProcess = givenFinalTargetProcess;
			this.givenJson = givenJson;
			this.flow = flow;
		}

		public CcpAndIfThisExecutionReturns and() {
			return new CcpAndIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, this.flow);
		}
	}

	/**
	 * Sexto e último elo da cadeia fluente de fluxo. Contém a lógica real de execução: tenta executar o processo
	 * principal e, se uma {@code CcpErrorFlowDisturb} for lançada, localiza no mapa de fluxo os processos de
	 * tratamento para aquele status e os executa recursivamente.
	 */
	public static final class CcpAndIfThisExecutionReturns {
		protected final CcpBusiness givenFinalTargetProcess;
		protected final CcpJsonRepresentation givenJson;
		protected final CcpJsonRepresentation flow;

		protected CcpAndIfThisExecutionReturns(CcpBusiness givenFinalTargetProcess,
				CcpJsonRepresentation givenJson, CcpJsonRepresentation flow) {
			this.givenFinalTargetProcess = givenFinalTargetProcess;
			this.givenJson = givenJson;
			this.flow = flow;
		}

		/**
		 * Adiciona mais uma ramificação condicional ao fluxo (volta ao quarto elo da cadeia).
		 * @param processStatus o status a ser tratado
		 */
		public CcpIfThisExecutionReturns ifThisExecutionReturns(CcpProcessStatus processStatus) {
			return new CcpIfThisExecutionReturns(this.givenFinalTargetProcess, this.givenJson, processStatus, this.flow);
		}

		/**
		 * Executa o processo principal. Se bem-sucedido, executa também os processos {@code whatToNext} e retorna o resultado.
		 * Se uma {@code CcpErrorFlowDisturb} for lançada, localiza os processos registrados para o status da exceção,
		 * os executa como tratamento e repete recursivamente até não haver mais ramificações a tratar.
		 * @param whatToNext processos a executar após o sucesso
		 */
		public CcpJsonRepresentation endThisStatement(CcpBusiness... whatToNext) {
			try {
				CcpJsonRepresentation responseWhenTheFlowPerformsNormally = this.tryToPerformNormally(whatToNext);
				return responseWhenTheFlowPerformsNormally;
			} catch (CcpErrorFlowDisturb e) {
				CcpJsonRepresentation json = this.tryToFixTheFlow(e);
				CcpJsonRepresentation remainingFlow = this.flow.removeFields(e.status);
				CcpAndIfThisExecutionReturns andIfThisExecutionReturns = new CcpAndIfThisExecutionReturns(this.givenFinalTargetProcess, json, remainingFlow);
				CcpJsonRepresentation endThisStatement = andIfThisExecutionReturns.endThisStatement(whatToNext);
				return endThisStatement;
			}
		}

		private CcpJsonRepresentation tryToPerformNormally(CcpBusiness... whatToNext) {
			CcpJsonRepresentation responseWhenTheFlowPerformsNormally = this.givenFinalTargetProcess.apply(this.givenJson);
			for (CcpBusiness function : whatToNext) {
				function.apply(this.givenJson);
			}
			return responseWhenTheFlowPerformsNormally;
		}

		private CcpJsonRepresentation tryToFixTheFlow(CcpErrorFlowDisturb e) {
			try {
				CcpBusiness[] nextFlows = this.flow.getAsObject(e.status);
				CcpJsonRepresentation json = this.givenJson;
				for (CcpBusiness nextFlow : nextFlows) {
					try {
						json = nextFlow.apply(json);
					} catch (CcpErrorFlowDisturb flowDisturb) {
						json = flowDisturb.json.mergeWithAnotherJson(json);
					}
				}
				return json;
			} catch (CcpErrorJsonFieldNotFound ex) {
				throw ex;
			}
		}
	}
}
