package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCache;

class DecoratorCacheEntity extends CcpEntityDelegator {
	
	final int cacheExpires;
	
	public DecoratorCacheEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		CcpEntityCache annotation = clazz.getAnnotation(CcpEntityCache.class);
		this.cacheExpires = annotation.value();
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
