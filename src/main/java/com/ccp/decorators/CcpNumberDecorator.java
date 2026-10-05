package com.ccp.decorators;

import java.util.Collection;

/**
 * Decorator over a numeric value ({@code double}) offering comparisons. It receives the number as text and converts
 * it to {@code double}, which eases range validations.
 */
public class CcpNumberDecorator implements CcpDecorator<Double> {
	/** The wrapped number. */
	public final double content;

	/**
	 * Parses the text as {@code double}.
	 * @param content the number as text
	 * @throws NumberFormatException when the text is not a number
	 */
	public CcpNumberDecorator(String content) {
		this.content = Double.valueOf(content);
	}

	/**
	 * Returns the number as text, in {@code double} format (e.g. {@code "3.0"}).
	 * @return the number as text
	 */
	public String toString() {
		String contentAsText = "" + this.content;
		return contentAsText;
	}

	/**
	 * Tells whether the number is strictly greater than {@code x}.
	 * @param x the value to compare with
	 * @return {@code true} when the number is greater than {@code x}
	 */
	public boolean greaterThan(Double x) {
		boolean greaterThan = this.content > x;
		return greaterThan ;
	}

	/**
	 * Tells whether the number is greater than or equal to {@code x}.
	 * @param x the value to compare with
	 * @return {@code true} when the number is greater than or equal to {@code x}
	 */
	public boolean equalsOrGreaterThan(Double x) {
		boolean greaterThanOrEqual = this.content >= x;
		return greaterThanOrEqual ;
	}

	/**
	 * Tells whether the number is strictly less than {@code x}.
	 * @param x the value to compare with
	 * @return {@code true} when the number is less than {@code x}
	 */
	public boolean lessThan(Double x) {
		boolean lessThan = this.content < x;
		return lessThan ;
	}

	/**
	 * Tells whether the number is less than or equal to {@code x}.
	 * @param x the value to compare with
	 * @return {@code true} when the number is less than or equal to {@code x}
	 */
	public boolean equalsOrLessThan(Double x) {
		boolean lessThanOrEqual = this.content <= x;
		return lessThanOrEqual ;
	}

	/**
	 * Tells whether the number is numerically equal to {@code x}.
	 * @param x the value to compare with
	 * @return {@code true} when both values are equal
	 */
	public boolean equalsTo(Double x) {
		boolean equal = this.content == x;
		return equal ;
	}

	/**
	 * Tells whether the number equals one of the allowed values.
	 * @param restrictedValues the allowed values
	 * @return {@code true} when the number is one of them
	 */
	public boolean belongsToRestrictedValues(Double...restrictedValues) {
		for (double restricted : restrictedValues) {
			boolean isRestrictedValue = restricted == this.content;
			if(isRestrictedValue) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tells whether the number equals one of the allowed values.
	 * @param restrictedValues the allowed values
	 * @return {@code true} when the number is one of them
	 */
	public boolean belongsToRestrictedValues(Collection<Double> restrictedValues) {
		int size = restrictedValues.size();
		Double[] a = new Double[size];
		Double[] array = restrictedValues.toArray(a);
		boolean belongsToRestrictedValues = this.belongsToRestrictedValues(array);
		return belongsToRestrictedValues;
	}

	/**
	 * Returns the wrapped number.
	 * @return the number
	 */
	public Double getContent() {
		return this.content;
	}
}
