package com.ccp.especifications.db.utils.decorators.engine;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.mensageria.receiver.CcpBusiness;

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
	
	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json,
			CcpBusiness ifNotFound) {
		
		CcpCacheDecorator cache = this.getCache(json);
	
		CcpJsonRepresentation result = cache.get(x -> this.entity.getOneById(json, ifNotFound), this.cacheExpires);
		
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
		
		boolean found = result.isEmpty() == false;
		
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

		if(exists == false) {
			cache.delete();
			return false;
		}
		CcpJsonRepresentation oneById = this.getOneById(json);
		cache.put(oneById, this.cacheExpires);
		return true;
	}

	public CcpJsonRepresentation createOrUpdate(CcpJsonRepresentation json) {

		CcpJsonRepresentation createOrUpdate = this.entity.createOrUpdate(json);
		
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

	public boolean delete(String id) {
		
		boolean delete = this.entity.delete(id);
		
		CcpCacheDecorator cache = this.getCache(id);
		
		cache.delete();
		
		return delete;
	}

	public CcpJsonRepresentation createOrUpdate(CcpJsonRepresentation json, String id) {

		CcpJsonRepresentation createOrUpdate = this.entity.createOrUpdate(json, id);
		
		CcpCacheDecorator cache = this.getCache(json);
		
		cache.put(createOrUpdate, this.cacheExpires);
		
		return createOrUpdate;
	}

	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		CcpCacheDecorator cache = this.getCache(json);
		
		boolean notPresentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json) == false;
		
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
		
		boolean notPresentInThisUnionAll = super.isPresentInThisUnionAll(unionAll, json) == false;
		
		if(notPresentInThisUnionAll) {
			cache.delete();
			return CcpOtherConstants.EMPTY_JSON;
		}
		
		CcpJsonRepresentation recordFromUnionAll = super.getRecordFromUnionAll(unionAll, json);
		cache.put(recordFromUnionAll, this.cacheExpires);
		return recordFromUnionAll;
	}
}