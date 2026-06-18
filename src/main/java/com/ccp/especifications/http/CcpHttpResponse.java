package com.ccp.especifications.http;

import java.io.InputStream;
import java.util.List;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
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
		if(this.httpResponse.trim().isEmpty()) {
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
			return new CcpStringDecorator(this.httpResponse).json();
		} catch (Exception e) {
			return CcpOtherConstants.EMPTY_JSON;
		}
	}
	
	/**
	 * Converte o corpo como lista de JSONs.
	 * @return lista de representações JSON
	 */
	public List<CcpJsonRepresentation> asListRecord(){
		try {
			CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			List<CcpJsonRepresentation> fromJson = json.fromJson(this.httpResponse);
			return fromJson; 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Converte o corpo como lista de objetos genéricos.
	 * @return lista de objetos
	 */
	public List<Object> asListObject(){
		try {
			CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			List<Object> fromJson = json.fromJson(this.httpResponse);
			return fromJson; 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Codifica o corpo em Base64.
	 * @return corpo da resposta codificado em Base64
	 */
	public String asBase64() {
		byte[] bytes = this.httpResponse.getBytes();
		String encodeToString = new CcpStringDecorator(bytes).text().asBase64().content;
		return encodeToString;
	}
	
	
	/**
	 * Serializa status e corpo em JSON.
	 * @return representação JSON com httpStatus e httpResponse
	 */
	public String toString() {
		return CcpOtherConstants.EMPTY_JSON
				.put(JsonFieldNames.httpStatus, this.httpStatus)
				.put(JsonFieldNames.httpResponse, this.httpResponse)
				.toString();
	}
	

	private boolean isInRange(int range) {
		if(this.httpStatus < range) {
			return false;
		}
		if(this.httpStatus > (range + 99)) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * Verifica se o status está na faixa 400–499.
	 * @return true se for erro de cliente
	 */
	public boolean isClientError() {
		return this.isInRange(400);
	}
	
	/**
	 * Verifica se o status está na faixa 500–599.
	 * @return true se for erro de servidor
	 */
	public boolean isServerError() {
		return this.isInRange(500);
	}

	/**
	 * Verifica se o status está na faixa 200–299.
	 * @return true se for resposta de sucesso
	 */
	public boolean isSuccess() {
		return this.isInRange(200);
	}
}
