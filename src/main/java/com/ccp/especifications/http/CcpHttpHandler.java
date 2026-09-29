package com.ccp.especifications.http;

import java.util.List;
import java.util.Set;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;


/**
 * Orquestrador de execução de requisições HTTP. Combina {@link CcpHttpRequester} com um mapa de
 * fluxos (status → lógica de negócio), executando a lógica correspondente ao status recebido
 * ou lançando {@link CcpErrorHttp} quando o status não é mapeado.
 */
public final class CcpHttpHandler {

	private final String url;
	private final CcpJsonRepresentation flows;
	private final CcpBusiness alternativeFlow;
	/**
	 * Diz se status não mapeado tem para onde ir. Até 2026-09-27 a ausência de fluxo alternativo era
	 * representada por {@code alternativeFlow = null}; quando o aspecto que proíbe {@code null} entrou
	 * (commit cd97ba0, 2026-07-31), o valor virou {@code DO_NOTHING} e todo status inesperado — 400, 404,
	 * 500 — passou a ser tratado como sucesso, com o json de erro do servidor no lugar da resposta.
	 */
	private final boolean hasAlternativeFlow;
	public final CcpHttpRequester ccpHttp = CcpDependencyInjection.getDependency(CcpHttpRequester.class);

	/**
	 * Cria handler com mapa explícito de fluxos (status → lógica de negócio). Status fora do mapa lança
	 * {@link CcpErrorHttp}.
	 * @param flows mapa de status HTTP para fluxos de negócio
	 * @param url URL alvo das requisições
	 */
	public CcpHttpHandler(CcpJsonRepresentation flows, String url) {
		this.alternativeFlow = CcpOtherConstants.DO_NOTHING;
		this.hasAlternativeFlow = false;
		this.flows = flows;
		this.url = url;

	}

	/**
	 * Cria handler com um status mapeado para fluxo alternativo.
	 * @param httpStatus status HTTP aceito
	 * @param alternativeFlow fluxo de negócio alternativo para o status informado
	 * @param url URL alvo das requisições
	 */
	public CcpHttpHandler(Integer httpStatus, CcpBusiness alternativeFlow, String url) {
		this.flows = CcpOtherConstants.EMPTY_JSON.addJsonTransformer(httpStatus, CcpOtherConstants.DO_NOTHING);
		this.alternativeFlow = alternativeFlow;
		this.hasAlternativeFlow = true;
		this.url = url;
	}

	/**
	 * Cria handler com um único status aceito. Qualquer outro status lança {@link CcpErrorHttp}.
	 * @param httpStatus status HTTP aceito
	 * @param url URL alvo das requisições
	 */
	public CcpHttpHandler(Integer httpStatus, String url) {
		this.flows = CcpOtherConstants.EMPTY_JSON.addJsonTransformer(httpStatus, CcpOtherConstants.DO_NOTHING);
		this.alternativeFlow = CcpOtherConstants.DO_NOTHING;
		this.hasAlternativeFlow = false;
		this.url = url;
	}
	
	
	/**
	 * Executa GET simples sem headers nem corpo.
	 * @param trace identificador de rastreamento da requisição
	 * @param transformer transformador da resposta para o tipo desejado
	 * @return resultado transformado da resposta
	 */
	public <V> V executeHttpSimplifiedGet(String trace, CcpHttpResponseTransform<V> transformer) {
		V executeHttpRequest = this.executeHttpRequest(trace, CcpHttpMethods.GET, CcpOtherConstants.EMPTY_JSON, CcpOtherConstants.EMPTY_JSON, transformer);
		return executeHttpRequest;
	}
	
	/**
	 * Executa requisição com corpo JSON.
	 * @param trace identificador de rastreamento
	 * @param method método HTTP
	 * @param headers cabeçalhos da requisição
	 * @param body corpo da requisição como JSON
	 * @param transformer transformador da resposta
	 * @return resultado transformado da resposta
	 */
	public <V> V executeHttpRequest(String trace, CcpHttpMethods method, CcpJsonRepresentation headers, CcpJsonRepresentation body, CcpHttpResponseTransform<V> transformer) {
		
		String asJson = body.asUgglyJson();
		V executeHttpRequest = this.executeHttpRequest(trace, method, headers, asJson, transformer);
		return executeHttpRequest;
	}

	/**
	 * Executa requisição com corpo String.
	 * @param trace identificador de rastreamento
	 * @param method método HTTP
	 * @param headers cabeçalhos da requisição
	 * @param request corpo da requisição como String
	 * @param transformer transformador da resposta
	 * @return resultado transformado da resposta
	 */
	public <V>V executeHttpRequest(String trace,  CcpHttpMethods method, CcpJsonRepresentation headers, String request, CcpHttpResponseTransform<V> transformer) {
		
		CcpHttpResponse response = this.ccpHttp.executeHttpRequest(this.url, method, headers, request);
	
		V executeHttpRequest = this.executeHttpRequest(trace, method, headers, request, transformer, response);
		
		return executeHttpRequest;
	}

	/**
	 * Executa requisição multipart com partes textuais e binárias.
	 * @param trace identificador de rastreamento
	 * @param method método HTTP
	 * @param headers cabeçalhos da requisição
	 * @param texts partes textuais do multipart
	 * @param binaries partes binárias do multipart
	 * @param transformer transformador da resposta
	 * @return resultado transformado da resposta
	 */
	public <V>V executeMultiPartHttpRequest(String trace, CcpHttpMethods method, CcpJsonRepresentation headers, List<CcpHttpBodyText> texts, List<CcpHttpBodyBinary> binaries, CcpHttpResponseTransform<V> transformer) {
		
		CcpHttpResponse response = this.ccpHttp.executeMultiPartHttpRequest(url, method, headers, texts, binaries);
	
		V executeHttpRequest = this.executeHttpRequest(trace, method, headers, "", transformer, response);
		
		return executeHttpRequest;
	}
	
	/**
	 * Versão completa que recebe resposta já obtida, seleciona fluxo pelo status e executa o {@link CcpBusiness}.
	 * @param trace identificador de rastreamento
	 * @param method método HTTP utilizado
	 * @param headers cabeçalhos da requisição
	 * @param request corpo da requisição
	 * @param transformer transformador da resposta
	 * @param response resposta HTTP já obtida
	 * @return resultado transformado da resposta
	 */
	@SuppressWarnings("unchecked")
	public <V> V executeHttpRequest(String trace, CcpHttpMethods method, CcpJsonRepresentation headers, String request, CcpHttpResponseTransform<V> transformer, CcpHttpResponse response) {
		
		int status = response.httpStatus;
		CcpFieldName ccpFieldName = new CcpFieldName(status);

		boolean statusIsMapped = this.flows.containsAllFields(ccpFieldName);
		boolean thereIsNoFlowForThisStatus = false == statusIsMapped && false == this.hasAlternativeFlow;

		if(thereIsNoFlowForThisStatus) {
			Set<String> fieldSet = this.flows.fieldSet(); 
			CcpErrorHttp httpError = this.ccpHttp.getHttpError(trace, this.url, method, headers, request, status, response.httpResponse, fieldSet);
			throw httpError;
		}

		CcpBusiness flow = this.flows.getOrDefault(ccpFieldName, () -> this.alternativeFlow);
		boolean validSingleJson = response.isValidSingleJson();

		boolean invalidSingleJson = false == validSingleJson;
		
		V tranform = transformer.transform(response);

		if(invalidSingleJson) {
			return tranform;
		}
		boolean isCcpJsonRepresentation = tranform instanceof CcpJsonRepresentation;
		boolean valorIgual = false == (isCcpJsonRepresentation);

		if(valorIgual) {
			return tranform;
		}
		CcpJsonRepresentation ccpJsonRepresentation = (CcpJsonRepresentation)tranform;

		CcpJsonRepresentation execute = flow.execute(ccpJsonRepresentation);
		V v = (V)execute;
		return v;
	}
	
	
}
