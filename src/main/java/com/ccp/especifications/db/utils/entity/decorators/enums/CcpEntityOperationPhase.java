package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * When the side effects of an entity operation run, relative to the operation itself. Each phase knows its position in
 * the decorator chain and how it wraps the operation: {@code _before} runs the side effect and hands its JSON to the
 * operation, {@code _after} runs the operation and then the side effect of its outcome.
 */
public enum CcpEntityOperationPhase {
	/** After the operation. */
	_after{
		/**
		 * Priority 5: inside the field transformers, so the side effect sees the record as written.
		 * @return 5
		 */
		public int getPriority() {
			return 5;
		}

		/**
		 * Runs the write and then the side effect of its outcome.
		 * @param operation the write that happens
		 * @param json the record
		 * @param entity the rest of the chain
		 * @param sideEffect the side effect, given the JSON and the outcome of the write
		 * @return the result of the write
		 */
		public boolean executeWrite(CcpEntityDecoratorOperationType operation, CcpJsonRepresentation json, CcpEntity entity, BiFunction<CcpJsonRepresentation, CcpEntityDecoratorOperationType, CcpJsonRepresentation> sideEffect) {
			boolean result = operation.executeAfter(json, entity, sideEffect);
			return result;
		}

		/**
		 * Runs the transfer and then, when it happened, the side effect.
		 * @param transfer the transfer that happens
		 * @param json the record
		 * @param entity the rest of the chain
		 * @param targetEntity the target entity
		 * @param sideEffect the side effect, given the JSON and the target entity
		 * @return the result of the transfer
		 */
		public boolean executeTransfer(CcpEntityDecoratorTransferType transfer, CcpJsonRepresentation json, CcpEntity entity, CcpEntity targetEntity, BiFunction<CcpJsonRepresentation, CcpEntity, CcpJsonRepresentation> sideEffect) {
			boolean result = transfer.executeAfter(json, entity, targetEntity, sideEffect);
			return result;
		}

		/**
		 * After the operation a handled exception only runs its handlers: the operation already happened.
		 * @param writer the runner of businesses
		 * @param json the input JSON
		 * @param business the business
		 * @param exceptionHandlers handlers by exception class
		 * @return the output of the business or of the last handler
		 */
		CcpJsonRepresentation executeBusiness(OperationWriter writer, CcpJsonRepresentation json, CcpBusiness business, Map<Class<?>, List<CcpBusiness>> exceptionHandlers) {
			CcpJsonRepresentation result = writer.executeBusiness(json, business, exceptionHandlers);
			return result;
		}
	},
	/** Before the operation. */
	_before{
		/**
		 * Priority 7: outside the field transformers and every other decorator but the asynchronous writer, the read-only
		 * guard and the validator, so the JSON it produces is the one that goes inward.
		 * @return 7
		 */
		public int getPriority() {
			return 7;
		}

		/**
		 * Runs the side effect and then the write with the JSON it produced; a handled exception cancels the write.
		 * @param operation the write that happens
		 * @param json the record
		 * @param entity the rest of the chain
		 * @param sideEffect the side effect, given the JSON and the write
		 * @return the result of the write, {@code false} when it was canceled
		 */
		public boolean executeWrite(CcpEntityDecoratorOperationType operation, CcpJsonRepresentation json, CcpEntity entity, BiFunction<CcpJsonRepresentation, CcpEntityDecoratorOperationType, CcpJsonRepresentation> sideEffect) {
			boolean result = operation.executeBefore(json, entity, sideEffect);
			return result;
		}

		/**
		 * Runs the side effect and then the transfer with the JSON it produced; a handled exception cancels the transfer.
		 * @param transfer the transfer that happens
		 * @param json the record
		 * @param entity the rest of the chain
		 * @param targetEntity the target entity
		 * @param sideEffect the side effect, given the JSON and the target entity
		 * @return the result of the transfer, {@code false} when it was canceled
		 */
		public boolean executeTransfer(CcpEntityDecoratorTransferType transfer, CcpJsonRepresentation json, CcpEntity entity, CcpEntity targetEntity, BiFunction<CcpJsonRepresentation, CcpEntity, CcpJsonRepresentation> sideEffect) {
			boolean result = transfer.executeBefore(json, entity, targetEntity, sideEffect);
			return result;
		}

		/**
		 * Before the operation a business guards it: a handled exception runs its handlers and cancels the operation.
		 * @param writer the runner of businesses
		 * @param json the input JSON
		 * @param business the business
		 * @param exceptionHandlers handlers by exception class
		 * @return the output of the business
		 * @throws CcpErrorEntityOperationCanceled when a handled exception cancels the operation
		 */
		CcpJsonRepresentation executeBusiness(OperationWriter writer, CcpJsonRepresentation json, CcpBusiness business, Map<Class<?>, List<CcpBusiness>> exceptionHandlers) {
			CcpJsonRepresentation result = writer.executeBusinessCancelingTheOperationWhenHandled(json, business, exceptionHandlers);
			return result;
		}
	}
	;

	/**
	 * The position in the decorator chain of the side effects of this phase: higher priorities wrap the lower ones.
	 * @return the priority
	 */
	public abstract int getPriority();

	/**
	 * Runs a write ({@code save}, {@code delete} or {@code deleteAnyWhere}) with the side effect in this phase.
	 * @param operation the write that happens
	 * @param json the record
	 * @param entity the rest of the chain
	 * @param sideEffect the side effect, given the JSON and the write (before) or its outcome (after)
	 * @return the result of the write
	 */
	public abstract boolean executeWrite(CcpEntityDecoratorOperationType operation, CcpJsonRepresentation json, CcpEntity entity, BiFunction<CcpJsonRepresentation, CcpEntityDecoratorOperationType, CcpJsonRepresentation> sideEffect);

	/**
	 * Runs a transfer ({@code transferDataTo} or {@code copyDataTo}) with the side effect in this phase.
	 * @param transfer the transfer that happens
	 * @param json the record
	 * @param entity the rest of the chain
	 * @param targetEntity the target entity
	 * @param sideEffect the side effect, given the JSON and the target entity
	 * @return the result of the transfer
	 */
	public abstract boolean executeTransfer(CcpEntityDecoratorTransferType transfer, CcpJsonRepresentation json, CcpEntity entity, CcpEntity targetEntity, BiFunction<CcpJsonRepresentation, CcpEntity, CcpJsonRepresentation> sideEffect);

	/**
	 * Runs one business of a side effect with the exception handling of this phase.
	 * @param writer the runner of businesses
	 * @param json the input JSON
	 * @param business the business
	 * @param exceptionHandlers handlers by exception class
	 * @return the output of the business
	 */
	abstract CcpJsonRepresentation executeBusiness(OperationWriter writer, CcpJsonRepresentation json, CcpBusiness business, Map<Class<?>, List<CcpBusiness>> exceptionHandlers);
}
