package com.ccp.validations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

/**
 * Componente da API fluente de validação para campos cujos valores são arrays. Cada método itera
 * sobre todos os itens de todos os campos indicados, retornando {@code true} somente se a condição
 * for satisfeita por todos eles.
 */
public class CcpIfTheyAreArrayValuesSoEachOne {
	public final CcpJsonRepresentation content;
	public final String[] fields;

	/** Armazena o JSON de contexto e os campos array a serem verificados. */
	public CcpIfTheyAreArrayValuesSoEachOne(CcpJsonRepresentation content, String[] fields) {
		this.content = content;
		this.fields = fields;
	}

	/** Retorna {@code true} se nenhum dos arrays contiver itens duplicados. */
	public boolean hasNonDuplicatedItems() {
		for (String field : this.fields) {
			boolean hasDuplicatedItems = false == this.content.getAsArrayMetadata(new CcpFieldName(field)).hasNonDuplicatedItems();
			if(hasDuplicatedItems) {
				return false;
			}
		}
		return true;
	}
	/** Retorna um {@code CcpAreOfTheType} operando sobre todos os itens concatenados dos arrays. */
	public CcpAreOfTheType isOfTheType() {

		Function<String[], String[]> arrayProducerOfArrays = CcpItIsTrueThatTheFollowingFields.getArrayProducerOfArrays(this.content);
		return new CcpAreOfTheType(arrayProducerOfArrays, this.fields);
	}
	/** Retorna um {@code CcpRangeSize} cujos valores são os tamanhos (número de itens) de cada array. */
	public CcpRangeSize hasTheSizeThatIs() {
		Function<String[], String[]> arrayProducer = fields -> {
			String[] result = new String[fields.length];
			int k = 0;
			for (String field : fields) {
				int size = this.content.getAsObjectList(new CcpFieldName(field)).size();
				result[k++] = "" + size;
			}
			return result;
		};
		return new CcpRangeSize(arrayProducer, this.fields);
	}
	/**
	 * Retorna {@code true} se cada item textual de cada array estiver contido nos valores permitidos.
	 * @param args valores permitidos
	 */
	public boolean isTextAndItIsContainedAtTheList(String...args) {
		
		List<String> asList = Arrays.asList(args);	
		
		for (String field : this.fields) {
			List<String> asStringList = this.content.getAsStringList(new CcpFieldName(field));
			for (String string : asStringList) {
				boolean notAllowedValue = false == asList.contains(string);
				if(notAllowedValue) {
					return false;
				}
			}
		
		}
		return true;
	}
	
	/**
	 * Sobrecarga de {@link #isTextAndItIsContainedAtTheList(String...)} que aceita {@code Collection}.
	 * @param args coleção de valores permitidos
	 */
	public <T> boolean isTextAndItIsContainedAtTheList(Collection<T> args) {
		List<String> collect = args.stream().map(x -> x.toString()).collect(Collectors.toList());
		String[] array = collect.toArray(new String[collect.size()]);
		boolean result = this.isTextAndItIsContainedAtTheList(array);
		return result;
	}
	
	
	/**
	 * Retorna {@code true} se cada item numérico de cada array estiver na lista de doubles permitidos.
	 * @param args valores numéricos permitidos
	 */
	public boolean isNumberAndItIsContainedAtTheList(Double...args) {
		
		List<Double> asList = Arrays.asList(args);

		for (String field : this.fields) {
			Optional<String> findFirst = this.content.getAsStringList(new CcpFieldName(field))
			.stream().filter(x -> false == new CcpStringDecorator(x).isDoubleNumber()).findFirst();
			boolean sameNumberIsNotDouble = findFirst.isPresent();
			if (sameNumberIsNotDouble) {
				return false;
			}
			List<Double> collect = this.content.getAsStringList(new CcpFieldName(field))
			.stream()
			.map(x -> Double.valueOf(x))
			.collect(Collectors.toList());
			
			for (Double number : collect) {
				boolean notAllowedValue = false == asList.contains(number);
				if(notAllowedValue) {
					return false;
				}
			}
		}
		
		return true;
	}
	/**
	 * Sobrecarga de {@link #isNumberAndItIsContainedAtTheList(Double...)} que aceita {@code Collection}.
	 * @param args coleção de valores numéricos permitidos
	 */
	public boolean isNumberAndItIsContainedAtTheList(Collection<Object> args) {
		
		List<Double> collect = args.stream().map(x -> Double.valueOf("" + x)).collect(Collectors.toList());
		Double[] array = collect.toArray(new Double[collect.size()]);
		boolean result = this.isNumberAndItIsContainedAtTheList(array);
		return result;
	
	}	
	
	/** Retorna um {@code CcpRangeSize} com os comprimentos de cada item textual dos arrays. */
	public CcpRangeSize isTextAndHasSizeThatIs() {
		Function<String[], String[]> xxx = fields ->{
			List<String> list = new ArrayList<>();

			for (String field : fields) {
				List<String> asStringList = this.content.getAsStringList(new CcpFieldName(field));
				list.addAll(asStringList.stream().map(x -> "" + x.length()).collect(Collectors.toList()));
			}
			
			return list.toArray(new String[list.size()]);
		};
	
		return new CcpRangeSize(xxx, this.fields);
	}
	/** Retorna um {@code CcpRangeSize} com os valores numéricos de cada item dos arrays. */
	public CcpRangeSize isNumberAndItIs() {
		Function<String[], String[]> arrayProducer = fields -> {
			List<String> list = new ArrayList<String>();
			for (String field : fields) {
				List<String> asStringList = this.content.getAsStringList(new CcpFieldName(field));
				list.addAll(asStringList);
			}
			String[] result = list.toArray(new String[list.size()]);
			return result;
		};

		return new CcpRangeSize(arrayProducer, this.fields);
	}

	
}
