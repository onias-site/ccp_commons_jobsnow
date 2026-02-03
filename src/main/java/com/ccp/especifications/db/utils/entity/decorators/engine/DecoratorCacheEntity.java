package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;

class DecoratorCacheEntity extends CcpEntityDelegator{

	private final int cacheExpires;

	public DecoratorCacheEntity(CcpEntity entity, int cacheExpires) {
		super(entity);
		this.cacheExpires = cacheExpires;
	}

	private CcpCacheDecorator getCache(CcpJsonRepresentation json) {
		CcpCacheDecorator ccpCacheDecorator = new CcpCacheDecorator(this, json);
		return ccpCacheDecorator;
	}

	private CcpCacheDecorator getCache(String id) {
		CcpCacheDecorator ccpCacheDecorator = new CcpCacheDecorator(this, id);
		return ccpCacheDecorator;
	}
	
	public CcpJsonRepresentation getOneByIdOrHandleItIfThisIdWasNotFound(CcpJsonRepresentation json, CcpBusiness ifNotFound) {
		
		CcpCacheDecorator cache = this.getCache(json);
	
		CcpJsonRepresentation result = cache.get(x -> this.entity.getOneByIdOrHandleItIfThisIdWasNotFound(json, ifNotFound), this.cacheExpires);
		
		return result;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		
		CcpCacheDecorator cache = this.getCache(json);
		
		CcpJsonRepresentation result = cache.get(x -> this.entity.getOneById(json), this.cacheExpires);
		
		return result;
	}

	public CcpJsonRepresentation getOneById(String id) {

		CcpCacheDecorator cache = this.getCache(id);
		
		CcpJsonRepresentation result = cache.get(x -> this.entity.getOneById(id), this.cacheExpires);
		
		return result;
	}

	public boolean exists(String id) {

		CcpCacheDecorator cache = this.getCache(id);

		boolean exists = cache.isPresentInTheCache();
		
		if(exists) {
			return true;
		}

		CcpJsonRepresentation result = cache.get(x -> this.entity.getOneById(id), this.cacheExpires);
		
		boolean found = false == result.isEmpty();
		
		if(found) {
			return true;
		}
		
		cache.delete();
		
		return false;
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

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {

		CcpJsonRepresentation createOrUpdate = this.entity.save(json);
		
		CcpCacheDecorator cache = this.getCache(json);
		
		cache.put(createOrUpdate, this.cacheExpires);
		
		return createOrUpdate;
	}

	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation delete = this.entity.delete(json);
		
		CcpCacheDecorator cache = this.getCache(json);
		
		cache.delete();
		
		return delete;
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
	
	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		CcpCacheDecorator cache = this.getCache(json);
		
		boolean notPresentInThisUnionAll = false == super.isPresentInThisUnionAll(unionAll, json);
		
		if(notPresentInThisUnionAll) {
			cache.delete();
			return CcpOtherConstants.EMPTY_JSON;
		}
		
		CcpJsonRepresentation recordFromUnionAll = super.getRecordFromUnionAll(unionAll, json);
		cache.put(recordFromUnionAll, this.cacheExpires);
		return recordFromUnionAll;
	}
}