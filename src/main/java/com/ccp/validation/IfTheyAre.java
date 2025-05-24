package com.ccp.validation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;

public class IfTheyAre {
	public final CcpJsonRepresentation content;
	public final String[] fields;
	public IfTheyAre(CcpJsonRepresentation content, String[] fields) {
		this.content = content;
		this.fields = fields;
	}
	
	public boolean textsThenEachOneIsContainedAtTheList(String ...args) {
		
		List<String> asList = Arrays.asList(args);
		
		for (String field : this.fields) {
			
			String asString = this.content.getAsString(field);
			
			boolean notAllowedValue = asList.contains(asString) == false;
			if(notAllowedValue) {
				return false;
			}
		}
		return true;
	}
	
	public <T> boolean textsThenEachOneIsContainedAtTheList(Collection<T> args) {
		List<String> collect = args.stream().map(x -> x.toString()).collect(Collectors.toList());
		String[] array = collect.toArray(new String[collect.size()]);
		boolean result = this.textsThenEachOneIsContainedAtTheList(array);
		return result;
	}	

	public RangeSize textsThenEachOneHasTheSizeThatIs() {
		Function<String[], String[]> arrayProducer = fields -> {
			String[] result = new String[fields.length];
			int k = 0;
			for (String field : fields) {
				String asString = this.content.getAsString(field);
				result[k++] = "" + asString.length();
			}
			return result;
		};
		return new RangeSize(arrayProducer, this.fields);
	}
	public RangeSize numbersThenEachOneIs() {
		Function<String[], String[]> arrayProducerOfItems = ItIsTrueThatTheFollowingFields.getArrayProducerOfItems(this.content);
		return new RangeSize(arrayProducerOfItems, this.fields);
	}

	public boolean numbersThenEachOneIsContainedAtTheList(Double... args) {

		List<Double> asList = Arrays.asList(args);
		
		for (String field : this.fields) {

			Double value = this.content.getAsDoubleNumber(field);
			
			boolean notAllowedValue = asList.contains(value) == false;
			if(notAllowedValue) {
				return false;
			}
		}
		return true;
	}

	public boolean numbersThenEachOneIsContainedAtTheList(Collection<Object> args) {
		List<Double> collect = args.stream().map(x -> Double.valueOf(x.toString())).collect(Collectors.toList());
		Double[] array = collect.toArray(new Double[collect.size()]);
		boolean result = this.numbersThenEachOneIsContainedAtTheList(array);
		return result;
	}

	public boolean textsThenEachOneMatchesWithTheFollowingRegex(String regex) {

		for (String field : this.fields) {
			
			String value = this.content.getAsString(field);
			CcpStringDecorator csd = new CcpStringDecorator(value);
			CcpTextDecorator text = csd.text();
			
			boolean regexDoesNotMatch = text.regexMatches(regex) == false;
			
			if(regexDoesNotMatch) {
				return false;
			}
		}
		return true;
	}

}
