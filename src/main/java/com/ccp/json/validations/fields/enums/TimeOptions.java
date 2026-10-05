package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeAfter;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;

/**
 * Direction of the time comparison of a field: {@code _before} measures how long ago the timestamp is and {@code _after}
 * how far ahead it is. Each constant reads its own annotation ({@code @CcpJsonFieldTypeTimeBefore} or
 * {@code @CcpJsonFieldTypeTimeAfter}), so the rest of the validation does not need to know which one is in use.
 */
enum TimeOptions{
	/** Past timestamps ({@code @CcpJsonFieldTypeTimeBefore}). */
	_before {
		/**
		 * Returns how long ago the timestamp is: now minus the timestamp.
		 * @param time the timestamp
		 * @return the elapsed milliseconds
		 */
		long subtractNumber(long time) {
			long currentTimeMillis = System.currentTimeMillis();
			long enlapsedTime = currentTimeMillis - time;
			return enlapsedTime;
		}

		/**
		 * Reads the granularity of {@code @CcpJsonFieldTypeTimeBefore}.
		 * @param field the field
		 * @return the granularity
		 */
		public CcpEntityExpurgableOptions getIntervalType(Field field) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			CcpEntityExpurgableOptions intervalType = annotation.intervalType();
			return intervalType;
		}

		/**
		 * Reads {@code maxValue} of {@code @CcpJsonFieldTypeTimeBefore}.
		 * @param field the field
		 * @return the maximum
		 */
		public int getMaxValue(Field field) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			int maxValue = annotation.maxValue();
			return maxValue;
		}

		/**
		 * Reads {@code minValue} of {@code @CcpJsonFieldTypeTimeBefore}.
		 * @param field the field
		 * @return the minimum
		 */
		public int getMinValue(Field field) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			int minValue = annotation.minValue();
			return minValue;
		}

		/**
		 * Reads {@code exactValue} of {@code @CcpJsonFieldTypeTimeBefore}.
		 * @param field the field
		 * @return the exact value
		 */
		public int getExactValue(Field field) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			int exactValue = annotation.exactValue();
			return exactValue;
		}
	},
	/** Future timestamps ({@code @CcpJsonFieldTypeTimeAfter}). */
	_after {
		/**
		 * Returns how far ahead the timestamp is: the timestamp minus now.
		 * @param time the timestamp
		 * @return the remaining milliseconds
		 */
		long subtractNumber(long time) {
			long currentTimeMillis = System.currentTimeMillis();
			long enlapsedTime = time - currentTimeMillis;
			return enlapsedTime;
		}

		/**
		 * Reads the granularity of {@code @CcpJsonFieldTypeTimeAfter}.
		 * @param field the field
		 * @return the granularity
		 */
		public CcpEntityExpurgableOptions getIntervalType(Field field) {
			CcpJsonFieldTypeTimeAfter annotation = field.getAnnotation(CcpJsonFieldTypeTimeAfter.class);
			CcpEntityExpurgableOptions intervalType = annotation.intervalType();
			return intervalType;
		}

		/**
		 * Reads {@code maxValue} of {@code @CcpJsonFieldTypeTimeAfter}.
		 * @param field the field
		 * @return the maximum
		 */
		public int getMaxValue(Field field) {
			CcpJsonFieldTypeTimeAfter annotation = field.getAnnotation(CcpJsonFieldTypeTimeAfter.class);
			int maxValue = annotation.maxValue();
			return maxValue;
		}

		/**
		 * Reads {@code minValue} of {@code @CcpJsonFieldTypeTimeAfter}.
		 * @param field the field
		 * @return the minimum
		 */
		public int getMinValue(Field field) {
			CcpJsonFieldTypeTimeAfter annotation = field.getAnnotation(CcpJsonFieldTypeTimeAfter.class);
			int minValue = annotation.minValue();
			return minValue;
		}

		/**
		 * Reads {@code exactValue} of {@code @CcpJsonFieldTypeTimeAfter}.
		 * @param field the field
		 * @return the exact value
		 */
		public int getExactValue(Field field) {
			CcpJsonFieldTypeTimeAfter annotation = field.getAnnotation(CcpJsonFieldTypeTimeAfter.class);
			int exactValue = annotation.exactValue();
			return exactValue;
		}
	}
	;
	/**
	 * Returns the distance between now and the timestamp, in the direction of this constant.
	 * @param time the timestamp
	 * @return the distance in milliseconds
	 */
	abstract long subtractNumber(long time);

	/**
	 * Granularity declared in the annotation of the field (days, hours, etc).
	 * @param field the field
	 * @return the granularity
	 */
	public abstract CcpEntityExpurgableOptions getIntervalType(Field field);

	/**
	 * Upper bound declared in the annotation of the field, in its granularity.
	 * @param field the field
	 * @return the upper bound
	 */
	public abstract int getMaxValue(Field field);

	/**
	 * Lower bound declared in the annotation of the field, in its granularity.
	 * @param field the field
	 * @return the lower bound
	 */
	public abstract int getMinValue(Field field);

	/**
	 * Exact value declared in the annotation of the field, in its granularity.
	 * @param field the field
	 * @return the exact value
	 */
	public abstract int getExactValue(Field field);

	/**
	 * Finds the direction of the comparison from the annotation of the field; used where only the field is available.
	 * @param field the field
	 * @return {@code _before} for {@code @CcpJsonFieldTypeTimeBefore}, {@code _after} otherwise
	 */
	public static TimeOptions getTimeOptions(Field field) {
		boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeTimeBefore.class);

		if(annotationPresent) {
			return _before;
		}
		return _after;
	}

	/**
	 * Distance, in milliseconds, between the timestamp of the field and now, in the direction of this constant.
	 * @param json the JSON being validated
	 * @param field the field
	 * @return the distance in milliseconds
	 */
	public Long getEnlapsedTime(CcpJsonRepresentation json, Field field) {
		String fieldName = field.getName();
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName);

		Long time = json.getAsLongNumber(ccpFieldName);

		long providedValue = this.subtractNumber(time);

		return providedValue;
	}

	/**
	 * The same distance converted to the granularity of the annotation (truncated). The bounds of the annotation are
	 * written in this unit: {@code maxValue = 7} with {@code intervalType = daily} means seven days.
	 * @param json the JSON being validated
	 * @param field the field
	 * @return the distance in the granularity of the annotation
	 */
	public Long getEnlapsedInterval(CcpJsonRepresentation json, Field field) {
		Long enlapsedTime = this.getEnlapsedTime(json, field);
		CcpEntityExpurgableOptions intervalType = this.getIntervalType(field);
		long currentTimeMillis = System.currentTimeMillis();
		long milliseconds = intervalType.getMilliseconds(currentTimeMillis);
		long enlapsedInterval = enlapsedTime / milliseconds;
		return enlapsedInterval;
	}
}
