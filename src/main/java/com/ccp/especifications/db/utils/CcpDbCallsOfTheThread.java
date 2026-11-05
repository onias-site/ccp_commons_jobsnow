package com.ccp.especifications.db.utils;

import java.util.List;
import java.util.UUID;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;

/**
 * The calls to the database (and, since 2026-11-05, the commands to the cache, counted as hits, misses and writes) made
 * by the current thread while one unit of work runs: an online request (started by the
 * filter of the REST APIs) or a queue message (started by the Pub/Sub receiver). The unit gets a trace id, sent to the
 * database with each call so the database's own logs (the Elasticsearch slowlog) can be joined to it, and each call is
 * recorded with its latency as seen by the client. At the end, {@link #finish()} gives the summary that the unit writes
 * in its log line, read by the local panels of the requests and of the queues.
 * <p>Outside a unit (scheduled process, bot listener, tests) nothing is recorded and no trace is sent. Calls made by
 * other threads of the same unit are not counted.</p>
 */
public final class CcpDbCallsOfTheThread {
	/** Fields of the summary and of each call. */
	public enum Fields implements CcpJsonFieldName {
		/** The trace id of the unit: 32 hexadecimal characters, the same format as the trace of Cloud Run. */
		traceId,
		/** The calls, in the order they were made. */
		dbCalls,
		/** How many calls were made. */
		dbCallsCount,
		/** The sum of the latencies of the calls as seen by the client, in milliseconds. */
		dbClientMs,
		/** The sum of the times the database reported in its responses (when it reports one), in milliseconds. */
		dbTookMs,
		/** The latency of one call as seen by the client, in milliseconds. */
		clientMs,
		/** The time the database reported in the response of one call, in milliseconds. */
		tookMs,
		/** The position of the call in the unit, starting at 1. */
		sequence,
		/** Reads of the cache that found the key. */
		cacheHits,
		/** Reads of the cache that did not find the key. */
		cacheMisses,
		/** Writes and deletes sent to the cache. */
		cacheWrites,
		/** How many commands were sent to the cache (reads, writes and deletes). */
		cacheCallsCount,
		/** The sum of the latencies of the commands to the cache as seen by the client, in milliseconds. */
		cacheClientMs,
		/** The reads of the cache per entity of the key (empty name: keys that are not of a record). */
		cacheByEntity,
		/** Reads of one entity that found the key. */
		hits,
		/** Reads of one entity that did not find the key. */
		misses
	}

	/** The texts of this class. */
	enum Constants implements CcpJsonFieldName {
		/** The separator of the groups of a UUID. */
		HYPHEN { public String getValue() { return "-"; } },
		/** Nothing. */
		EMPTY { public String getValue() { return ""; } }
	}

	/** The unit of the current thread: empty when no unit runs. */
	private static final ThreadLocal<CcpJsonRepresentation> UNIT_OF_THE_THREAD = ThreadLocal.withInitial(() -> CcpOtherConstants.EMPTY_JSON);

	private CcpDbCallsOfTheThread() {}

	/**
	 * A new trace id: a random UUID without the hyphens.
	 * @return 32 hexadecimal characters
	 */
	public static String newTraceId() {
		UUID uuid = UUID.randomUUID();
		String uuidText = uuid.toString();
		String hyphen = Constants.HYPHEN.getValue();
		String empty = Constants.EMPTY.getValue();
		String traceId = uuidText.replace(hyphen, empty);
		return traceId;
	}

	/**
	 * Starts a unit in the current thread, discarding any unit left unfinished.
	 * @param traceId the trace id of the unit
	 */
	public static void start(String traceId) {
		CcpJsonRepresentation unit = CcpOtherConstants.EMPTY_JSON.put(Fields.traceId, traceId);
		UNIT_OF_THE_THREAD.set(unit);
	}

	/**
	 * Whether a unit runs in the current thread.
	 * @return true when the calls are being recorded
	 */
	public static boolean isMeasuring() {
		CcpJsonRepresentation unit = UNIT_OF_THE_THREAD.get();
		boolean measuring = unit.containsAllFields(Fields.traceId);
		return measuring;
	}

	/**
	 * The trace id of the unit of the current thread.
	 * @return the trace id, or an empty text when no unit runs
	 */
	public static String getTraceId() {
		CcpJsonRepresentation unit = UNIT_OF_THE_THREAD.get();
		String traceId = unit.getAsString(Fields.traceId);
		return traceId;
	}

	/**
	 * The position the next call will take in the unit.
	 * @return 1 for the first call
	 */
	public static int getNextSequence() {
		CcpJsonRepresentation unit = UNIT_OF_THE_THREAD.get();
		List<CcpJsonRepresentation> calls = unit.getAsJsonList(Fields.dbCalls);
		int callsCount = calls.size();
		int nextSequence = callsCount + 1;
		return nextSequence;
	}

	/**
	 * Records one call in the unit of the current thread; does nothing when no unit runs.
	 * @param call the call, with at least {@code clientMs}
	 */
	public static void record(CcpJsonRepresentation call) {
		boolean notMeasuring = false == isMeasuring();
		if(notMeasuring) {
			return;
		}
		CcpJsonRepresentation unit = UNIT_OF_THE_THREAD.get();
		CcpJsonRepresentation unitWithTheCall = unit.addToList(Fields.dbCalls, call);
		UNIT_OF_THE_THREAD.set(unitWithTheCall);
	}

	/**
	 * Records one read of the cache in the unit of the current thread, counted in the totals and per entity of the key;
	 * does nothing when no unit runs.
	 * @param key the key read
	 * @param hit whether the key was found
	 * @param clientMs the latency of the read as seen by the client
	 */
	public static void recordCacheRead(String key, boolean hit, double clientMs) {
		boolean notMeasuring = false == isMeasuring();
		if(notMeasuring) {
			return;
		}
		CcpJsonRepresentation unit = UNIT_OF_THE_THREAD.get();
		Fields totalCounter = hit ? Fields.cacheHits : Fields.cacheMisses;
		Fields entityCounter = hit ? Fields.hits : Fields.misses;
		String entityName = CcpCacheDecorator.getEntityNameOfTheKey(key);
		CcpFieldName entityField = new CcpFieldName(entityName);
		CcpJsonRepresentation byEntity = unit.getInnerJson(Fields.cacheByEntity);
		CcpJsonRepresentation ofTheEntity = byEntity.getInnerJson(entityField);
		int entityCount = countOf(ofTheEntity, entityCounter);
		CcpJsonRepresentation ofTheEntityCounted = ofTheEntity.put(entityCounter, entityCount + 1);
		CcpJsonRepresentation unitWithTheEntity = unit.addToItem(Fields.cacheByEntity, entityField, ofTheEntityCounted);
		CcpJsonRepresentation unitWithTheRead = addCacheCall(unitWithTheEntity, totalCounter, clientMs);
		UNIT_OF_THE_THREAD.set(unitWithTheRead);
	}

	/**
	 * Records one write or delete sent to the cache in the unit of the current thread; does nothing when no unit runs.
	 * @param clientMs the latency of the command as seen by the client
	 */
	public static void recordCacheWrite(double clientMs) {
		boolean notMeasuring = false == isMeasuring();
		if(notMeasuring) {
			return;
		}
		CcpJsonRepresentation unit = UNIT_OF_THE_THREAD.get();
		CcpJsonRepresentation unitWithTheWrite = addCacheCall(unit, Fields.cacheWrites, clientMs);
		UNIT_OF_THE_THREAD.set(unitWithTheWrite);
	}

	/**
	 * Adds one command to a counter of the cache and its latency to the total.
	 * @param unit the unit
	 * @param counter the counter of the command
	 * @param clientMs the latency of the command
	 * @return the unit with the command
	 */
	private static CcpJsonRepresentation addCacheCall(CcpJsonRepresentation unit, Fields counter, double clientMs) {
		int count = countOf(unit, counter);
		double totalMs = unit.containsAllFields(Fields.cacheClientMs) ? unit.getAsDoubleNumber(Fields.cacheClientMs) : 0;
		double newTotalMs = totalMs + clientMs;
		CcpJsonRepresentation unitWithTheCall = unit.put(counter, count + 1).put(Fields.cacheClientMs, newTotalMs);
		return unitWithTheCall;
	}

	/**
	 * A counter of a JSON.
	 * @param json the JSON
	 * @param counter the counter
	 * @return its value, 0 when absent
	 */
	private static int countOf(CcpJsonRepresentation json, Fields counter) {
		boolean absent = false == json.containsAllFields(counter);
		if(absent) {
			return 0;
		}
		Integer count = json.getAsIntegerNumber(counter);
		return count;
	}

	/**
	 * Ends the unit of the current thread.
	 * @return the trace id, the calls and their totals ({@code dbCallsCount}, {@code dbClientMs}, {@code dbTookMs}), and
	 * the commands to the cache ({@code cacheHits}, {@code cacheMisses}, {@code cacheWrites}, {@code cacheCallsCount},
	 * {@code cacheClientMs}, {@code cacheByEntity}); only the totals, all zero, when no unit runs
	 */
	public static CcpJsonRepresentation finish() {
		CcpJsonRepresentation unit = UNIT_OF_THE_THREAD.get();
		UNIT_OF_THE_THREAD.remove();
		int cacheHits = countOf(unit, Fields.cacheHits);
		int cacheMisses = countOf(unit, Fields.cacheMisses);
		int cacheWrites = countOf(unit, Fields.cacheWrites);
		int cacheCallsCount = cacheHits + cacheMisses + cacheWrites;
		double cacheClientMs = unit.containsAllFields(Fields.cacheClientMs) ? unit.getAsDoubleNumber(Fields.cacheClientMs) : 0;
		double roundedCacheClientMs = roundToTenths(cacheClientMs);
		CcpJsonRepresentation unitWithTheCache = unit
				.put(Fields.cacheHits, cacheHits)
				.put(Fields.cacheMisses, cacheMisses)
				.put(Fields.cacheWrites, cacheWrites)
				.put(Fields.cacheCallsCount, cacheCallsCount)
				.put(Fields.cacheClientMs, roundedCacheClientMs);
		CcpJsonRepresentation summary = summarizeTheDbCalls(unitWithTheCache);
		return summary;
	}

	/**
	 * Adds the totals of the calls to the database.
	 * @param unit the unit
	 * @return the unit with {@code dbCallsCount}, {@code dbClientMs} and {@code dbTookMs}
	 */
	private static CcpJsonRepresentation summarizeTheDbCalls(CcpJsonRepresentation unit) {
		List<CcpJsonRepresentation> calls = unit.getAsJsonList(Fields.dbCalls);
		double clientMs = 0;
		double tookMs = 0;
		for (CcpJsonRepresentation call : calls) {
			Double callClientMs = call.getAsDoubleNumber(Fields.clientMs);
			clientMs += callClientMs;
			boolean withoutTook = false == call.containsAllFields(Fields.tookMs);
			if(withoutTook) {
				continue;
			}
			Double callTookMs = call.getAsDoubleNumber(Fields.tookMs);
			tookMs += callTookMs;
		}
		int callsCount = calls.size();
		double roundedClientMs = roundToTenths(clientMs);
		CcpJsonRepresentation summary = unit
				.put(Fields.dbCallsCount, callsCount)
				.put(Fields.dbClientMs, roundedClientMs)
				.put(Fields.dbTookMs, tookMs);
		return summary;
	}

	/**
	 * Rounds to one decimal place.
	 * @param value the value
	 * @return the rounded value
	 */
	public static double roundToTenths(double value) {
		double tenths = value * 10;
		long roundedTenths = Math.round(tenths);
		double rounded = roundedTenths / 10.0;
		return rounded;
	}
}
