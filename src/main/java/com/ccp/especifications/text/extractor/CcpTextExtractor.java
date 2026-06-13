package com.ccp.especifications.text.extractor;

/**
 * Contrato para extração de texto de documentos (Apache Tika).
 */
public interface CcpTextExtractor {

	/**
	 * Extrai e retorna o texto puro do conteúdo fornecido.
	 * @param content conteúdo do documento a extrair texto
	 * @return texto puro extraído do documento
	 */
	String extractText(String content);
}
