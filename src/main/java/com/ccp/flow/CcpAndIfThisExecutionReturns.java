package com.ccp.flow;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpErrorJsonFieldNotFound;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

/**
 * Sexto e último elo da cadeia fluente de fluxo. Contém a lógica real de execução: tenta executar o processo
 * principal e, se uma {@code CcpErrorFlowDisturb} for lançada, localiza no mapa de fluxo os processos de
 * tratamento para aquele status e os executa recursivamente.
 */
public final class CcpAndIfThisExecutionReturns {
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


	private CcpJsonRepresentation tryToPerformNormally(
			CcpBusiness... whatToNext) {
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
