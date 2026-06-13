package com.ccp.validations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;

/**
 * Componente da API fluente de validação para campos de valor simples (não-array). Oferece
 * verificações de pertencimento a lista, intervalo de tamanho/valor e conformidade com regex,
 * retornando {@code true} apenas se todos os campos satisfizerem a condição.
 */
public class CcpIfTheyAre {
	public final CcpJsonRepresentation content;
	public final String[] fields;

	/** Armazena o JSON de contexto e os campos a serem verificados. */
	public CcpIfTheyAre(CcpJsonRepresentation content, String[] fields) {
		this.content = content;
		this.fields = fields;
	}

	/**
	 * Retorna {@code true} se o valor textual de cada campo estiver contido na lista fornecida.
	 * @param args valores permitidos
	 */
	public boolean textsThenEachOneIsContainedAtTheList(String ...args) {
		
		List<String> asList = Arrays.asList(args);
		
		for (String field : this.fields) {
			
			String asString = this.content.getAsString(new CcpFieldName(field));
			
			boolean notAllowedValue = false == asList.contains(asString);
			if(notAllowedValue) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Sobrecarga que aceita uma {@code Collection} genérica como lista de valores permitidos.
	 * @param args coleção de valores permitidos
	 */
	public <T> boolean textsThenEachOneIsContainedAtTheList(Collection<T> args) {
		List<String> collect = args.stream().map(x -> x.toString()).collect(Collectors.toList());
		String[] array = collect.toArray(new String[collect.size()]);
		boolean result = this.textsThenEachOneIsContainedAtTheList(array);
		return result;
	}	

	/**
	 * Retorna um {@code CcpRangeSize} cujos valores são os comprimentos (caracteres) dos textos de
	 * cada campo, permitindo comparações de tamanho na sequência da chamada fluente.
	 */
	public CcpRangeSize textsThenEachOneHasTheSizeThatIs() {
		Function<String[], String[]> arrayProducer = fields -> {
			String[] result = new String[fields.length];
			int k = 0;
			for (String field : fields) {
				String asString = this.content.getAsString(new CcpFieldName(field));
				result[k++] = "" + asString.length();
			}
			return result;
		};
		return new CcpRangeSize(arrayProducer, this.fields);
	}
	/**
	 * Retorna um {@code CcpRangeSize} cujos valores são os números de cada campo, permitindo
	 * comparações numéricas na sequência da chamada fluente.
	 */
	public CcpRangeSize numbersThenEachOneIs() {
		Function<String[], String[]> arrayProducerOfItems = CcpItIsTrueThatTheFollowingFields.getArrayProducerOfItems(this.content);
		return new CcpRangeSize(arrayProducerOfItems, this.fields);
	}

	/**
	 * Retorna {@code true} se o valor numérico de cada campo estiver na lista de doubles fornecida.
	 * @param args valores numéricos permitidos
	 */
	public boolean numbersThenEachOneIsContainedAtTheList(Double... args) {

		List<Double> asList = Arrays.asList(args);
		
		for (String field : this.fields) {

			Double value = this.content.getAsDoubleNumber(new CcpFieldName(field));
			
			boolean notAllowedValue = false == asList.contains(value);
			if(notAllowedValue) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Sobrecarga que aceita uma {@code Collection} como lista de valores numéricos permitidos.
	 * @param args coleção de valores numéricos permitidos
	 */
	public boolean numbersThenEachOneIsContainedAtTheList(Collection<Object> args) {
		List<Double> collect = args.stream().map(x -> Double.valueOf(x.toString())).collect(Collectors.toList());
		Double[] array = collect.toArray(new Double[collect.size()]);
		boolean result = this.numbersThenEachOneIsContainedAtTheList(array);
		return result;
	}

	/**
	 * Retorna {@code true} se o valor textual de cada campo corresponder à expressão regular informada.
	 * @param regex a expressão regular a ser testada
	 */
	public boolean textsThenEachOneMatchesWithTheFollowingRegex(String regex) {

		for (String field : this.fields) {
			
			String value = this.content.getAsString(new CcpFieldName(field));
			CcpStringDecorator csd = new CcpStringDecorator(value);
			CcpTextDecorator text = csd.text();
			
			boolean regexDoesNotMatch = false == text.regexMatches(regex);
			
			if(regexDoesNotMatch) {
				return false;
			}
		}
		return true;
	}

}
