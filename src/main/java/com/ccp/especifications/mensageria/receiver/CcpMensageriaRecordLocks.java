package com.ccp.especifications.mensageria.receiver;

import java.util.concurrent.locks.ReentrantLock;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Keeps the asynchronous writes of the same record from running at the same time in the consumers of a process. The
 * decorators that read the stored record before a write and correct a counter or a group after it (the grouper
 * counters, for instance) count a record twice when two writes of it read the record at the same moment: both see it
 * absent, both count it as an insertion. Since 2026-11-16, after a resume saved twice in 70 ms was counted twice in
 * production, each message of an entity write runs holding the lock of its record.
 * <p>
 * The locks are striped: the key of the record chooses one of a fixed number of locks, so the memory does not grow with
 * the records. Two different records may share a lock and then wait for each other, which costs only time. Only the
 * messages taken from the queue lock ({@link CcpMensageriaReceiver#executeProcess}); the writes they cause in the same
 * thread (the synchronous messaging) do not, so a thread holds at most one of these locks and they cannot deadlock. The
 * locks live in the memory of the process: they serialize the consumers of one process, the only one that reads each
 * queue.
 */
public class CcpMensageriaRecordLocks {

	/** How many locks the records share. */
	private static final int STRIPES = 1024;

	/** The single instance. */
	public static final CcpMensageriaRecordLocks INSTANCE = new CcpMensageriaRecordLocks();

	/** The locks, chosen by the hash of the key of the record. */
	private final ReentrantLock[] locks = new ReentrantLock[STRIPES];

	private CcpMensageriaRecordLocks() {
		for (int index = 0; index < STRIPES; index++) {
			this.locks[index] = new ReentrantLock();
		}
	}

	/**
	 * Runs the process holding the lock of the record, after any other write of the same record still running ends.
	 * @param recordKey the record: the class of the entity and the id of the record
	 * @param process the write of the record
	 * @param json the message
	 * @return the response of the process
	 */
	public CcpJsonRepresentation executeLocked(String recordKey, CcpBusiness process, CcpJsonRepresentation json) {
		int hashCode = recordKey.hashCode();
		int index = Math.floorMod(hashCode, STRIPES);
		ReentrantLock lock = this.locks[index];
		lock.lock();
		try {
			CcpJsonRepresentation response = process.execute(json);
			return response;
		} finally {
			lock.unlock();
		}
	}
}
