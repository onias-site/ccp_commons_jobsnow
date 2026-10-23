package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.function.Function;

import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;

/**
 * Derives the entity name from the simple name of the configurator class, keeping the cost center prefix and
 * discarding only the {@code Entity} marker. Example: {@code XyEntityOrderItem} becomes {@code xy_order_item}. The
 * default of {@code CcpEntityFactory.mainEntityNameProducer}.
 */
public class CcpEntityMainNameProducer implements Function<Class<?>, String> {

	/** The only instance: the producer keeps no state. */
	public final static CcpEntityMainNameProducer INSTANCE = new CcpEntityMainNameProducer();

	private CcpEntityMainNameProducer() {
	}

	/**
	 * Derives the entity name.
	 * @param clazz the configurator class
	 * @return the entity name
	 */
	public String apply(Class<?> clazz) {
		String simpleName = clazz.getSimpleName();
		CcpStringDecorator simpleNameDecorator = new CcpStringDecorator(simpleName);
		CcpTextDecorator simpleNameText = simpleNameDecorator.text();
		var snakeCaseDecorator = simpleNameText.toSnakeCase();
		String snakeCase = snakeCaseDecorator.content;
		int entityMarkerIndex = snakeCase.indexOf("_entity_");
		String costCenterPrefix = snakeCase.substring(0, entityMarkerIndex);
		int entityNameStartIndex = entityMarkerIndex + 8;
		String nameAfterEntityMarker = snakeCase.substring(entityNameStartIndex);
		String entityName = costCenterPrefix + "_" + nameAfterEntityMarker;
		return entityName;
	}
}
