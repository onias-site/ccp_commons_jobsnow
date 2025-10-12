package com.ccp.decorators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CcpCollectionDecorator implements Iterable<Object>, CcpDecorator<Collection<Object>>{

	public final Collection<Object> content;
	
	@SuppressWarnings("unchecked")
	public CcpCollectionDecorator(Collection<?> content) {
		this.content = (Collection<Object>)content;
	}
	
	public CcpCollectionDecorator(Object[] array) {
		this.content = Arrays.asList(array);
	}
	
	
	public CcpCollectionDecorator(CcpJsonRepresentation json, String key) {
		List<Object> asObjectList = json.getDynamicVersion().getAsObjectList(key);
		this.content = asObjectList;
	}

	public boolean isLongNumberList() {
		
		boolean validList = this.isValidList(x -> x.isLongNumber());
		return validList;
	}

	public boolean isDoubleNumberList() {
		
		boolean validList = this.isValidList(x -> x.isDoubleNumber());
		return validList;
	}

	public boolean isBooleanList() {
		
		boolean validList = this.isValidList(x -> x.isBoolean());
		return validList;
	}
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

	public Iterator<Object> iterator() {
		return this.content.iterator();
	}
	
	
	public boolean isEmpty() {
		return this.content.isEmpty();
	}
	
	public CcpNumberDecorator size() {
		return new CcpNumberDecorator("" + this.content.size());
	}
	
	public boolean hasNonDuplicatedItems() {
		HashSet<Object> hashSet = new HashSet<Object>(this.content);
		int s1 = this.content.size();
		int s2 = hashSet.size();
		return s1 == s2;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getExclusiveList(Collection<T> listToCompare){
		Predicate<? super Object> p = x ->  false == listToCompare.contains(x);
		List<Object> collect = new ArrayList<Object>(this.content.stream().filter(p).collect(Collectors.toList()));
		return (List<T>)collect;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getIntersectList(Collection<T> listToCompare){
		Predicate<? super Object> p = x -> listToCompare.contains(x);
		List<T> collect = (List<T> )new ArrayList<Object>(this.content.stream().filter(p).collect(Collectors.toList()));
		return collect;
	}
	
	public <T> boolean hasIntersect(Collection<T> listToCompare) {
		List<T> intersectList = this.getIntersectList(listToCompare);
		return false == intersectList.isEmpty();
	}
	
	public CcpCollectionDecorator getSubCollection(int start, int end) {
		if(end > this.content.size()) {
			end = this.content.size();
		}
		
		ArrayList<Object> arrayList = new ArrayList<>(this.content);
		List<Object> subList = arrayList.subList(start, end);
		CcpCollectionDecorator ccpCollectionDecorator = new CcpCollectionDecorator(subList);
		return ccpCollectionDecorator;
	}

	public Collection<Object> getContent() {
		return this.content;
	}
	
	
}
