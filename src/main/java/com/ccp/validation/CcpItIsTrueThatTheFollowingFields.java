package com.ccp.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpNumberDecorator;

public class CcpItIsTrueThatTheFollowingFields {

	public final CcpJsonRepresentation content;
	public final String[] fields;

	public CcpItIsTrueThatTheFollowingFields(CcpJsonRepresentation content, String[] fields) {
		this.content = content;
		List<String> collect = Arrays.asList(fields).stream().filter(x -> content.getDynamicVersion().containsField(x)).collect(Collectors.toList());
		this.fields = collect.toArray(new String[collect.size()]);
	}

	public CcpAreOfTheType areAllOfTheType() {
		Function<String[], String[]> arrayProducerOfItems = CcpItIsTrueThatTheFollowingFields.getArrayProducerOfItems(this.content);
		return new CcpAreOfTheType(arrayProducerOfItems, this.fields);
	}

	public CcpIfTheyAreArrayValuesSoEachOne ifTheyAreAllArrayValuesThenEachOne() {
		return new CcpIfTheyAreArrayValuesSoEachOne(this.content, this.fields);
	}

	public CcpIfTheyAre ifTheyAreAll() {
		return new CcpIfTheyAre(this.content, this.fields);
	}
	
	public static Function<String[], String[]> getArrayProducerOfArrays(CcpJsonRepresentation content) {
		Function<String[], String[]> arrayProcucer = fields ->{
			List<String> list = new ArrayList<String>();
			for (String field : fields) {
				List<String> asStringList = content.getDynamicVersion().getAsStringList(field);
				list.addAll(asStringList);
			}
			String[] array = list.toArray(new String[list.size()]);
			return array;
		};
		
		return arrayProcucer;

	}
	
	public static Function<String[], String[]> getArrayProducerOfItems(CcpJsonRepresentation content) {
		Function<String[], String[]> arrayProducer = fields -> {
			String [] result = new String[fields.length];	
			int k = 0;
			for (String field : fields) {
				String value = content.getDynamicVersion().getAsString(field);
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
