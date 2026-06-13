package com.ccp.validations;

import java.util.function.Function;
import java.util.function.Predicate;

import com.ccp.decorators.CcpNumberDecorator;

/**
 * Componente terminal da API fluente de validação. Aplica uma comparação numérica sobre um conjunto
 * de valores produzidos por {@code arrayProducer} (que pode representar tamanhos de texto ou valores
 * numéricos), retornando {@code true} apenas se todos satisfizerem a condição.
 */
public class CcpRangeSize {
	public final Function<String[], String[]> arrayProducer;
	public final String[] fields;

	/** Armazena o produtor de valores e os campos a serem verificados. */
	public CcpRangeSize(Function<String[], String[]> arrayProducer, String[] fields) {
		this.arrayProducer = arrayProducer;
		this.fields = fields;
	}

	/**
	 * Retorna {@code true} se todos os valores forem iguais a {@code y}.
	 * @param y o valor de referência para comparação
	 */
	public boolean equalsTo(Double y) {
		boolean true1 = this.isTrue(x -> x.equalsTo(y));
		return true1;
	}

	/**
	 * Retorna {@code true} se todos os valores forem maiores ou iguais a {@code y}.
	 * @param y o valor mínimo inclusivo
	 */
	public boolean equalsOrGreaterThan(Double y) {
		boolean true1 = this.isTrue(x -> x.equalsOrGreaterThan(y));
		return true1;
	}

	/**
	 * Retorna {@code true} se todos os valores forem menores ou iguais a {@code y}.
	 * @param y o valor máximo inclusivo
	 */
	public boolean equalsOrLessThan(Double y) {
		boolean true1 = this.isTrue(x -> x.equalsOrLessThan(y));
		return true1;
	}

	/**
	 * Retorna {@code true} se todos os valores forem estritamente maiores que {@code y}.
	 * @param y o valor de referência exclusivo
	 */
	public boolean greaterThan(Double y) {
		boolean true1 = this.isTrue(x -> x.greaterThan(y));
		return true1;
	}

	/**
	 * Retorna {@code true} se todos os valores forem estritamente menores que {@code y}.
	 * @param y o valor de referência exclusivo
	 */
	public boolean lessThan(Double y) {
		boolean true1 = this.isTrue(x -> x.lessThan(y));
		return true1;
	}

	
	private boolean isTrue(Predicate<CcpNumberDecorator> predicate) {
		String[] apply = this.arrayProducer.apply(this.fields);
		for (String string : apply) {
			CcpNumberDecorator cnd = new CcpNumberDecorator(string);
			boolean hasFailed = false == predicate.test(cnd);
			if(hasFailed) {
				return false;
			}
		}
		
		return true;
	}
	
}
