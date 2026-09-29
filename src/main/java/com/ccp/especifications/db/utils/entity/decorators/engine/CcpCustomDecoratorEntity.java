package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCustomDecorator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCustomDecorators;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;

public abstract class CcpCustomDecoratorEntity implements CcpEntityDecoratorType{

	/**
	 * Finds, within {@code @CcpEntityCustomDecorators}, the item whose {@code value()} is the class
	 * of this decorator and returns the priority declared there. The {@code @CcpEntityCustomDecorator} annotation
	 * is not applied directly to the configurator class: it only exists nested in the container.
	 */
	public final int getPriority(Class<?> configurationClass) {
		CcpEntityCustomDecorators annotation = configurationClass.getAnnotation(CcpEntityCustomDecorators.class);
		CcpEntityCustomDecorator[] customDecorators = annotation.value();
		Class<?> thisDecorator = this.getClass();

		for (CcpEntityCustomDecorator customDecorator : customDecorators) {
			Class<?> builderClass = customDecorator.value();
			boolean isThisDecorator = builderClass.equals(thisDecorator);

			boolean isAnotherDecorator = false == isThisDecorator;

			if(isAnotherDecorator) {
				continue;
			}
			int priority = customDecorator.priority();
			return priority;
		}

		CcpEntityCustomDecoratorIsNotDeclared notDeclaredError = new CcpEntityCustomDecoratorIsNotDeclared(configurationClass, thisDecorator);
		throw notDeclaredError;
	}

	/**
	 * Two custom decorators are the same decorator when they are of the same class. Identity does not work:
	 * {@code CcpEntityFactory} instantiates each builder through reflection every time an entity is assembled, so the
	 * object passed in {@code decoratorsToAvoid} is never the same one that is in the chain. Without this
	 * comparison by class, the {@code contains} of the exclusion filter always returns {@code false} and the
	 * decorator that was asked to be avoided stays in the chain.
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
