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

	@SuppressWarnings("serial")
	public static class CcpEntityCustomDecoratorIsNotDeclared extends RuntimeException {
		private CcpEntityCustomDecoratorIsNotDeclared(Class<?> configurationClass, Class<?> thisDecorator) {
			super("The class '" + configurationClass.getName() + "' does not declare the decorator '" + thisDecorator.getName() + "' in the annotation '" + CcpEntityCustomDecorators.class.getSimpleName() + "'");
		}
	}

}
