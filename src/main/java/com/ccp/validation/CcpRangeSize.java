package com.ccp.validation;

import java.util.function.Function;
import java.util.function.Predicate;

import com.ccp.decorators.CcpNumberDecorator;

public class CcpRangeSize {
	public final Function<String[], String[]> arrayProducer;
	public final String[] fields;
	public CcpRangeSize(Function<String[], String[]> arrayProducer, String[] fields) {
		this.arrayProducer = arrayProducer;
		this.fields = fields;
	}

	public boolean equalsTo(Double y) {
		boolean true1 = this.isTrue(x -> x.equalsTo(y));
		return true1;
	}

	public boolean equalsOrGreaterThan(Double y) {
		boolean true1 = this.isTrue(x -> x.equalsOrGreaterThan(y));
		return true1;
	}

	public boolean equalsOrLessThan(Double y) {
		boolean true1 = this.isTrue(x -> x.equalsOrLessThan(y));
		return true1;
	}

	public boolean greaterThan(Double y) {
		boolean true1 = this.isTrue(x -> x.greaterThan(y));
		return true1;
	}

	public boolean lessThan(Double y) {
		boolean true1 = this.isTrue(x -> x.lessThan(y));
		return true1;
	}

	
	private boolean isTrue(Predicate<CcpNumberDecorator> predicate) {
		String[] apply = this.arrayProducer.apply(this.fields);
		for (String string : apply) {
			CcpNumberDecorator cnd = new CcpNumberDecorator(string);
			boolean hasFailed = predicate.test(cnd) == false;
			if(hasFailed) {
				return false;
			}
		}
		
		return true;
	}
	
}
