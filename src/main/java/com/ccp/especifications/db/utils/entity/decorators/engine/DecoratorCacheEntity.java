package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCache;

/**
 * Decorator that adds caching to the read and write operations of an entity annotated with
 * {@code @CcpEntityCache}. On reads it returns the cached result when available; on
 * writes it updates or invalidates the cache according to the operation performed.
 */
class DecoratorCacheEntity extends CcpEntityDelegator {
	
	final int cacheExpires;
	
	public DecoratorCacheEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		CcpEntityCache annotation = clazz.getAnnotation(CcpEntityCache.class);
		this.cacheExpires = annotation.value();
	}
	
	private CcpCacheDecorator getCache(String recordId) {
		CcpCacheDecorator cache = new CcpCacheDecorator(this, recordId);
		return cache;
	}

	public boolean delete(CcpJsonRepresentation json) {

		boolean deleted = this.entity.delete(json);
		String recordId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(recordId);

		cache.delete();

		return deleted;
	}

	public boolean deleteAnyWhere(CcpJsonRepresentation json) {

		boolean deleted = this.entity.deleteAnyWhere(json);

		String recordId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(recordId);
		
		cache.delete();
		
		return deleted;
	}
	
	public boolean exists(CcpJsonRepresentation json) {
		
		String recordId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(recordId);

		boolean presentInTheCache = cache.isPresentInTheCache();
		
		if(presentInTheCache) {
			return true;
		}
		
		boolean exists = this.entity.exists(json);
		boolean doesNotExist = false == exists;

		if(doesNotExist) {
			cache.delete();
			return false;
		}
		CcpJsonRepresentation oneById = this.getOneById(json);
		cache.put(oneById, this.cacheExpires);
		return true;
	}
	
	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		
		String recordId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(recordId);
		
		CcpJsonRepresentation result = cache.get(x -> this.entity.getOneById(json), this.cacheExpires);
		
		return result;
	}

	/**
	 * Returns the record from the union-all result <b>without going through the cache</b>.
	 *
	 * <p>{@code CcpSelectUnionAll} is an in-memory structure: the {@code _mget} has already brought every
	 * record before this call. Checking the cache here cannot save any round trip to the database —
	 * at best it avoids a RAM read, and at worst it pays for a read and a write on the
	 * cache server to get what was already at hand.</p>
	 *
	 * <p>The cache is still used in {@code getOneById} and {@code exists}, where the alternative is
	 * actually going to the database.</p>
	 */
	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {

		CcpJsonRepresentation result = this.entity.getRecordFromUnionAll(unionAll, json.getJsonSupplier());

		return result;
	}

	/**
	 * Tells whether the record is in the union-all result, <b>without touching the cache</b>.
	 *
	 * <p>Besides the lookup being in memory (see {@code getRecordFromUnionAll}), the previous version
	 * undid its own work: {@code CcpCrud.unionAll} deletes the key right before the {@code _mget},
	 * and this method wrote it back right after with the freshly read data. That was three conversations with the cache
	 * server — delete, read, write — to end up in the same state as doing nothing.</p>
	 */
	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {

		boolean presentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json);

		return presentInThisUnionAll;
	}

	public boolean save(CcpJsonRepresentation json) {

		boolean inserted = this.entity.save(json);

		String recordId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(recordId);

		cache.put(json, this.cacheExpires);

		return inserted;
	}

	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {


		String recordId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(recordId);
		cache.delete();

		boolean transferred = this.entity.transferDataTo(json, entities);
		return transferred;
	}
}
