package com.ccp.especifications.json;

/**
 * Contrato de serialização/desserialização JSON (Gson). Converte objetos Java em JSON e vice-versa,
 * e valida se uma string é JSON bem formado.
 */
public interface CcpJsonHandler {

	/**
	 * Serializa o objeto para JSON compacto.
	 * @param md objeto a serializar
	 * @return string JSON compacta
	 */
	String toJson(Object md);

	/**
	 * Serializa o objeto para JSON formatado (pretty print).
	 * @param md objeto a serializar
	 * @return string JSON formatada
	 */
	String asPrettyJson(Object md);

	/**
	 * Desserializa JSON para o tipo inferido.
	 * @param <T> tipo de destino
	 * @param md string JSON a desserializar
	 * @return objeto do tipo inferido
	 */
	<T> T fromJson(String md);

	/**
	 * Verifica se a string é um JSON válido.
	 * @param src string a verificar
	 * @return true se for JSON válido
	 */
	boolean isValidJson(String src);
	
}
