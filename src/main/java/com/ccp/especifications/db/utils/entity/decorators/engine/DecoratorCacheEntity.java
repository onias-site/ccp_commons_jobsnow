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
	
	private CcpCacheDecorator getCache(String calculateId) {
		CcpCacheDecorator ccpCacheDecorator = new CcpCacheDecorator(this, calculateId);
		return ccpCacheDecorator;
	}

	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation delete = this.entity.delete(json);
		String calculateId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(calculateId);
		
		cache.delete();
		
		return delete;
	}

	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation delete = this.entity.deleteAnyWhere(json);

		String calculateId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(calculateId);
		
		cache.delete();
		
		return delete;
	}
	
	public boolean exists(CcpJsonRepresentation json) {
		
		String calculateId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(calculateId);

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
		
		String calculateId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(calculateId);
		
		CcpJsonRepresentation result = cache.get(x -> this.entity.getOneById(json), this.cacheExpires);
		
		return result;
	}

	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		String calculateId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(calculateId);
		
		CcpJsonRepresentation result = cache.get(x -> this.entity.getRecordFromUnionAll(unionAll, json), this.cacheExpires);
		
		return result;
	}
	
	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		String calculateId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(calculateId);
		
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
		
		String calculateId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(calculateId);
		
		cache.put(createOrUpdate, this.cacheExpires);
		
		return createOrUpdate;
	}
	
	public CcpJsonRepresentation transferDataTo(CcpJsonRepresentation json, CcpEntity... entities) {

		
		String calculateId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(calculateId);
		cache.delete();
		
		CcpJsonRepresentation transferDataTo = this.entity.transferDataTo(json, entities);
		return transferDataTo;
	}
}
