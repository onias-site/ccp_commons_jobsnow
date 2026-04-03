package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.function.Function;

import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityAsyncWriter;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCache;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDisposable;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsTransformer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOlyReadable;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperations;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityVersionable;

enum DecoratorEntityEnum {
	Disposable(CcpEntityDisposable.class, x -> x.getAnnotation(CcpEntityDisposable.class).expurgableEntityFactory(), 1),
	Versionable(CcpEntityVersionable.class, x -> x.getAnnotation(CcpEntityVersionable.class).value(), 2),
	AsyncWriter(CcpEntityAsyncWriter.class, x -> x.getAnnotation(CcpEntityAsyncWriter.class).value(), 6),
	FieldsTransformer(CcpEntityFieldsTransformer.class, x -> DecoratorFieldsTransformerEntity.class, 8),
	FieldsValidator(CcpEntityFieldsValidator.class, x -> DecoratorFieldsValidatorEntity.class, 9),
	Operations(CcpEntityOperations.class, x -> DecoratorOperationsWriterEntity.class, 4),
	Transfers(CcpEntityOperations.class, x -> DecoratorTransferDataEntity.class, 4),
	ReadOnly(CcpEntityOlyReadable.class, x -> DecoratorReadOnlyEntity.class, 7),
	Cache(CcpEntityCache.class, x -> DecoratorCacheEntity.class, 3),
	Twin(CcpEntityTwin.class, x -> DecoratorTwinEntity.class, 5),
	;

	

	private DecoratorEntityEnum(Class<? extends Annotation> annotation, Function<Class<?>, Class<?>> clazzProducer, int priority) {
		this.clazzProducer = clazzProducer;
		this.annotation = annotation;
		this.priority = priority;
	}
	public final Function<Class<?>, Class<?>> clazzProducer;
	private final Class<? extends Annotation> annotation;
	public final int priority;
	
	boolean isDecorated(Class<?> clazz) {
		boolean annotationPresent = clazz.isAnnotationPresent(this.annotation);
		return annotationPresent;
	}
	
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
