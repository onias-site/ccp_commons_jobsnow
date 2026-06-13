package com.ccp.especifications.email;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpContentType;

/**
 * Define o contrato para envio de e-mails via provedor externo (implementado com SendGrid).
 * Abstrai os detalhes do provedor, permitindo envio de e-mails de texto simples ou HTML.
 */
public interface CcpEmailSender {

	/**
	 * Envia um e-mail para um ou mais destinatários usando o provedor configurado.
	 * @param providerToken token de autenticação do provedor
	 * @param providerUrl URL base do provedor
	 * @param templateId identificador do template de e-mail
	 * @param sender endereço do remetente
	 * @param subject assunto do e-mail
	 * @param message corpo da mensagem
	 * @param contentType tipo de conteúdo (texto simples ou HTML)
	 * @param recipients destinatários do e-mail
	 * @return JSON com o resultado do envio
	 */
	CcpJsonRepresentation sendSimpleTextEmailMessage(String providerToken, String providerUrl, String templateId, String sender, String subject, String message, CcpHttpContentType contentType, String... recipients);
		
	
}
