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
 * Cataloga todos os tipos de decorator disponíveis para entidades, associando cada um à sua
 * anotação, à classe de decorator e a uma prioridade de encadeamento. A prioridade determina a
 * ordem de aplicação ao construir a entidade final via {@code CcpEntityFactory}.
 *
 * <p>Os side effects de escrita e de transferência de dados têm um item de enum por fase: o
 * {@code Before*} recebe prioridade alta para ficar na parte externa da cadeia (executa antes de
 * tudo) e o {@code After*} recebe prioridade baixa para ficar na parte interna (executa depois que a
 * gravação ou a transferência de fato aconteceu). Uma única anotação ({@code @CcpEntityOperations} ou
 * {@code @CcpEntityDataTransfers}) ativa os dois itens do par.
 *
 * <p>O topo da faixa segue uma ordem deliberada. O {@code FieldsValidator} fica em 10 porque precisa
 * reprovar a entrada antes de qualquer outra coisa. Logo abaixo dele, e acima de todo o resto, vem o
 * {@code DataReadOnly} em 9: entidade somente leitura recusa a escrita, então a recusa tem que
 * acontecer antes de qualquer side effect, antes de transformar campo e antes de publicar mensagem em
 * fila. A prioridade <b>8 é reservada para decorators que cortam a cadeia e republicam a operação</b>
 * — hoje o {@code JnAsyncWriterEntity}, que serializa o JSON e o envia para a mensageria. Quem corta a
 * cadeia tem que receber o JSON <b>cru</b>: se ficar por dentro do {@code FieldsTransformer}, o que vai
 * para a fila já vem transformado e o {@code FieldsValidator} reprova a mensagem quando ela volta. Por
 * isso nenhum item deste enum usa a prioridade 8.
 *
 * <p>Atenção ao empate: quando dois decorators têm a mesma prioridade, o custom fica <b>por fora</b> do
 * item de enum, porque {@code CcpEntityFactory} concatena os customs depois dos itens de enum e a
 * ordenação é estável. Não use empate para expressar ordem — dê prioridades distintas.
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
	
	/** Retorna {@code true} se a anotação deste tipo de decorator está presente em {@code clazz}. */
	public boolean isAnnoted(Class<?> clazz) {
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
			var clazzClass = clazz.getClass();
			Constructor<?> declaredConstructor = apply.getDeclaredConstructor(CcpEntity.class, clazzClass);
			declaredConstructor.setAccessible(true);
			var newInstance2 = declaredConstructor.newInstance(decoratedEntity, clazz);
			CcpEntity newInstance = (CcpEntity)newInstance2;
			return newInstance;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getPriority(Class<?> configurationClass) {
		return this.priority;
	}
}
