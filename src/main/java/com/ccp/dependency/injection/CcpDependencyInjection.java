package com.ccp.dependency.injection;

import java.util.HashMap;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;

/**
 * Registro central de injeção de dependência do framework. Mantém um mapa estático de interface → implementação
 * e fornece métodos para registrar, recuperar, remover e substituir temporariamente implementações. É o mecanismo
 * que permite trocar comportamentos (real vs. mock) em testes ou em diferentes ambientes.
 */
public class CcpDependencyInjection {

	static Map<Class<?>, Object> instances = new HashMap<>();

	/**
	 * Substitui temporariamente as dependências, executa o {@code business} com o {@code json} fornecido
	 * e, ao final, restaura as dependências originais. Retorna o resultado da execução do business.
	 */
	@SuppressWarnings("rawtypes")
	public static CcpJsonRepresentation replaceDependenciesTemporally(CcpJsonRepresentation json, CcpBusiness business, CcpInstanceProvider<?>... providers) {

		CcpInstanceProvider[] actuallyDependecies = new CcpInstanceProvider[providers.length];
		int k = 0;
		for (CcpInstanceProvider<?> provider : providers) {
			actuallyDependecies[k++] = (CcpInstanceProvider) getDependency(provider.getClass().getInterfaces()[0]);
		}
		loadAllDependencies(providers);

		CcpJsonRepresentation apply = business.apply(json);
		loadAllDependencies(actuallyDependecies);
		return apply;
	}


	/**
	 * Registra cada provider no mapa de dependências, associando a interface (primeiro {@code getInterfaces()[0]})
	 * à instância retornada por {@code getInstance()}.
	 */
	public static void loadAllDependencies(CcpInstanceProvider<?>... providers) {

		for (CcpInstanceProvider<?> provider : providers) {
			Object implementation = provider.getInstance();
			Class<? extends Object> class1 = implementation.getClass();
			Class<?>[] interfaces = class1.getInterfaces();
			Class<?> especification = interfaces[0];
			instances.put(especification, implementation);
		}
	}

	/**
	 * Retorna {@code true} se já existe uma implementação registrada para a interface {@code interfaceClass}.
	 */
	public static <T>boolean hasDependency(Class<T> interfaceClass) {
		Object implementation = instances.get(interfaceClass);
		return implementation != null;
	}

	/**
	 * Recupera a implementação registrada para a interface {@code interfaceClass}.
	 * Lança {@code CcpErrorDependencyInjectionMissing} se nenhuma implementação foi registrada.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getDependency(Class<T> interfaceClass) {
		Object implementation = instances.get(interfaceClass);
		if(implementation == null) {
			throw new CcpErrorDependencyInjectionMissing(interfaceClass);
		}
		return (T) implementation;
	}

	/**
	 * Remove do registro a implementação associada a {@code interfaceClass}.
	 */
	public static void removeDependecy(Class<?> interfaceClass) {
		instances.remove(interfaceClass);
	}

	/**
	 * Instancia via reflexão a classe {@code interfaceClass} (que deve implementar {@code CcpInstanceProvider}),
	 * chama {@code getInstance()} e retorna o objeto resultante.
	 */
	public static <T> T getInstance(Class<CcpInstanceProvider<T>> interfaceClass) {

		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(interfaceClass);
		CcpInstanceProvider<T> instanceProvider = reflection.newInstance();
		T instance = instanceProvider.getInstance();
		return instance;
	}




}
