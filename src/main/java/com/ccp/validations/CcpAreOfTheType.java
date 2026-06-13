package com.ccp.validations;

import java.util.function.Function;
import java.util.function.Predicate;

import com.ccp.decorators.CcpStringDecorator;

/**
 * Componente da API fluente de validação. Dado um conjunto de campos de um JSON, verifica se todos
 * os valores são de um tipo específico (booleano, double, JSON, lista, long).
 */
public class CcpAreOfTheType {
	public final Function<String[], String[]> arrayProducer;
	public final String[] fields;

	/** Armazena o produtor de valores e os campos a serem verificados. */
	public CcpAreOfTheType(Function<String[], String[]> arrayProducer, String[] fields) {
		this.arrayProducer = arrayProducer;
		this.fields = fields;
	}

	/** Retorna {@code true} se todos os valores dos campos forem booleanos. */
	public boolean bool() {
		boolean true1 = this.isTrue(x -> x.isBoolean());
		return true1;
	}
	/** Retorna {@code true} se todos os valores dos campos forem números double. */
	public boolean doubleNumber() {
		boolean true1 = this.isTrue(x -> x.isDoubleNumber());
		return true1;
	}
	/** Retorna {@code true} se todos os valores dos campos forem JSONs (mapas aninhados). */
	public boolean json() {
		boolean true1 = this.isTrue(x -> x.isInnerJson());
		return true1;
	}
	/** Retorna {@code true} se todos os valores dos campos forem listas. */
	public boolean list() {
		boolean true1 = this.isTrue(x -> x.isList());
		return true1;
	}
	/** Retorna {@code true} se todos os valores dos campos forem números long. */
	public boolean longNumber() {
		boolean true1 = this.isTrue(x -> x.isLongNumber());
		return true1;
	}
	
	private boolean isTrue(Predicate<CcpStringDecorator> predicate) {
		
		String[] values = this.arrayProducer.apply(this.fields);
		
		for (String value : values) {
			CcpStringDecorator t = new CcpStringDecorator(value);
			boolean isFalse = false == predicate.test(t);
			if(isFalse) {
				return false;
			}
		}
		return true;
	}

}
