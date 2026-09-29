package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.function.Function;

import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCache;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDataTransfers;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsTransformer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOlyReadable;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperations;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;

/**
 * Catalogs every decorator type available for entities, associating each one with its
 * annotation, its decorator class and a chaining priority. The priority determines the
 * order of application when the final entity is built through {@code CcpEntityFactory}.
 *
 * <p>The write and data transfer side effects have one enum item per phase: the
 * {@code Before*} gets a high priority to stay in the outer part of the chain (it runs before
 * everything) and the {@code After*} gets a low priority to stay in the inner part (it runs after the
 * write or the transfer has actually happened). A single annotation ({@code @CcpEntityOperations} or
 * {@code @CcpEntityDataTransfers}) activates both items of the pair.
 *
 * <p>The top of the range follows a deliberate order. {@code FieldsValidator} is at 10 because it must
 * reject the input before anything else. Right below it, and above everything else, comes
 * {@code DataReadOnly} at 9: a read-only entity refuses the write, so the refusal must
 * happen before any side effect, before transforming fields and before publishing a message to a
 * queue. Priority <b>8 is reserved for decorators that cut the chain and republish the operation</b>
 * — today {@code JnAsyncWriterEntity}, which serializes the JSON and sends it to the messaging system. Whoever cuts the
 * chain must receive the <b>raw</b> JSON: if it stays inside {@code FieldsTransformer}, what goes
 * to the queue is already transformed and {@code FieldsValidator} rejects the message when it comes back. That is
 * why no item of this enum uses priority 8.
 *
 * <p>Beware of ties: when two decorators have the same priority, the custom one stays <b>outside</b> the
 * enum item, because {@code CcpEntityFactory} appends the customs after the enum items and the
 * sort is stable. Do not use ties to express order — give distinct priorities.
 */
public enum CcpEntityDecoratorTypes implements CcpEntityDecoratorType{
	FieldsTransformer(CcpEntityFieldsTransformer.class, x -> DecoratorFieldsTransformerEntity.class, 6),
	FieldsValidator(CcpEntityFieldsValidator.class, x -> DecoratorFieldsValidatorEntity.class, 10),
	BeforeWriteOperations(CcpEntityOperations.class, x -> DecoratorBeforeOperationsWriterEntity.class, 7),
	AfterWriteOperations(CcpEntityOperations.class, x -> DecoratorAfterOperationsWriterEntity.class, 5),
	BeforeDataTransfer(CcpEntityDataTransfers.class, x -> DecoratorBeforeTransferDataEntity.class, 7),
	AfterDataTransfer(CcpEntityDataTransfers.class, x -> DecoratorAfterTransferDataEntity.class, 5),
	DataReadOnly(CcpEntityOlyReadable.class, x -> DecoratorReadOnlyEntity.class, 9),
	Cacheable(CcpEntityCache.class, x -> DecoratorCacheEntity.class, 3),
	Twin(CcpEntityTwin.class, x -> DecoratorTwinEntity.class, 4),
	;

	

	private CcpEntityDecoratorTypes(Class<? extends Annotation> annotation, Function<Class<?>, Class<?>> clazzProducer, int priority) {
		this.clazzProducer = clazzProducer;
		this.annotation = annotation;
		this.priority = priority;
	}
	public final Function<Class<?>, Class<?>> clazzProducer;
	private final Class<? extends Annotation> annotation;
	private final int priority;
	
	/** Returns {@code true} if the annotation of this decorator type is present on {@code clazz}. */
	public boolean isAnnoted(Class<?> clazz) {
		boolean annotationPresent = clazz.isAnnotationPresent(this.annotation);
		return annotationPresent;
	}
	
	/**
	 * Instantiates the decorator corresponding to this type for the given entity.
	 * @param clazz the entity's configurator class (the one carrying the annotations)
	 * @param decoratedEntity the entity already decorated up to this point of the chain
	 */
	public CcpEntity getEntity(Class<?> clazz, CcpEntity decoratedEntity) {
		try {
			Class<?> decoratorClass = this.clazzProducer.apply(clazz);
			var configurationClassType = clazz.getClass();
			Constructor<?> declaredConstructor = decoratorClass.getDeclaredConstructor(CcpEntity.class, configurationClassType);
			declaredConstructor.setAccessible(true);
			var decoratorInstance = declaredConstructor.newInstance(decoratedEntity, clazz);
			CcpEntity decorator = (CcpEntity)decoratorInstance;
			return decorator;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getPriority(Class<?> configurationClass) {
		return this.priority;
	}
}
