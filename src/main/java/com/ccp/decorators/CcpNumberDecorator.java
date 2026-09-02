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
		String valorMais = "" + this.content;
		return valorMais;
	}

	/**
	 * Retorna {@code true} se o valor for estritamente maior que {@code x}.
	 */
	public boolean greaterThan(Double x) {
		boolean contentMaior = this.content > x;
		return contentMaior ;
	}

	/**
	 * Retorna {@code true} se o valor for maior ou igual a {@code x}.
	 */
	public boolean equalsOrGreaterThan(Double x) {
		boolean contentMaiorOuIgual = this.content >= x;
		return contentMaiorOuIgual ;
	}

	/**
	 * Retorna {@code true} se o valor for estritamente menor que {@code x}.
	 */
	public boolean lessThan(Double x) {
		boolean contentMenor = this.content < x;
		return contentMenor ;
	}

	/**
	 * Retorna {@code true} se o valor for menor ou igual a {@code x}.
	 */
	public boolean equalsOrLessThan(Double x) {
		boolean contentMenorOuIgual = this.content <= x;
		return contentMenorOuIgual ;
	}

	/**
	 * Retorna {@code true} se o valor for exatamente igual a {@code x}.
	 */
	public boolean equalsTo(Double x) {
		boolean contentIgual = this.content == x;
		return contentIgual ;
	}

	/**
	 * Retorna {@code true} se o valor estiver em um conjunto fixo de valores permitidos (varargs).
	 */
	public boolean belongsToRestrictedValues(Double...restrictedValues) {
		for (double restricted : restrictedValues) {
			boolean restrictedIgual = restricted == this.content;
			if(restrictedIgual) {
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
		boolean belongsToRestrictedValues = this.belongsToRestrictedValues(array);
		return belongsToRestrictedValues;
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna o valor como {@code Double}.
	 */
	public Double getContent() {
		return this.content;
	}
}
