package com.ccp.especifications.db.utils.entity.decorators.engine2;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCache;

class DecoratorCacheEntity extends CcpEntityDelegator implements CcpDecoratorEntity<CcpEntityCache>{
	
	final int cacheExpires;
	final Class<?>  clazz;
	
	public DecoratorCacheEntity(CcpEntity2 entity, Class<?> clazz, int cacheExpires) {
		super(entity, 3);
		this.cacheExpires = cacheExpires;
		this.clazz = clazz;
	}
	
	public boolean isThisEntityDecorated(Class<CcpEntityCache> annotation) {
		boolean annotationPresent = this.clazz.isAnnotationPresent(annotation);
		return annotationPresent;
	}

	public CcpEntityCache getAnnotation() {
		CcpEntityCache annotation = this.clazz.getAnnotation(CcpEntityCache.class);
		return annotation;
	}
	private CcpCacheDecorator getCache(CcpJsonRepresentation json) {
		//FIXME
		CcpCacheDecorator ccpCacheDecorator = new CcpCacheDecorator(null, json);
		return ccpCacheDecorator;
	}

	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation delete = this.entity.delete(json);
		
		CcpCacheDecorator cache = this.getCache(json);
		
		cache.delete();
		
		return delete;
	}

	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation delete = this.entity.deleteAnyWhere(json);
		
		CcpCacheDecorator cache = this.getCache(json);
		
		cache.delete();
		
		return delete;
	}
	
	public boolean exists(CcpJsonRepresentation json) {
		
		CcpCacheDecorator cache = this.getCache(json);

		boolean presentInTheCache = cache.isPresentInTheCache();
		
		if(presentInTheCache) {
			return true;
		}
		
		boolean exists = this.entity.exists(json);

		if(false == exists) {
			cache.delete();
			return false;
		}
		CcpJsonRepresentation oneById = this.getOneById(json);
		cache.put(oneById, this.cacheExpires);
		return true;
	}
	
	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		
		CcpCacheDecorator cache = this.getCache(json);
		
		CcpJsonRepresentation result = cache.get(x -> this.entity.getOneById(json), this.cacheExpires);
		
		return result;
	}

	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		CcpCacheDecorator cache = this.getCache(json);
		
		CcpJsonRepresentation result = cache.get(x -> this.entity.getRecordFromUnionAll(unionAll, json), this.cacheExpires);
		
		return result;
	}
	
	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		CcpCacheDecorator cache = this.getCache(json);
		
		boolean notPresentInThisUnionAll = false == this.entity.isPresentInThisUnionAll(unionAll, json);
		
		if(notPresentInThisUnionAll) {
			cache.delete();
			return false;
		}
		
		CcpJsonRepresentation requiredEntityRow = this.entity.getRequiredEntityRow(unionAll, json);
		cache.put(requiredEntityRow, this.cacheExpires);
		return true;
	}

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {

		CcpJsonRepresentation createOrUpdate = this.entity.save(json);
		
		CcpCacheDecorator cache = this.getCache(json);
		
		cache.put(createOrUpdate, this.cacheExpires);
		
		return createOrUpdate;
	}
}
