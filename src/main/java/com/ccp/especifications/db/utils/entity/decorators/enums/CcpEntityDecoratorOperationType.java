package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.util.List;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperation;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperations;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * The operations with side effects that can be configured for an entity through {@code @CcpEntityOperation}:
 * {@code save}, {@code insert}, {@code update}, {@code delete} and {@code deleteAnyWhere}. Each constant performs the
 * operation on the entity and exposes the {@code before} and {@code after} flows in separate methods
 * ({@code executeBefore} and {@code executeAfter}), because each flow is applied by its own decorator at its own
 * position of the chain.
 * <p>
 * {@code insert} and {@code update} are outcomes of {@code save}: {@code insert} when {@code save} returns {@code true}
 * (the document did not exist) and {@code update} when it returns {@code false} (the document already existed). A
 * configured {@code save} covers both outcomes, so it is the one to use when the outcome does not matter.
 */
public enum CcpEntityDecoratorOperationType implements OperationWriter{
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
	 * Runs the {@code before} flow and then delegates the operation to the rest of the decorator chain. The JSON produced
	 * by the {@code before} flow is the one that goes to the inner decorators, so everything after it sees the result of
	 * the previous side effects.
	 * <p>
	 * A business of the {@code before} flow guards the operation: when it throws an exception that a configured
	 * {@code @CcpExceptionFlow} handles, the handlers run and the operation is canceled, returning {@code false} without
	 * reaching the rest of the chain. Up to 2026-10-02 the operation went on after the handlers, so a {@code before}
	 * validation could not refuse a write without making it fail.
	 * @param json the input JSON
	 * @param clazz the class with the {@code @CcpEntityOperations} annotation
	 * @param entity the rest of the chain
	 * @return the result of the operation, or {@code false} when it was canceled
	 */
	public boolean executeBefore(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity) {
		try {
			CcpJsonRepresentation before = this.executeFlow(json, CcpEntityOperationPhase._before, clazz, entity);
			boolean result = this.executeEntityOperation(before, entity);
			return result;
		} catch (CcpErrorEntityOperationCanceled e) {
			return false;
		}
	}

	/**
	 * Delegates the operation to the rest of the decorator chain and runs the {@code after} flow of the outcome. In
	 * {@code delete} the {@code after} is skipped when no record was removed; in {@code save} it always runs, and the
	 * outcome ({@code insert} or {@code update}) decides which configured items fire. Since the operation returns only a
	 * boolean, the {@code after} flow receives the same JSON that reached this decorator.
	 * @param json the input JSON
	 * @param clazz the class with the {@code @CcpEntityOperations} annotation
	 * @param entity the rest of the chain
	 * @return the result of the operation
	 */
	public boolean executeAfter(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity) {
		boolean result = this.executeEntityOperation(json, entity);

		boolean afterFlowIsDispensed = this.isAfterFlowDispensed(result);

		if(afterFlowIsDispensed) {
			return result;
		}

		CcpEntityDecoratorOperationType outcome = this.getOutcome(result);
		outcome.executeFlow(json, CcpEntityOperationPhase._after, clazz, entity);
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

	/**
	 * Runs the businesses of the first {@code @CcpEntityOperation} whose operation covers this one, whose entity phase
	 * names the entity and whose phase is {@code when}, chaining their outputs. Only the first matching item runs: other
	 * items with the same operation, entity and phase are ignored. Local exception handlers take precedence over the
	 * global ones; in the {@code before} phase a handled exception cancels the operation.
	 * @param json the input JSON
	 * @param when the phase to run
	 * @param clazz the class with the {@code @CcpEntityOperations} annotation
	 * @param entity the entity whose name selects the items
	 * @return the JSON produced by the businesses, or the input JSON when no item matched
	 * @throws CcpErrorEntityOperationCanceled when a handled exception cancels a before flow
	 */
	protected CcpJsonRepresentation executeFlow(CcpJsonRepresentation json, CcpEntityOperationPhase when, Class<?> clazz, CcpEntity entity) {
		
		CcpEntityOperations annotation = clazz.getAnnotation(CcpEntityOperations.class);
		
		CcpExceptionFlow[] globalHandlers = annotation.globalHandlers();
		
		CcpEntityOperation[] operations = annotation.value();

		for (CcpEntityOperation operation : operations) {

			CcpEntityOperationType configuredOperationType = operation.operationType();

			CcpEntityDecoratorOperationType operationType = configuredOperationType.operationType;
			boolean operationTypeCovers = operationType.covers(this);
			boolean operationTypeDoesNotCover = false == operationTypeCovers;

			if(operationTypeDoesNotCover) {
				continue;
			}

			CcpEntityPhase entityType = configuredOperationType.entityPhase;
			String extractEntityName = entityType.extractEntityName(clazz);
			CcpEntityMetaData entityDetails = entity.getEntityMetaData();
			boolean extractEntityNameEquals = extractEntityName.equals(entityDetails.entityName);

			boolean isNotTheEntity = false == extractEntityNameEquals;
			
			if(isNotTheEntity) {
				continue;
			}
			var when2 = configuredOperationType.operationPhase;
			var when2Equals = when2.equals(when);

			boolean whenNotFound = false == when2Equals;
			
			if(whenNotFound) {
				continue;
			}
			
			CcpExceptionFlow[] localHandlers = operation.operationHandlers();
			Map<Class<?>, List<CcpBusiness>> localExceptionHandlers = this.getExceptionHandlers(localHandlers);
			Map<Class<?>, List<CcpBusiness>> globalExceptionHandlers = this.getExceptionHandlers(globalHandlers);
			globalExceptionHandlers.putAll(localExceptionHandlers);
			Class<?>[] execute = operation.execute();
			boolean isBeforeFlow = CcpEntityOperationPhase._before.equals(when);
			for (Class<?> businessClass : execute) {
				CcpBusiness business = this.getBusiness(businessClass);
				json = isBeforeFlow
						? this.executeBusinessCancelingTheOperationWhenHandled(json, business, globalExceptionHandlers)
						: this.executeBusiness(json, business, globalExceptionHandlers);
			}
			return json;
		} 
		
		return json;
	}
	
}
