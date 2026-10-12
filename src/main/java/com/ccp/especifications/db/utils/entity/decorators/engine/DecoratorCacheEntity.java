package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.function.Supplier;

import com.ccp.constants.CcpOtherConstants;
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
	
	/** How long, in seconds, a record stays cached. */
	final int cacheExpires;
	
	/**
	 * Wraps the entity, reading the expiration from {@code @CcpEntityCache}.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class
	 */
	public DecoratorCacheEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		CcpEntityCache annotation = clazz.getAnnotation(CcpEntityCache.class);
		this.cacheExpires = annotation.value();
	}
	
	/**
	 * Returns the cache entry of the record ({@code records.entity.<name>.id.<id>}).
	 * @param recordId the record id
	 * @return the cache entry
	 */
	private CcpCacheDecorator getCache(String recordId) {
		CcpCacheDecorator cache = new CcpCacheDecorator(this, recordId);
		return cache;
	}

	/**
	 * Deletes the record and then its cache entry.
	 * @param json the record
	 * @return the outcome of the wrapped delete
	 */
	public boolean delete(CcpJsonRepresentation json) {

		boolean deleted = this.entity.delete(json);
		String recordId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(recordId);

		cache.delete();

		return deleted;
	}

	/**
	 * Deletes the record everywhere and then its cache entry.
	 * @param json the record
	 * @return the outcome of the wrapped deleteAnyWhere
	 */
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {

		boolean deleted = this.entity.deleteAnyWhere(json);

		String recordId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(recordId);
		
		cache.delete();
		
		return deleted;
	}
	
	/**
	 * Tells whether the record exists: {@code true} when it is cached; otherwise asks the wrapped entity, caching the
	 * record when it exists and clearing the entry when it does not.
	 * @param json the record (at least its primary key)
	 * @return {@code true} when the record exists
	 */
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
	
	/**
	 * Reads the record through the cache: a cached record is returned; otherwise it is read from the wrapped entity and
	 * cached.
	 * @param json the record (at least its primary key)
	 * @return the record
	 */
	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		
		String recordId = this.entity.calculateId(json);		
		CcpCacheDecorator cache = this.getCache(recordId);
		
		CcpJsonRepresentation result = cache.get(x -> this.entity.getOneById(json), this.cacheExpires);
		
		return result;
	}

	/**
	 * Returns the record from the union-all result, <b>reading the cache first</b>: a cached record is returned as it
	 * is; otherwise the record comes from the union-all result.
	 * <p>
	 * The id is calculated over the primary key after the field transformers, as the wrapped entity does, so the key
	 * of the cache is the same one used by {@code getOneById}, {@code exists}, {@code save} and {@code delete}.
	 * <p>
	 * What comes from the union all is <b>not</b> cached. These two methods also run in the middle of writes (the twin
	 * entity asks whether the record existed while transferring it on a delete), and caching there put back a record
	 * the same operation was deleting (tried on 2026-10-11: 12 entity decorator tests broke). The cache keeps being
	 * filled where it always was: {@code getOneById}, {@code exists} and {@code save}.
	 * @param unionAll the search result
	 * @param jsonSupplier supplies the search parameters
	 * @return the record, or an empty JSON
	 */
	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, Supplier<CcpJsonRepresentation> jsonSupplier) {
		CcpJsonRepresentation json = jsonSupplier.get();
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		CcpJsonRepresentation handledJson = entityDetails.entity.getJsonWithHandledPrimaryKey(json);
		String recordId = this.entity.calculateId(handledJson);
		CcpCacheDecorator cache = this.getCache(recordId);

		CcpJsonRepresentation cachedRecord = cache.getOrDefault(CcpOtherConstants.EMPTY_JSON);
		boolean foundInTheCache = false == cachedRecord.isEmpty();

		if(foundInTheCache) {
			return cachedRecord;
		}

		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, jsonSupplier);
		return recordFromUnionAll;
	}

	/**
	 * Tells whether the record exists, <b>reading the cache first</b>: {@code true} when it is cached; otherwise asks
	 * the union-all result. Nothing is cached here, for the reason given in {@code getRecordFromUnionAll}.
	 * <p>
	 * The json arrives here with its primary key already handled by the field transformers (they wrap this decorator).
	 * @param unionAll the search result
	 * @param json the search parameters
	 * @return {@code true} when the record exists
	 */
	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		String recordId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(recordId);

		boolean presentInTheCache = cache.isPresentInTheCache();

		if(presentInTheCache) {
			return true;
		}

		boolean presentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json);
		return presentInThisUnionAll;
	}

	/**
	 * Caches only the fields the entity stores, the same record a {@code getOneById} brings from the
	 * database. Up to 2026-09-30 the whole json went to the cache: a save arriving through the messaging
	 * cached the message envelope ({@code operation}, {@code messageId}...) and the request's session fields
	 * along with the record, and whoever read it back got a record the database does not have.
	 */
	public boolean save(CcpJsonRepresentation json) {

		boolean inserted = this.entity.save(json);

		String recordId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(recordId);

		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		CcpJsonRepresentation storedRecord = entityDetails.getOnlyExistingFields(json);
		cache.put(storedRecord, this.cacheExpires);

		return inserted;
	}

	/**
	 * Clears the cache entry of the record and then transfers it.
	 * @param json the record
	 * @param entities the target entity
	 * @return the outcome of the wrapped transfer
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {


		String recordId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(recordId);
		cache.delete();

		boolean transferred = this.entity.transferDataTo(json, entities);
		return transferred;
	}
}
