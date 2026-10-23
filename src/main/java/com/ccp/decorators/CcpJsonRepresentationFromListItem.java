package com.ccp.decorators;

import java.util.Map;
import java.util.function.Function;

/**
 * Turns an item of a list field into a {@code CcpJsonRepresentation}: an item that already is one is kept, a map is
 * wrapped.
 */
public class CcpJsonRepresentationFromListItem implements Function<Object, CcpJsonRepresentation> {

	/** The only instance: the conversion keeps no state. */
	public final static CcpJsonRepresentationFromListItem INSTANCE = new CcpJsonRepresentationFromListItem();

	private CcpJsonRepresentationFromListItem() {
	}

	/**
	 * Converts the item.
	 * @param item a {@code CcpJsonRepresentation} or a map
	 * @return the item as a {@code CcpJsonRepresentation}
	 */
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(Object item) {

		if(item instanceof CcpJsonRepresentation jsonItem) {
			return jsonItem;
		}

		CcpJsonRepresentation json = new CcpJsonRepresentation((Map<String, Object>) item);
		return json;
	}
}
