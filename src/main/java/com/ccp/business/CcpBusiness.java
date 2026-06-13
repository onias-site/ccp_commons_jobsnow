package com.ccp.business;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

/**
 * Contrato central de toda lógica de negócio do sistema. Estende {@code Function<CcpJsonRepresentation, CcpJsonRepresentation>},
 * garantindo que toda operação de negócio receba e devolva o mapa JSON que flui pelo sistema.
 * Adiciona suporte a validação de entrada e controle de execução assíncrona.
 */
public interface CcpBusiness extends Function<CcpJsonRepresentation, CcpJsonRepresentation>, CcpJsonFieldName{

	/**
	 * Indica se esta operação pode ser salva como tarefa assíncrona.
	 * O padrão retorna {@code true}; implementações específicas podem sobrescrever para desabilitar.
	 */
	default boolean canBeSavedAsAsyncTask() {
		return true;
	}
	
	/**
	 * Retorna a classe usada para buscar as regras de validação do JSON de entrada.
	 * Por padrão retorna a própria classe da implementação, permitindo que as anotações de validação sejam encontradas por reflexão.
	 */
	default Class<?> getJsonValidationClass(){
		Class<? extends CcpBusiness> class1 = this.getClass();
		return class1;
	}
	
	/**
	 * Ponto de entrada seguro para execução do negócio: primeiro valida o JSON de entrada usando
	 * {@code CcpJsonValidatorEngine}, depois chama {@code apply(json)}.
	 * Use este método ao invés de chamar {@code apply} diretamente quando a validação for necessária.
	 * @param json o JSON de entrada a ser validado e processado
	 * @return o JSON resultante da execução
	 */
	default CcpJsonRepresentation execute(CcpJsonRepresentation json) {
		String className = this.getClass().getName();
		Class<?> jsonValidationClass = this.getJsonValidationClass();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, className);

		CcpJsonRepresentation apply = this.apply(json);
		return apply;
	}
	default String name() {
		return this.getClass().getName();
	}
	
}
