package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.util.function.BiFunction;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * The write operations whose side effects can be configured for an entity, each one through its own annotation (such as
 * {@code @CcpAfterSaveFromMainEntity}): {@code save}, {@code insert}, {@code update}, {@code delete} and
 * {@code deleteAnyWhere}. Each constant performs the operation on the entity and exposes the {@code before} and
 * {@code after} phases in separate methods ({@code executeBefore} and {@code executeAfter}), because each side effect
 * is applied by its own decorator at the position of its phase in the chain.
 * <p>
 * {@code insert} and {@code update} are outcomes of {@code save}: {@code insert} when {@code save} returns {@code true}
 * (the document did not exist) and {@code update} when it returns {@code false} (the document already existed). A
 * configured {@code save} covers both outcomes, so it is the one to use when the outcome does not matter.
 */
public enum CcpEntityDecoratorOperationType {
	/** Deletes the record from the entity and from its twin. */
	deleteAnyWhere{

		/**
		 * Runs {@code deleteAnyWhere} on the rest of the chain.
		 * @param json the record
		 * @param entity the rest of the chain
		 * @return whether the record existed somewhere
		 */
		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = entity.deleteAnyWhere(json);
			return result;
		}
	},
	/** Deletes the record from the entity. */
	delete{

		/**
		 * Runs {@code delete} on the rest of the chain.
		 * @param json the record
		 * @param entity the rest of the chain
		 * @return whether the record existed
		 */
		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = entity.delete(json);
			return result;
		}
	},
	/** Creates or updates the record; covers the {@code insert} and {@code update} outcomes. */
	save{
		/**
		 * Runs {@code save} on the rest of the chain.
		 * @param json the record
		 * @param entity the rest of the chain
		 * @return {@code true} when inserted, {@code false} when updated
		 */
		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = entity.save(json);
			return result;
		}

		/**
		 * {@code save} covers itself, {@code insert} and {@code update}.
		 * @param executed the operation (or its outcome) that actually happened
		 * @return {@code true} for save, insert or update
		 */
		public boolean covers(CcpEntityDecoratorOperationType executed) {
			boolean isSave = save.equals(executed);
			boolean isInsert = insert.equals(executed);
			boolean isUpdate = update.equals(executed);
			boolean covers = isSave || isInsert || isUpdate;
			return covers;
		}

		/**
		 * The {@code after} flow of a save always runs: {@code false} means update, not "nothing happened".
		 * @param result the result of the save
		 * @return always {@code false}
		 */
		public boolean isAfterFlowDispensed(boolean result) {
			return false;
		}

		/**
		 * Translates the result of the save into its outcome.
		 * @param result the result of the save
		 * @return {@code insert} for {@code true}, {@code update} for {@code false}
		 */
		public CcpEntityDecoratorOperationType getOutcome(boolean result) {
			if(result) {
				return insert;
			}
			return update;
		}
	},
	/** Outcome of a save that created the record; as an operation, it saves. */
	insert{
		/**
		 * Runs {@code save} on the rest of the chain.
		 * @param json the record
		 * @param entity the rest of the chain
		 * @return {@code true} when inserted, {@code false} when updated
		 */
		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = save.executeEntityOperation(json, entity);
			return result;
		}
	},
	/** Outcome of a save that updated an existing record; as an operation, it saves. */
	update{
		/**
		 * Runs {@code save} on the rest of the chain.
		 * @param json the record
		 * @param entity the rest of the chain
		 * @return {@code true} when inserted, {@code false} when updated
		 */
		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = save.executeEntityOperation(json, entity);
			return result;
		}
	},
;
	/**
	 * Performs the operation on the rest of the decorator chain.
	 * @param json the record
	 * @param entity the rest of the chain
	 * @return the result of the operation
	 */
	abstract boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity);

	/**
	 * Runs the side effect of the {@code before} phase and then delegates the operation to the rest of the decorator
	 * chain. The JSON produced by the side effect is the one that goes to the inner decorators, so everything after it sees
	 * the result of the previous side effects.
	 * <p>
	 * A business of the {@code before} phase guards the operation: when it throws an exception that a configured
	 * {@code @CcpExceptionFlow} handles, the handlers run and the operation is canceled, returning {@code false} without
	 * reaching the rest of the chain. Up to 2026-10-02 the operation went on after the handlers, so a {@code before}
	 * validation could not refuse a write without making it fail.
	 * @param json the input JSON
	 * @param entity the rest of the chain
	 * @param sideEffect the side effect, given the JSON and this operation
	 * @return the result of the operation, or {@code false} when it was canceled
	 */
	public boolean executeBefore(CcpJsonRepresentation json, CcpEntity entity, BiFunction<CcpJsonRepresentation, CcpEntityDecoratorOperationType, CcpJsonRepresentation> sideEffect) {
		try {
			CcpJsonRepresentation before = sideEffect.apply(json, this);
			boolean result = this.executeEntityOperation(before, entity);
			return result;
		} catch (CcpErrorEntityOperationCanceled e) {
			return false;
		}
	}

	/**
	 * Delegates the operation to the rest of the decorator chain and runs the side effect of the {@code after} phase with
	 * the outcome. In {@code delete} the side effect is skipped when no record was removed; in {@code save} it always
	 * runs, and the outcome ({@code insert} or {@code update}) decides which configured side effects fire. Since the
	 * operation returns only a boolean, the side effect receives the same JSON that reached this decorator.
	 * @param json the input JSON
	 * @param entity the rest of the chain
	 * @param sideEffect the side effect, given the JSON and the outcome of the operation
	 * @return the result of the operation
	 */
	public boolean executeAfter(CcpJsonRepresentation json, CcpEntity entity, BiFunction<CcpJsonRepresentation, CcpEntityDecoratorOperationType, CcpJsonRepresentation> sideEffect) {
		boolean result = this.executeEntityOperation(json, entity);

		boolean afterFlowIsDispensed = this.isAfterFlowDispensed(result);

		if(afterFlowIsDispensed) {
			return result;
		}

		CcpEntityDecoratorOperationType outcome = this.getOutcome(result);
		sideEffect.apply(json, outcome);
		return result;
	}

	/**
	 * Tells whether an item configured with this operation fires when the operation {@code executed} happens. By default
	 * only the operation itself; {@code save} also covers {@code insert} and {@code update}.
	 * @param executed the operation (or its outcome) that actually happened
	 * @return {@code true} when the configured item fires
	 */
	public boolean covers(CcpEntityDecoratorOperationType executed) {
		boolean covers = this.equals(executed);
		return covers;
	}

	/**
	 * Tells whether the {@code after} flow must be skipped given the result of the operation. By default it is skipped
	 * when the operation returns {@code false} (the delete found no record); {@code save} never skips it, because its
	 * {@code false} means {@code update}.
	 * @param result the result of the operation
	 * @return {@code true} when the after flow is skipped
	 */
	public boolean isAfterFlowDispensed(boolean result) {
		boolean operationDidNotHappen = false == result;
		return operationDidNotHappen;
	}

	/**
	 * Translates the result of the operation into the outcome that matches the items of the {@code after} flow. By default
	 * the outcome is the operation itself; for {@code save} it is {@code insert} ({@code true}) or {@code update}
	 * ({@code false}).
	 * @param result the result of the operation
	 * @return the outcome
	 */
	public CcpEntityDecoratorOperationType getOutcome(boolean result) {
		return this;
	}
}
