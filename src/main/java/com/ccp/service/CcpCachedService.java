package com.ccp.service;

import java.util.Map;

import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.hash.CcpHashAlgorithm;

public class CcpCachedService{
	
	private final CcpJsonFieldName fieldToCache;
	private final CcpService service;
	private final int cacheSeconds;

	public CcpCachedService(CcpJsonFieldName fieldToCache, CcpService service, int cacheSeconds) {
		this.fieldToCache = fieldToCache;
		this.cacheSeconds = cacheSeconds;
		this.service = service;
	}

	public Map<String, Object> execute(Map<String, Object> map) {
		CcpJsonRepresentation json = new CcpJsonRepresentation(map);
		CcpStringDecorator cacheKey = json.getAsStringDecorator(this.fieldToCache);
		CcpHashDecorator hash = cacheKey.hash();
		String hashValue = hash.asString(CcpHashAlgorithm.SHA1);
		CcpCacheDecorator ccd = new CcpCacheDecorator(hashValue);
		CcpJsonRepresentation value = ccd.get(this.service, json, this.cacheSeconds);
		CcpJsonRepresentation put = value.put(() -> "cacheHash", hashValue);
		return put.content;
	}
}
