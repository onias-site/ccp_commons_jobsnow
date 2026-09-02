package com.ccp.especifications.http;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import java.util.stream.Stream;


/**
 * Contrato para execução de requisições HTTP brutas. Além dos métodos de envio, fornece método
 * default que valida o status de retorno e factory que constrói a exceção HTTP correta por faixa de status.
 */
public interface CcpHttpRequester {
	enum JsonFieldNames implements CcpJsonFieldName{
		expectedStatusList,
		details,
		trace,
		response,
		status,
		request,
		headers,
		method,
		url,
	}
	/**
	 * Executa requisição HTTP simples.
	 * @param url URL destino
	 * @param method método HTTP
	 * @param headers cabeçalhos da requisição
	 * @param body corpo da requisição
	 * @return resposta HTTP encapsulada
	 */
	CcpHttpResponse executeHttpRequest(String url, CcpHttpMethods method, CcpJsonRepresentation headers, String body);
	
	/**
	 * Executa requisição HTTP multipart.
	 * @param url URL destino
	 * @param method método HTTP
	 * @param headers cabeçalhos da requisição
	 * @param bodyTexts partes textuais do multipart
	 * @param bodyBinaries partes binárias do multipart
	 * @return resposta HTTP encapsulada
	 */
	CcpHttpResponse executeMultiPartHttpRequest(String url, CcpHttpMethods method, CcpJsonRepresentation headers, List<CcpHttpBodyText> bodyTexts, List<CcpHttpBodyBinary> bodyBinaries);

	/**
	 * Executa e valida se o status está entre os esperados; lança {@link CcpErrorHttp} caso contrário.
	 * @param url URL destino
	 * @param method método HTTP
	 * @param headers cabeçalhos da requisição
	 * @param request corpo da requisição
	 * @param numbers status HTTP esperados
	 * @return resposta HTTP encapsulada
	 */
	default CcpHttpResponse executeHttpRequest(String url, CcpHttpMethods method, CcpJsonRepresentation headers, String request, Integer... numbers) {
		CcpHttpResponse res = this.executeHttpRequest(url, method, headers, request);

		for (int expectedStatus : numbers) {
			boolean expectedStatusIgual = expectedStatus == res.httpStatus;
			if (expectedStatusIgual) {
				return res;
			}
		}
		Stream<Integer> stream = Arrays.asList(numbers).stream();
		var streamMap = stream.map(x -> "" + x);
		Set<String> expectedStatusList = streamMap.collect(Collectors.toSet());
		
		CcpErrorHttp httpError = this.getHttpError(
				"", 
				url, 
				method, 
				headers, 
				request, 
				res.httpStatus, 
				res.httpResponse, 
				expectedStatusList
				);
		
		throw httpError;
	}

	/**
	 * Constrói {@link CcpErrorHttpClient} (4xx), {@link CcpErrorHttpServer} (5xx) ou {@link CcpErrorHttp} genérico.
	 * @param trace identificador de rastreamento
	 * @param url URL destino
	 * @param method método HTTP
	 * @param headers cabeçalhos da requisição
	 * @param request corpo da requisição
	 * @param status status HTTP recebido
	 * @param response corpo da resposta
	 * @param expectedStatusList lista de status esperados
	 * @return exceção HTTP adequada ao status recebido
	 */
	default CcpErrorHttp getHttpError(String trace, String url, CcpHttpMethods method, CcpJsonRepresentation headers,
			String request, Integer status, String response, Set<String> expectedStatusList) {
				CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON
				.put(JsonFieldNames.url, url);
				CcpJsonRepresentation put3 = put2
				.put(JsonFieldNames.method, method);
				CcpJsonRepresentation put4 = put3
				.put(JsonFieldNames.headers, headers);
				CcpJsonRepresentation put5 = put4
				.put(JsonFieldNames.request, request);
				CcpJsonRepresentation put6 = put5
				.put(JsonFieldNames.status, status);

				CcpJsonRepresentation put = put6
				.put(JsonFieldNames.response, response);
				CcpJsonRepresentation put7 = put
				.put(JsonFieldNames.trace, trace);
				CcpJsonRepresentation put8 = put7
				.put(JsonFieldNames.details, put.content);
				CcpJsonRepresentation entity = put8
				.put(JsonFieldNames.expectedStatusList, expectedStatusList);
				boolean statusMaiorOuIgual = status >= 600;

				if (statusMaiorOuIgual) {
			CcpErrorHttp ccpHttpError = new CcpErrorHttp(entity);
			return ccpHttpError;
		}
		boolean statusMenor = status < 400;

		if (statusMenor) {
			CcpErrorHttp ccpHttpError = new CcpErrorHttp(entity);
			return ccpHttpError;
		}

		boolean isClientError = status < 500;

		if (isClientError) {
			CcpErrorHttpClient ccpHttpClientError = new CcpErrorHttpClient(entity);
			return ccpHttpClientError;
		}

		CcpErrorHttpServer ccpHttpServerError = new CcpErrorHttpServer(entity);
		return ccpHttpServerError;
	}



}
