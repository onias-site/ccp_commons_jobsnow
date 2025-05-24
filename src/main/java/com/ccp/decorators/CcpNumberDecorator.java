package com.ccp.decorators;

import java.util.Collection;

public class CcpNumberDecorator implements CcpDecorator<Double> {
	public final double content;

	public CcpNumberDecorator(String content) {
		this.content = Double.valueOf(content);
	}

	public String toString() {
		return "" + this.content;
	}
	public boolean greaterThan(Double x) {
		return this.content > x ;
	}

	public boolean equalsOrGreaterThan(Double x) {
		return this.content >= x ;
	}

	public boolean lessThan(Double x) {
		return this.content < x ;
	}

	public boolean equalsOrLessThan(Double x) {
		return this.content <= x ;
	}

	public boolean equalsTo(Double x) {
		return this.content == x ;
	}
	
	public boolean belongsToRestrictedValues(Double...restrictedValues) {
		for (double restricted : restrictedValues) {
			if(restricted == this.content) {
				return true;
			}
		}
		return false;
	}

	public boolean belongsToRestrictedValues(Collection<Double> restrictedValues) {
		int size = restrictedValues.size();
		Double[] a = new Double[size];
		Double[] array = restrictedValues.toArray(a);
		return this.belongsToRestrictedValues(array);
	}

	public Double getContent() {
		return this.content;
	}	
}
