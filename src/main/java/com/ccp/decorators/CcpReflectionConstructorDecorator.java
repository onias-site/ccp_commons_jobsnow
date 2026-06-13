package com.ccp.decorators;

import java.lang.reflect.Constructor;

/**
 * Decorator sobre o nome qualificado de uma classe Java que oferece operações de reflexão: resolução da
 * classe, criação de instâncias e escolha do contexto de invocação (estático ou por instância). É o ponto
 * de entrada da API fluente de reflexão do framework.
 */
public class CcpReflectionConstructorDecorator implements CcpDecorator<String> {

	public final String content;

	/**
	 * Encapsula o nome completo da classe.
	 */
	protected CcpReflectionConstructorDecorator(String content) {
		this.content = content;
	}

	/**
	 * Extrai o nome da classe a partir de um campo do JSON.
	 */
	public CcpReflectionConstructorDecorator(CcpJsonRepresentation json, String field) {
		this.content = json.getAsString(new CcpFieldName(field));
	}

	/**
	 * Extrai o nome da classe diretamente de um objeto {@code Class}.
	 */
	public CcpReflectionConstructorDecorator(Class<?> clazz) {
		this.content = clazz.getName();
	}

	/**
	 * Carrega e retorna o objeto {@code Class} pelo nome. Lança {@code RuntimeException} se a classe não for encontrada.
	 */
	public Class<?> forName(){
		try {
			Class<?> forName = Class.forName(this.content);
			return forName;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna {@code true} se a classe puder ser carregada; {@code false} caso contrário (sem lançar exceção).
	 */
	public boolean thisClassExists(){
		try {
			Class.forName(this.content);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Cria uma nova instância da classe via construtor padrão (sem argumentos), mesmo que o construtor seja privado (usa {@code setAccessible(true)}).
	 */
	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		try {
			Class<?> forName = Class.forName(this.content);
			Constructor<?> declaredConstructor = forName.getDeclaredConstructor();
			declaredConstructor.setAccessible(true);
			T newInstance = (T) declaredConstructor.newInstance();
			return newInstance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna o nome da classe.
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Retorna o nome completo da classe.
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Inicia o contexto de reflexão para chamada de métodos estáticos (retorna {@code CcpReflectionStaticContextDecorator}).
	 */
	public CcpReflectionOptionsDecorator fromStaticContext() {
		CcpReflectionStaticContextDecorator result = new CcpReflectionStaticContextDecorator(this);
		return result;
	}

	/**
	 * Inicia o contexto de reflexão criando uma nova instância da classe (retorna {@code CcpReflectionNewInstanceDecorator}).
	 */
	public CcpReflectionOptionsDecorator fromNewInstance() {
		CcpReflectionOptionsDecorator result = new CcpReflectionNewInstanceDecorator(this);
		return result;
	}

	/**
	 * Inicia o contexto de reflexão a partir de uma instância já existente.
	 */
	public CcpReflectionOptionsDecorator fromInstance(Object instance) {
		Class<?> clazz = instance.getClass();
		CcpReflectionOptionsDecorator result = new CcpReflectionNewInstanceDecorator(instance, clazz);
		return result;
	}

}
