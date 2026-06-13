package com.ccp.decorators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Decorator sobre {@code Collection<Object>} que adiciona operações de análise de tipo, comparação entre coleções e fatiamento.
 * Permite verificar se todos os elementos são de um determinado tipo JSON, calcular interseções/diferenças e iterar de forma padronizada.
 */
public class CcpCollectionDecorator implements Iterable<Object>, CcpDecorator<Collection<Object>>{

	public final Collection<Object> content;

	/**
	 * Encapsula qualquer {@code Collection} já existente.
	 * @param content a coleção a ser encapsulada
	 */
	@SuppressWarnings("unchecked")
	public CcpCollectionDecorator(Collection<?> content) {
		this.content = (Collection<Object>)content;
	}
	
	/**
	 * Converte um array em coleção encapsulada.
	 * @param array o array a ser encapsulado
	 */
	public CcpCollectionDecorator(Object[] array) {
		this.content = Arrays.asList(array);
	}

	/**
	 * Extrai a lista do campo {@code key} dentro do JSON fornecido.
	 * @param json o JSON de origem
	 * @param key o nome do campo que contém a lista
	 */
	public CcpCollectionDecorator(CcpJsonRepresentation json, String key) {
		this.content = json.getAsObjectList(new CcpFieldName(key));
	}

	/**
	 * Retorna {@code true} se todos os elementos da coleção podem ser interpretados como números {@code long}.
	 */
	public boolean isLongNumberList() {
		
		boolean validList = this.isValidList(x -> x.isLongNumber());
		return validList;
	}

	/**
	 * Retorna {@code true} se todos os elementos podem ser interpretados como {@code double}.
	 */
	public boolean isDoubleNumberList() {
		
		boolean validList = this.isValidList(x -> x.isDoubleNumber());
		return validList;
	}

	/**
	 * Retorna {@code true} se todos os elementos são {@code "true"} ou {@code "false"}.
	 */
	public boolean isBooleanList() {
		
		boolean validList = this.isValidList(x -> x.isBoolean());
		return validList;
	}
	/**
	 * Retorna {@code true} se todos os elementos são strings que representam JSON de objeto válido.
	 */
	public boolean isJsonList() {
		
		boolean validList = this.isValidList(x -> x.text().isValidSingleJson());
		return validList;
	}

	private boolean isValidList(Predicate<CcpStringDecorator> predicate) {
		
		for (Object object : this.content) {
			CcpStringDecorator t = new CcpStringDecorator("" + object);
			boolean failed = false ==  predicate.test(t);
			if(failed) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Implementação de {@code Iterable}; permite uso em {@code for-each}.
	 */
	public Iterator<Object> iterator() {
		return this.content.iterator();
	}
	
	
	/**
	 * Verifica se a coleção não possui elementos.
	 */
	public boolean isEmpty() {
		return this.content.isEmpty();
	}
	
	/**
	 * Retorna o tamanho da coleção encapsulado em {@code CcpNumberDecorator} para facilitar comparações.
	 */
	public CcpNumberDecorator size() {
		return new CcpNumberDecorator("" + this.content.size());
	}
	
	/**
	 * Retorna {@code true} se todos os itens são únicos (sem duplicatas), comparando os tamanhos com e sem {@code HashSet}.
	 */
	public boolean hasNonDuplicatedItems() {
		HashSet<Object> hashSet = new HashSet<Object>(this.content);
		int s1 = this.content.size();
		int s2 = hashSet.size();
		return s1 == s2;
	}
	
	/**
	 * Retorna os itens presentes nesta coleção que NÃO estão em {@code listToCompare} (diferença).
	 * @param listToCompare a coleção de comparação
	 * @return lista com os itens exclusivos desta coleção
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getExclusiveList(Collection<T> listToCompare){
		Predicate<? super Object> p = x ->  false == listToCompare.contains(x);
		List<Object> collect = new ArrayList<Object>(this.content.stream().filter(p).collect(Collectors.toList()));
		return (List<T>)collect;
	}

	/**
	 * Retorna os itens presentes em ambas as coleções (interseção).
	 * @param listToCompare a coleção de comparação
	 * @return lista com os itens presentes em ambas as coleções
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getIntersectList(Collection<T> listToCompare){
		Predicate<? super Object> p = x -> listToCompare.contains(x);
		List<T> collect = (List<T> )new ArrayList<Object>(this.content.stream().filter(p).collect(Collectors.toList()));
		return collect;
	}
	
	/**
	 * Retorna {@code true} se a interseção com {@code listToCompare} não for vazia.
	 * @param listToCompare a coleção de comparação
	 */
	public <T> boolean hasIntersect(Collection<T> listToCompare) {
		List<T> intersectList = this.getIntersectList(listToCompare);
		return false == intersectList.isEmpty();
	}
	
	/**
	 * Retorna uma sub-coleção delimitada pelos índices {@code start} e {@code end}, ajustando {@code end} caso ultrapasse o tamanho real.
	 * @param start índice inicial (inclusivo)
	 * @param end índice final (exclusivo)
	 */
	public CcpCollectionDecorator getSubCollection(int start, int end) {
		if(end > this.content.size()) {
			end = this.content.size();
		}
		
		ArrayList<Object> arrayList = new ArrayList<>(this.content);
		List<Object> subList = arrayList.subList(start, end);
		CcpCollectionDecorator ccpCollectionDecorator = new CcpCollectionDecorator(subList);
		return ccpCollectionDecorator;
	}

	/**
	 * Implementação de {@code CcpDecorator}; devolve a coleção interna.
	 */
	public Collection<Object> getContent() {
		return this.content;
	}
	
	
}
