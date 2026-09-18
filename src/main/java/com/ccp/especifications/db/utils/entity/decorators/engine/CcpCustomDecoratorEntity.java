package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCustomDecorator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCustomDecorators;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;

public abstract class CcpCustomDecoratorEntity implements CcpEntityDecoratorType{

	/**
	 * Localiza, dentro de {@code @CcpEntityCustomDecorators}, o item cujo {@code value()} é a classe
	 * deste decorator e devolve a prioridade ali declarada. A anotação {@code @CcpEntityCustomDecorator}
	 * não é aplicada diretamente na classe configuradora: ela só existe aninhada no container.
	 */
	public final int getPriority(Class<?> configurationClass) {
		CcpEntityCustomDecorators annotation = configurationClass.getAnnotation(CcpEntityCustomDecorators.class);
		CcpEntityCustomDecorator[] customDecorators = annotation.value();
		Class<?> thisDecorator = this.getClass();

		for (CcpEntityCustomDecorator customDecorator : customDecorators) {
			Class<?> builderClass = customDecorator.value();
			boolean builderClassEquals = builderClass.equals(thisDecorator);

			boolean isAnotherDecorator = false == builderClassEquals;

			if(isAnotherDecorator) {
				continue;
			}
			int priority = customDecorator.priority();
			return priority;
		}

		CcpEntityCustomDecoratorIsNotDeclared ccpEntityCustomDecoratorIsNotDeclared = new CcpEntityCustomDecoratorIsNotDeclared(configurationClass, thisDecorator);
		throw ccpEntityCustomDecoratorIsNotDeclared;
	}

	/**
	 * Dois decorators custom são o mesmo decorator quando são da mesma classe. A identidade não serve:
	 * {@code CcpEntityFactory} instancia cada builder por reflexão a cada montagem de entidade, então o
	 * objeto passado em {@code decoratorsToAvoid} nunca é o mesmo que está na cadeia. Sem esta
	 * comparação por classe o {@code contains} do filtro de exclusão devolve sempre {@code false} e o
	 * decorator que se pediu para evitar continua na cadeia.
	 */
	public final boolean equals(Object obj) {

		boolean isNull = obj == null;

		if(isNull) {
			return false;
		}

		Class<?> thisDecorator = this.getClass();
		Class<?> otherDecorator = obj.getClass();
		boolean sameDecorator = thisDecorator.equals(otherDecorator);
		return sameDecorator;
	}

	public final int hashCode() {
		Class<?> thisDecorator = this.getClass();
		int hashCode = thisDecorator.hashCode();
		return hashCode;
	}

	@SuppressWarnings("serial")
	public static class CcpEntityCustomDecoratorIsNotDeclared extends RuntimeException {
		private CcpEntityCustomDecoratorIsNotDeclared(Class<?> configurationClass, Class<?> thisDecorator) {
			super("The class '" + configurationClass.getName() + "' does not declare the decorator '" + thisDecorator.getName() + "' in the annotation '" + CcpEntityCustomDecorators.class.getSimpleName() + "'");
		}
	}

}
