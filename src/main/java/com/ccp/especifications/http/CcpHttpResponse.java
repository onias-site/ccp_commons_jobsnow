package com.ccp.especifications.http;

import java.io.InputStream;
import java.util.List;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;
/**
 * Objeto imutável que encapsula a resposta HTTP, oferecendo métodos para interpretar o corpo
 * em diferentes formatos e verificar a faixa do status.
 */
public class CcpHttpResponse {
	enum JsonFieldNames implements CcpJsonFieldName{
		httpResponse, httpStatus
	}

	public final String httpResponse;
	public final int httpStatus;
	public final String curl;
	
	
	/**
	 * Lê o InputStream e converte para String, inicializando a resposta.
	 * @param httpResponse corpo da resposta como InputStream
	 * @param httpStatus código de status HTTP
	 * @param curl comando curl equivalente para debug
	 */
	public CcpHttpResponse(InputStream httpResponse, int httpStatus, String curl) {
		this(new CcpStringDecorator(httpResponse).content, httpStatus, curl);
	}

	/**
	 * Inicializa com corpo, status e curl.
	 * @param httpResponse corpo da resposta como String
	 * @param httpStatus código de status HTTP
	 * @param curl comando curl equivalente para debug
	 */
	public CcpHttpResponse(String httpResponse, int httpStatus, String curl) {
		this.httpResponse = httpResponse;
		this.httpStatus = httpStatus;
		this.curl = curl;
	}
	
	/**
	 * Verifica se o corpo é um JSON de objeto único válido.
	 * @return true se o corpo for um JSON de objeto único válido
	 */
	public boolean isValidSingleJson() {
		String httpResponseTrim = this.httpResponse.trim();
		boolean httpResponseTrimEmpty = httpResponseTrim.isEmpty();
		if(httpResponseTrimEmpty) {
			return true;
		}
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(this.httpResponse);
		CcpTextDecorator text = ccpStringDecorator.text();
		boolean validSingleJson = text.isValidSingleJson();
		return validSingleJson;
	}
	
	/**
	 * Converte o corpo para JSON; retorna JSON vazio em falha.
	 * @return representação JSON do corpo da resposta
	 */
	public CcpJsonRepresentation asSingleJson() {
		try {
			CcpStringDecorator ccpStringDecorator2 = new CcpStringDecorator(this.httpResponse);
			CcpJsonRepresentation json2 = ccpStringDecorator2.json();
			return json2;
		} catch (Exception e) {
			return CcpOtherConstants.EMPTY_JSON;
		}
	}
	
	/**
	 * Converte o corpo como lista de JSONs.
	 * @return lista de representações JSON
	 */
	public List<CcpJsonRepresentation> asListRecord(){
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		List<CcpJsonRepresentation> fromJson = json.fromJson(this.httpResponse);
		return fromJson; 
	}

	/**
	 * Converte o corpo como lista de objetos genéricos.
	 * @return lista de objetos
	 */
	public List<Object> asListObject(){
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		List<Object> fromJson = json.fromJson(this.httpResponse);
		return fromJson; 
	}

	/**
	 * Codifica o corpo em Base64.
	 * @return corpo da resposta codificado em Base64
	 */
	public String asBase64() {
		byte[] bytes = this.httpResponse.getBytes();
		CcpStringDecorator ccpStringDecorator3 = new CcpStringDecorator(bytes);
		CcpTextDecorator ccpStringDecorator3Text = ccpStringDecorator3.text();
		CcpTextDecorator asBase64 = ccpStringDecorator3Text.asBase64();
		String encodeToString = asBase64.content;
		return encodeToString;
	}
	
	
	/**
	 * Serializa status e corpo em JSON.
	 * @return representação JSON com httpStatus e httpResponse
	 */
	public String toString() {
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put(JsonFieldNames.httpStatus, this.httpStatus);
				CcpJsonRepresentation put2 = put
				.put(JsonFieldNames.httpResponse, this.httpResponse);
				String toString = put2
				.toString();
				return toString;
	}
	

	private boolean isInRange(int range) {
		boolean httpStatusMenor = this.httpStatus < range;
		if(httpStatusMenor) {
			return false;
		}
		int rangeMais = range + 99;
		boolean httpStatusMaior = this.httpStatus > (rangeMais);
		if(httpStatusMaior) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * Verifica se o status está na faixa 400–499.
	 * @return true se for erro de cliente
	 */
	public boolean isClientError() {
		boolean inRange = this.isInRange(400);
		return inRange;
	}
	
	/**
	 * Verifica se o status está na faixa 500–599.
	 * @return true se for erro de servidor
	 */
	public boolean isServerError() {
		boolean inRange2 = this.isInRange(500);
		return inRange2;
	}

	/**
	 * Verifica se o status está na faixa 200–299.
	 * @return true se for resposta de sucesso
	 */
	public boolean isSuccess() {
		boolean inRange3 = this.isInRange(200);
		return inRange3;
	}
}
