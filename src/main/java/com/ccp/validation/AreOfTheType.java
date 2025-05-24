package com.ccp.validation;

import java.util.function.Function;
import java.util.function.Predicate;

import com.ccp.decorators.CcpStringDecorator;

public class AreOfTheType {
	public final Function<String[], String[]> arrayProducer;
	public final String[] fields;

	
	
	public AreOfTheType(Function<String[], String[]> arrayProducer, String[] fields) {
		this.arrayProducer = arrayProducer;
		this.fields = fields;
	}
	public boolean bool() {
		boolean true1 = this.isTrue(x -> x.isBoolean());
		return true1;
	}
	public boolean doubleNumber() {
		boolean true1 = this.isTrue(x -> x.isDoubleNumber());
		return true1;
	}
	public boolean json() {
		boolean true1 = this.isTrue(x -> x.isInnerJson());
		return true1;
	}
	public boolean list() {
		boolean true1 = this.isTrue(x -> x.isList());
		return true1;
	}
	public boolean longNumber() {
		boolean true1 = this.isTrue(x -> x.isLongNumber());
		return true1;
	}
	
	private boolean isTrue(Predicate<CcpStringDecorator> predicate) {
		
		String[] values = this.arrayProducer.apply(this.fields);
		
		for (String value : values) {
			CcpStringDecorator t = new CcpStringDecorator(value);
			boolean isFalse = predicate.test(t) == false;
			if(isFalse) {
				return false;
			}
		}
		return true;
	}

}
