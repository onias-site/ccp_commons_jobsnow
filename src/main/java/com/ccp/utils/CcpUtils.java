package com.ccp.utils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class CcpUtils {
	
	@SuppressWarnings("unchecked")
	public static <T> T[] incrementAnArray(T[] anArray, T...items) {
		
		
		List<T> asList = Arrays.asList(anArray);
		LinkedHashSet<T> set = new LinkedHashSet<>(asList);
		for (T t : items) {
			set.add(t);
		}
		
		T[] array = (T[])set.toArray(new Object[set.size()]);
		return array;
	}
	
}
