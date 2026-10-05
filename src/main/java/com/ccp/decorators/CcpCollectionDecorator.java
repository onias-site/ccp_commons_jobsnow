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
 * Decorator over {@code Collection<Object>} adding type checks of the items, comparison between collections and
 * slicing.
 */
public class CcpCollectionDecorator implements Iterable<Object>, CcpDecorator<Collection<Object>>{

	/** The wrapped collection. */
	public final Collection<Object> content;

	/**
	 * Wraps an existing collection (no copy is made).
	 * @param content the collection to wrap
	 */
	@SuppressWarnings("unchecked")
	public CcpCollectionDecorator(Collection<?> content) {
		this.content = (Collection<Object>)content;
	}
	
	/**
	 * Wraps an array as a fixed-size list backed by it.
	 * @param array the array to wrap
	 */
	public CcpCollectionDecorator(Object[] array) {
		this.content = Arrays.asList(array);
	}

	/**
	 * Wraps the list held by a field of the JSON.
	 * @param json the source JSON
	 * @param key the field holding the list
	 */
	public CcpCollectionDecorator(CcpJsonRepresentation json, String key) {
		CcpFieldName ccpFieldName = new CcpFieldName(key);
		this.content = json.getAsObjectList(ccpFieldName);
	}

	/**
	 * Tells whether every item, in its text form, is an integer number. An empty collection is valid.
	 * @return {@code true} when every item is an integer number
	 */
	public boolean isLongNumberList() {
		
		boolean validList = this.isValidList(x -> x.isLongNumber());
		return validList;
	}

	/**
	 * Tells whether every item, in its text form, is a decimal number. An empty collection is valid.
	 * @return {@code true} when every item is a number
	 */
	public boolean isDoubleNumberList() {
		
		boolean validList = this.isValidList(x -> x.isDoubleNumber());
		return validList;
	}

	/**
	 * Tells whether every item, in its text form, is {@code true} or {@code false}. An empty collection is valid.
	 * @return {@code true} when every item is a boolean
	 */
	public boolean isBooleanList() {
		
		boolean validList = this.isValidList(x -> x.isBoolean());
		return validList;
	}
	/**
	 * Tells whether every item, in its text form, is a valid JSON object. An empty collection is valid.
	 * @return {@code true} when every item is a JSON object
	 */
	public boolean isJsonList() {
		
		boolean validList = this.isValidList(x -> x.text().isValidSingleJson());
		return validList;
	}

	/**
	 * Tells whether the text form of every item passes the predicate.
	 * @param predicate the check applied to each item
	 * @return {@code false} at the first item that fails, {@code true} otherwise
	 */
	private boolean isValidList(Predicate<CcpStringDecorator> predicate) {
		
		for (Object object : this.content) {
			String objectAsText = "" + object;
			CcpStringDecorator t = new CcpStringDecorator(objectAsText);
			boolean test = predicate.test(t);
			boolean failed = false ==  test;
			if(failed) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Iterates over the wrapped collection, allowing {@code for-each}.
	 * @return the iterator of the collection
	 */
	public Iterator<Object> iterator() {
		var iterator = this.content.iterator();
		return iterator;
	}
	
	
	/**
	 * Tells whether the collection has no items.
	 * @return {@code true} when the collection is empty
	 */
	public boolean isEmpty() {
		boolean contentEmpty = this.content.isEmpty();
		return contentEmpty;
	}
	
	/**
	 * Returns the size of the collection as a {@code CcpNumberDecorator}, to ease comparisons.
	 * @return the size
	 */
	public CcpNumberDecorator size() {
		int contentSize = this.content.size();
		String contentSizeAsText = "" + contentSize;
		CcpNumberDecorator ccpNumberDecorator = new CcpNumberDecorator(contentSizeAsText);
		return ccpNumberDecorator;
	}
	
	/**
	 * Tells whether every item is unique (by {@code equals}/{@code hashCode}).
	 * @return {@code true} when there are no duplicates
	 */
	public boolean hasNonDuplicatedItems() {
		HashSet<Object> hashSet = new HashSet<Object>(this.content);
		int s1 = this.content.size();
		int s2 = hashSet.size();
		boolean sameInstance = s1 == s2;
		return sameInstance;
	}
	
	/**
	 * Returns the items of this collection that are NOT in {@code listToCompare} (difference), keeping their order.
	 * @param <T> type of the items
	 * @param listToCompare the collection to compare with
	 * @return the items exclusive to this collection
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getExclusiveList(Collection<T> listToCompare){
		Predicate<? super Object> p = x ->  false == listToCompare.contains(x);
		var stream = this.content.stream();
		var filter = stream.filter(p);
		var collect2 = filter.collect(Collectors.toList());
		List<Object> collect = new ArrayList<Object>(collect2);
		List<T> listT = (List<T>)collect;
		return listT;
	}

	/**
	 * Returns the items of this collection that are also in {@code listToCompare} (intersection), keeping their order.
	 * @param <T> type of the items
	 * @param listToCompare the collection to compare with
	 * @return the items present in both collections
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getIntersectList(Collection<T> listToCompare){
		Predicate<? super Object> p = x -> listToCompare.contains(x);
		var stream2 = this.content.stream();
		var filter2 = stream2.filter(p);
		var collect3 = filter2.collect(Collectors.toList());
		ArrayList<Object> arrayList2 = new ArrayList<Object>(collect3);
		List<T> collect = (List<T> )arrayList2;
		return collect;
	}
	
	/**
	 * Tells whether both collections have at least one item in common.
	 * @param <T> type of the items
	 * @param listToCompare the collection to compare with
	 * @return {@code true} when the intersection is not empty
	 */
	public <T> boolean hasIntersect(Collection<T> listToCompare) {
		List<T> intersectList = this.getIntersectList(listToCompare);
		boolean intersectListEmpty = intersectList.isEmpty();
		boolean hasIntersection = false == intersectListEmpty;
		return hasIntersection;
	}
	
	/**
	 * Returns a copy of the items between {@code start} (inclusive) and {@code end} (exclusive); an {@code end} beyond the
	 * size is reduced to the size.
	 * @param start start index (inclusive)
	 * @param end end index (exclusive)
	 * @return the slice
	 * @throws IndexOutOfBoundsException when {@code start} is negative or greater than the adjusted {@code end}
	 */
	public CcpCollectionDecorator getSubCollection(int start, int end) {
		int contentSize2 = this.content.size();
		boolean endBeyondSize = end > contentSize2;
		if(endBeyondSize) {
			end = this.content.size();
		}
		
		ArrayList<Object> arrayList = new ArrayList<>(this.content);
		List<Object> subList = arrayList.subList(start, end);
		CcpCollectionDecorator ccpCollectionDecorator = new CcpCollectionDecorator(subList);
		return ccpCollectionDecorator;
	}

	/**
	 * Returns the wrapped collection.
	 * @return the collection
	 */
	public Collection<Object> getContent() {
		return this.content;
	}
	
	
}
