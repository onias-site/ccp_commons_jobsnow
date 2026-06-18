package com.ccp.especifications.http;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;


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
			if (expectedStatus == res.httpStatus) {
				return res;
			}
		}
		Set<String> expectedStatusList = Arrays.asList(numbers).stream().map(x -> "" + x).collect(Collectors.toSet());
		
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

		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put(JsonFieldNames.url, url)
				.put(JsonFieldNames.method, method)
				.put(JsonFieldNames.headers, headers)
				.put(JsonFieldNames.request, request)
				.put(JsonFieldNames.status, status)
				.put(JsonFieldNames.response, response);
		CcpJsonRepresentation entity = put
				.put(JsonFieldNames.trace, trace)
				.put(JsonFieldNames.details, put.content)
				.put(JsonFieldNames.expectedStatusList, expectedStatusList);

		if (status >= 600) {
			CcpErrorHttp ccpHttpError = new CcpErrorHttp(entity);
			return ccpHttpError;
		}

		if (status < 400) {
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

	@SuppressWarnings("serial")
	public static class CcpErrorHttp extends RuntimeException {
		public final CcpJsonRepresentation entity;
		protected CcpErrorHttp(CcpJsonRepresentation entity) {
			super(getMessage(entity));
			this.entity = entity;
		}
		private static String getMessage(CcpJsonRepresentation entity) {
			String string = "\n\n\nTrace:{trace}\nDetails: {details}\n. All expected status: {expectedStatusList}";
			String message = new com.ccp.decorators.CcpStringDecorator(string).text().resolveTemplate(entity).content;
			return message;
		}
	}


}
