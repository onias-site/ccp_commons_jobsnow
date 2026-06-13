package com.ccp.decorators;

import java.io.InputStream;
import java.util.Collection;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;

/**
 * Decorator central sobre {@code String} que serve como hub de conversão para todos os outros tipos do
 * framework. A partir de uma string, oferece acesso fluente a decorators especializados (e-mail, arquivo,
 * hash, JSON, URL, reflexão, etc.) e verificações de tipo.
 */
public class CcpStringDecorator implements CcpDecorator<String> {

	public final String content;

	/**
	 * Extrai o valor de um campo do JSON como string.
	 */
	public CcpStringDecorator(CcpJsonRepresentation json, String key) {
		this.content = json.getAsString(new CcpFieldName(key));
	}

	/**
	 * Encapsula a string fornecida.
	 */
	public CcpStringDecorator(String content) {
		this.content = content;
	}

	/**
	 * Lê todos os bytes do {@code InputStream} e os converte em string.
	 */
	public CcpStringDecorator(InputStream is) {
		this(readAllBytes(is));
	}

	/**
	 * Converte o array de bytes primitivos em string.
	 */
	public CcpStringDecorator(byte[] content) {
		this(new String(content));
	}

	/**
	 * Converte o array de bytes wrapper em string.
	 */
	public CcpStringDecorator(Byte[] content) {
		this(readAllBytes(content));
	}

	private static byte[] readAllBytes(InputStream is){
		try {
			byte[] readAllBytes = is.readAllBytes();
			return readAllBytes;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Interpreta a string como endereço de e-mail.
	 */
	public CcpEmailDecorator email() {
		return new CcpEmailDecorator(this.content);
	}

	/**
	 * Interpreta a string como caminho de arquivo.
	 */
	public CcpFileDecorator file() {
		return new CcpFileDecorator(this.content);
	}

	/**
	 * Interpreta a string como caminho de diretório.
	 */
	public CcpFolderDecorator folder() {
		return new CcpFolderDecorator(this.content);
	}

	/**
	 * Prepara a string para cálculo de hash.
	 */
	public CcpHashDecorator hash() {
		return new CcpHashDecorator(this.content);
	}

	/**
	 * Converte a string para número.
	 */
	public CcpNumberDecorator number() {
		return new CcpNumberDecorator(this.content);
	}

	/**
	 * Cria um {@code CcpJsonFieldName} cujo valor é esta string.
	 */
	public CcpJsonFieldName jsonFieldName() {
		CcpJsonFieldName ccpJsonFieldName = new CcpFieldName(this.content);
		return ccpJsonFieldName;
	}

	/**
	 * Acessa operações de manipulação de texto.
	 */
	public CcpTextDecorator text() {
		return new CcpTextDecorator(this.content);
	}

	/**
	 * Interpreta a string como URL para encode/decode.
	 */
	public CcpUrlDecorator url() {
		return new CcpUrlDecorator(this.content);
	}

	/**
	 * Desserializa a string como JSON.
	 */
	public CcpJsonRepresentation json() {
		return new CcpJsonRepresentation(this.content);
	}

	/**
	 * Interpreta a string como senha.
	 */
	public CcpPasswordDecorator password() {
		return new CcpPasswordDecorator(this.content);
	}

	/**
	 * Prepara a string para abertura de stream.
	 */
	public CcpInputStreamDecorator inputStreamFrom() {
		return new CcpInputStreamDecorator(this.content);
	}

	/**
	 * Prepara a string para leitura de configurações.
	 */
	public CcpPropertiesDecorator propertiesFrom() {
		CcpPropertiesDecorator ccpPropertiesDecorator = new CcpPropertiesDecorator(this.content);
		return ccpPropertiesDecorator;
	}

	/**
	 * Prepara a string (nome de classe) para reflexão.
	 */
	public CcpReflectionConstructorDecorator reflection() {
		CcpReflectionConstructorDecorator decorator = new CcpReflectionConstructorDecorator(this.content);
		return decorator;
	}

	/**
	 * Verifica se a string representa um JSON de objeto válido.
	 */
	public boolean isInnerJson() {
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		boolean validJson = json.isValidJson(this.content);
		return validJson;
	}

	/**
	 * Retorna a string interna.
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Verifica se a string representa uma lista JSON.
	 */
	public boolean isList() {
		boolean valid = this.isValid(x ->  {
			Collection<?> fromJson = CcpDependencyInjection.getDependency(CcpJsonHandler.class).fromJson(x);
			fromJson.toString();
		});
		return valid;

	}

	/**
	 * Verifica se a string pode ser convertida para {@code long}.
	 */
	@SuppressWarnings("unused")
	public boolean isLongNumber() {
		boolean valid = this.isValid(x -> {
			Object obj = x.endsWith(".0") ? Double.valueOf(x) : Long.valueOf(x);
		});
		return valid;
	}

	/**
	 * Verifica se a string pode ser convertida para {@code double}.
	 */
	public boolean isDoubleNumber() {
		boolean valid = this.isValid(x -> Double.valueOf(x));
		return valid;
	}

	/**
	 * Verifica se a string é {@code "true"} ou {@code "false"} (case insensitive).
	 */
	public boolean isBoolean() {
		boolean valid = this.isValid(x -> {
			if ("true".equalsIgnoreCase(x)) {
				return;
			}
			if ("false".equalsIgnoreCase(x)) {
				return;
			}
			throw new RuntimeException();
		});
		return valid;
	}

	private boolean isValid(Consumer<String>  consumer) {
		try {
			consumer.accept(this.content);;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna a string interna.
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Retorna o array de bytes da string (tipo wrapper {@code Byte[]}).
	 */
	public Byte[] getBytes() {
		byte[] bytes = this.content.getBytes();
		Byte[] result = new Byte[bytes.length];
		int k = 0;

		for (Byte byte1 : bytes) {
			result[k++] = byte1;
		}

		return result;
	}

	private static byte[] readAllBytes(Byte[] bytes) {
		byte[] result = new byte[bytes.length];
		int k = 0;

		for (Byte byte1 : bytes) {
			result[k++] = byte1;
		}

		return result;

	}
}
