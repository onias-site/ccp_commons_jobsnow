package com.ccp.validations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpNumberDecorator;

/**
 * Ponto de entrada da API fluente de validação. Recebe um {@code CcpJsonRepresentation} e uma lista
 * de campos e filtra automaticamente apenas os campos presentes no JSON, delegando as verificações
 * aos demais componentes da API ({@code CcpAreOfTheType}, {@code CcpIfTheyAre},
 * {@code CcpIfTheyAreArrayValuesSoEachOne}).
 */
public class CcpItIsTrueThatTheFollowingFields {

	public final CcpJsonRepresentation content;
	public final String[] fields;

	/**
	 * Inicializa o validador filtrando apenas os campos que existem em {@code content}.
	 * @param content o JSON de contexto
	 * @param fields os campos candidatos à validação
	 */
	public CcpItIsTrueThatTheFollowingFields(CcpJsonRepresentation content, String[] fields) {
		this.content = content;
		List<String> collect = Arrays.asList(fields).stream().filter(x -> content.containsField(new CcpFieldName(x))).collect(Collectors.toList());
		this.fields = collect.toArray(new String[collect.size()]);
	}

	/** Retorna um {@code CcpAreOfTheType} para verificar o tipo dos valores dos campos. */
	public CcpAreOfTheType areAllOfTheType() {
		Function<String[], String[]> arrayProducerOfItems = CcpItIsTrueThatTheFollowingFields.getArrayProducerOfItems(this.content);
		return new CcpAreOfTheType(arrayProducerOfItems, this.fields);
	}

	/** Retorna um {@code CcpIfTheyAreArrayValuesSoEachOne} para validação de campos array. */
	public CcpIfTheyAreArrayValuesSoEachOne ifTheyAreAllArrayValuesThenEachOne() {
		return new CcpIfTheyAreArrayValuesSoEachOne(this.content, this.fields);
	}

	/** Retorna um {@code CcpIfTheyAre} para validação de campos de valor simples. */
	public CcpIfTheyAre ifTheyAreAll() {
		return new CcpIfTheyAre(this.content, this.fields);
	}
	
	/**
	 * Retorna um produtor que, dado um array de nomes de campos, produz um array com todos os itens
	 * de cada campo array concatenados em uma única lista.
	 * @param content o JSON de contexto
	 */
	public static Function<String[], String[]> getArrayProducerOfArrays(CcpJsonRepresentation content) {
		Function<String[], String[]> arrayProcucer = fields ->{
			List<String> list = new ArrayList<String>();
			for (String field : fields) {
				List<String> asStringList = content.getAsStringList(new CcpFieldName(field));
				list.addAll(asStringList);
			}
			String[] array = list.toArray(new String[list.size()]);
			return array;
		};
		
		return arrayProcucer;

	}
	
	/**
	 * Retorna um produtor que, dado um array de nomes de campos, produz um array com o valor de
	 * cada campo individual (um valor por campo).
	 * @param content o JSON de contexto
	 */
	public static Function<String[], String[]> getArrayProducerOfItems(CcpJsonRepresentation content) {
		Function<String[], String[]> arrayProducer = fields -> {
			String [] result = new String[fields.length];	
			int k = 0;
			for (String field : fields) {
				String value = content.getAsString(new CcpFieldName(field));
				result[k++] = value;
			}
			return result;
		};
		
		return arrayProducer;

	}

}
interface Tester{
	boolean test(CcpNumberDecorator d, int limit);
}
