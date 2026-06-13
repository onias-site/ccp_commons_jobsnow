package com.ccp.decorators;

import java.util.Collection;

/**
 * Decorator sobre um valor numérico ({@code double}) que oferece operações de comparação semântica.
 * Recebe o número como string e converte para {@code double}, facilitando validações de faixa de valores.
 */
public class CcpNumberDecorator implements CcpDecorator<Double> {
	public final double content;

	/**
	 * Converte a string para {@code double}.
	 */
	public CcpNumberDecorator(String content) {
		this.content = Double.valueOf(content);
	}

	/**
	 * Retorna o número como string.
	 */
	public String toString() {
		return "" + this.content;
	}

	/**
	 * Retorna {@code true} se o valor for estritamente maior que {@code x}.
	 */
	public boolean greaterThan(Double x) {
		return this.content > x ;
	}

	/**
	 * Retorna {@code true} se o valor for maior ou igual a {@code x}.
	 */
	public boolean equalsOrGreaterThan(Double x) {
		return this.content >= x ;
	}

	/**
	 * Retorna {@code true} se o valor for estritamente menor que {@code x}.
	 */
	public boolean lessThan(Double x) {
		return this.content < x ;
	}

	/**
	 * Retorna {@code true} se o valor for menor ou igual a {@code x}.
	 */
	public boolean equalsOrLessThan(Double x) {
		return this.content <= x ;
	}

	/**
	 * Retorna {@code true} se o valor for exatamente igual a {@code x}.
	 */
	public boolean equalsTo(Double x) {
		return this.content == x ;
	}

	/**
	 * Retorna {@code true} se o valor estiver em um conjunto fixo de valores permitidos (varargs).
	 */
	public boolean belongsToRestrictedValues(Double...restrictedValues) {
		for (double restricted : restrictedValues) {
			if(restricted == this.content) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retorna {@code true} se o valor estiver em um conjunto fixo de valores permitidos (coleção).
	 */
	public boolean belongsToRestrictedValues(Collection<Double> restrictedValues) {
		int size = restrictedValues.size();
		Double[] a = new Double[size];
		Double[] array = restrictedValues.toArray(a);
		return this.belongsToRestrictedValues(array);
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna o valor como {@code Double}.
	 */
	public Double getContent() {
		return this.content;
	}
}
