package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Decorator que impede qualquer operação de escrita em entidades marcadas com
 * {@code @CcpEntityOlyReadable}. Os métodos {@code save}, {@code delete}, {@code deleteAnyWhere}
 * e {@code transferDataTo} não executam nada e retornam {@code false}.
 */
class DecoratorReadOnlyEntity extends CcpEntityDelegator {

	public DecoratorReadOnlyEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
	}

	public boolean delete(CcpJsonRepresentation json) {
		return false;
	}

	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		return false;
	}


	public boolean save(CcpJsonRepresentation json) {
		return false;
	}

	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity... entities) {
		return false;
	}
}
