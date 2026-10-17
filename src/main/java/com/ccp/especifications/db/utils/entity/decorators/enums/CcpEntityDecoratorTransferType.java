package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.util.function.BiFunction;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * The kinds of data transfer between entities whose side effects can be configured, each one through its own annotation
 * (such as {@code @CcpAfterTransferDataFromMainEntity}): {@code transferDataTo} (moves and removes the source) and
 * {@code copyDataTo} (copies without removing the source). Exposes the {@code before} and {@code after} phases in
 * separate methods ({@code executeBefore} and {@code executeAfter}), because each side effect is applied by its own
 * decorator at the position of its phase in the chain.
 */
public enum CcpEntityDecoratorTransferType {
	/** Moves the record to the target entity, removing it from the source. */
	transferDataTo{
		/**
		 * Runs {@code transferDataTo} on the rest of the chain.
		 * @param json the record
		 * @param entity the rest of the chain
		 * @param entities the target entity
		 * @return whether the record existed in the source
		 */
		boolean executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity entities) {
			boolean result = entity.transferDataTo(json, entities);
			return result;
		}
	},
	/** Copies the record to the target entity, keeping it in the source. */
	copyDataTo{
		/**
		 * Runs {@code copyDataTo} on the rest of the chain.
		 * @param json the record
		 * @param entity the rest of the chain
		 * @param entities the target entity
		 * @return whether the record existed in the source
		 */
		boolean executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity entities) {
			boolean result = entity.copyDataTo(json, entities);
			return result;
		}
	},
;
	/**
	 * Performs the transfer on the rest of the decorator chain.
	 * @param json the record
	 * @param entity the rest of the chain
	 * @param entityToTransfer the target entity
	 * @return whether the record existed in the source
	 */
	abstract boolean executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, CcpEntity entityToTransfer);

	/**
	 * Runs the side effect of the {@code before} phase and then delegates the transfer to the rest of the decorator chain.
	 * The JSON produced by the side effect is the one that goes to the inner decorators. As in the write operations, an
	 * exception handled in the {@code before} phase cancels the transfer, which returns {@code false}; until 2026-10-06
	 * the transfer went on anyway (decided with the user).
	 * @param json the input JSON
	 * @param entity the source entity
	 * @param entityToTransfer the target entity
	 * @param sideEffect the side effect, given the JSON and the target entity
	 * @return the result of the transfer, {@code false} when it was canceled
	 */
	public boolean executeBefore(CcpJsonRepresentation json, CcpEntity entity, CcpEntity entityToTransfer, BiFunction<CcpJsonRepresentation, CcpEntity, CcpJsonRepresentation> sideEffect) {
		try {
			CcpJsonRepresentation before = sideEffect.apply(json, entityToTransfer);
			boolean result = this.executeEntityTransfer(before, entity, entityToTransfer);
			return result;
		} catch (CcpErrorEntityOperationCanceled e) {
			return false;
		}
	}

	/**
	 * Delegates the transfer to the rest of the decorator chain and runs the side effect of the {@code after} phase only
	 * when the transfer actually happened (there was a source record). Since the transfer returns only a boolean, the side
	 * effect receives the same JSON that reached this decorator.
	 * @param json the input JSON
	 * @param entity the source entity
	 * @param entityToTransfer the target entity
	 * @param sideEffect the side effect, given the JSON and the target entity
	 * @return the result of the transfer
	 */
	public boolean executeAfter(CcpJsonRepresentation json, CcpEntity entity, CcpEntity entityToTransfer, BiFunction<CcpJsonRepresentation, CcpEntity, CcpJsonRepresentation> sideEffect) {
		boolean result = this.executeEntityTransfer(json, entity, entityToTransfer);

		boolean transferDidNotHappen = false == result;

		if(transferDidNotHappen) {
			return false;
		}

		sideEffect.apply(json, entityToTransfer);
		return result;
	}
}
