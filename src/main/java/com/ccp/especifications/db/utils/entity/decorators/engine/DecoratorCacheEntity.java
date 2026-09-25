package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCache;

/**
 * Decorator que adiciona cache às operações de leitura e escrita de uma entidade anotada com
 * {@code @CcpEntityCache}. Nas leituras retorna o resultado do cache quando disponível; nas
 * escritas atualiza ou invalida o cache conforme a operação realizada.
 */
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

	public boolean delete(CcpJsonRepresentation json) {

		boolean delete = this.entity.delete(json);
		String calculateId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(calculateId);

		cache.delete();

		return delete;
	}

	public boolean deleteAnyWhere(CcpJsonRepresentation json) {

		boolean delete = this.entity.deleteAnyWhere(json);

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
		boolean valorIgual = false == exists;

		if(valorIgual) {
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

	/**
	 * Devolve o registro do resultado de union-all <b>sem passar pelo cache</b>.
	 *
	 * <p>O {@code CcpSelectUnionAll} é uma estrutura em memória: o {@code _mget} já trouxe todos os
	 * registros antes desta chamada. Consultar o cache aqui não pode economizar ida nenhuma ao banco —
	 * na melhor hipótese evita uma leitura de RAM, e na pior paga uma leitura e uma gravação no
	 * servidor de cache para obter o que já estava na mão.</p>
	 *
	 * <p>O cache continua valendo em {@code getOneById} e {@code exists}, onde a alternativa é
	 * realmente ir ao banco.</p>
	 */
	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {

		CcpJsonRepresentation result = this.entity.getRecordFromUnionAll(unionAll, json.getJsonSupplier());

		return result;
	}

	/**
	 * Informa se o registro está no resultado de union-all, <b>sem tocar no cache</b>.
	 *
	 * <p>Além de a consulta ser em memória (ver {@code getRecordFromUnionAll}), a versão anterior
	 * desfazia o próprio trabalho: {@code CcpCrud.unionAll} apaga a chave logo antes do {@code _mget},
	 * e este método a regravava em seguida com o dado recém-lido. Eram três conversas com o servidor
	 * de cache — apagar, ler, gravar — para terminar no mesmo estado de quem não faz nada.</p>
	 */
	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {

		boolean presentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json);

		return presentInThisUnionAll;
	}

	public boolean save(CcpJsonRepresentation json) {

		boolean inserted = this.entity.save(json);

		String calculateId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(calculateId);

		cache.put(json, this.cacheExpires);

		return inserted;
	}

	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {


		String calculateId = this.entity.calculateId(json);
		CcpCacheDecorator cache = this.getCache(calculateId);
		cache.delete();

		boolean transferDataTo = this.entity.transferDataTo(json, entities);
		return transferDataTo;
	}
}
