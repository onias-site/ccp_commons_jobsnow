package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.lang.annotation.Annotation;

import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityAfterDelete;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityAfterDeletePermanently;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityAfterSave;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityAsyncWriter;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityBeforeDelete;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityBeforeDeletePermanently;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityBeforeSave;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCache;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDisposable;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsTransformer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOlyReadable;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityVersionable;

enum DecoratorEntityEnum {
	BeforeDeletePermanently(CcpEntityBeforeDeletePermanently.class, 4){},
	AfterDeletePermanently(CcpEntityAfterDeletePermanently.class, 4){},
	FieldsTransformer(CcpEntityFieldsTransformer.class, 8){},
	FieldsValidator(CcpEntityFieldsValidator.class, 9){},
	BeforeDelete(CcpEntityBeforeDelete.class, 4){},
	Versionable(CcpEntityVersionable.class, 2){},
	AfterDelete(CcpEntityAfterDelete.class, 4){},
	AsyncWriter(CcpEntityAsyncWriter.class, 6){},
	BeforeSave(CcpEntityBeforeSave.class, 4){},
	Disposable(CcpEntityDisposable.class, 1){},
	ReadOnly(CcpEntityOlyReadable.class, 7){},
	AfterSave(CcpEntityAfterSave.class, 4){},
	Cache(CcpEntityCache.class, 3){},
	Twin(CcpEntityTwin.class, 5){},
	;

	

	private DecoratorEntityEnum(Class<? extends Annotation> annotation, int priority) {
		this.annotation = annotation;
		this.priority = priority;
	}
	
	private final Class<? extends Annotation> annotation;
	public final int priority;
	
	boolean isDecorated(Class<?> clazz) {
		boolean annotationPresent = clazz.isAnnotationPresent(this.annotation);
		return annotationPresent;
	}
	
	public CcpEntity2 getEntity(Class<?> clazz, CcpEntity2 decoratedEntity) {
		return null;
	}
	
}
