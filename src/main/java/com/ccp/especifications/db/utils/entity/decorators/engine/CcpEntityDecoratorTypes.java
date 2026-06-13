package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.function.Function;

import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityAsyncWriter;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCache;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDataTransfers;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDisposable;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsTransformer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOlyReadable;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperations;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityVersionable;

/**
 * Cataloga todos os tipos de decorator disponíveis para entidades, associando cada um à sua
 * anotação, à classe de decorator e a uma prioridade de encadeamento. A prioridade determina a
 * ordem de aplicação ao construir a entidade final via {@code CcpEntityFactory}.
 */
public enum CcpEntityDecoratorTypes {
	Disposable(CcpEntityDisposable.class, x -> x.getAnnotation(CcpEntityDisposable.class).expurgableEntityFactory(), 1),
	Versionable(CcpEntityVersionable.class, x -> x.getAnnotation(CcpEntityVersionable.class).value(), 2),
	AsyncWriter(CcpEntityAsyncWriter.class, x -> x.getAnnotation(CcpEntityAsyncWriter.class).value(), 6),
	FieldsTransformer(CcpEntityFieldsTransformer.class, x -> DecoratorFieldsTransformerEntity.class, 8),
	FieldsValidator(CcpEntityFieldsValidator.class, x -> DecoratorFieldsValidatorEntity.class, 9),
	WriteOperations(CcpEntityOperations.class, x -> DecoratorOperationsWriterEntity.class, 4),
	DataTransfer(CcpEntityDataTransfers.class, x -> DecoratorTransferDataEntity.class, 4),
	DataReadOnly(CcpEntityOlyReadable.class, x -> DecoratorReadOnlyEntity.class, 7),
	Cacheable(CcpEntityCache.class, x -> DecoratorCacheEntity.class, 3),
	Twin(CcpEntityTwin.class, x -> DecoratorTwinEntity.class, 5),
	;

	

	private CcpEntityDecoratorTypes(Class<? extends Annotation> annotation, Function<Class<?>, Class<?>> clazzProducer, int priority) {
		this.clazzProducer = clazzProducer;
		this.annotation = annotation;
		this.priority = priority;
	}
	public final Function<Class<?>, Class<?>> clazzProducer;
	private final Class<? extends Annotation> annotation;
	public final int priority;
	
	/** Retorna {@code true} se a anotação deste tipo de decorator está presente em {@code clazz}. */
	boolean isDecorated(Class<?> clazz) {
		boolean annotationPresent = clazz.isAnnotationPresent(this.annotation);
		return annotationPresent;
	}
	
	/**
	 * Instancia o decorator correspondente a este tipo para a entidade informada.
	 * @param clazz a classe configuradora da entidade (portadora das anotações)
	 * @param decoratedEntity a entidade já decorada até este ponto da cadeia
	 */
	public CcpEntity getEntity(Class<?> clazz, CcpEntity decoratedEntity) {
		
		try {
			Class<?> apply = this.clazzProducer.apply(clazz);
			Constructor<?> declaredConstructor = apply.getDeclaredConstructor(CcpEntity.class, clazz.getClass());
			declaredConstructor.setAccessible(true);
			CcpEntity newInstance = (CcpEntity)declaredConstructor.newInstance(decoratedEntity, clazz);
			return newInstance;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
