package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;

/**
 * Which bound of {@code @CcpJsonFieldTypeTimeBefore} / {@code @CcpJsonFieldTypeTimeAfter} is evaluated: the maximum,
 * the minimum or the exact value. The comparison is made in the granularity declared in the annotation (days, hours,
 * etc), not in milliseconds, which is how the developer writes the bounds.
 * <p>
 * The direction of the comparison (backwards or forwards in time) comes from the {@code TimeOptions} received, which also
 * knows how to read the right annotation of the field.
 */
enum TimeValueExtractorFromAnnotation{
	/** The distance must not exceed {@code maxValue}. */
	max("maximum") {
		/**
		 * Reads {@code maxValue}.
		 * @param field the field
		 * @param timeOptions the direction of the comparison
		 * @return the bound
		 */
		int getValueFromAnnotation(Field field, TimeOptions timeOptions) {
			int maxValue = timeOptions.getMaxValue(field);
			return maxValue;
		}

		/**
		 * Active when {@code maxValue} is below {@code Integer.MAX_VALUE}.
		 * @param field the field
		 * @param timeOptions the direction of the comparison
		 * @return {@code true} when configured
		 */
		boolean isActive(Field field, TimeOptions timeOptions) {
			int maxValue = timeOptions.getMaxValue(field);
			boolean isActive = maxValue < Integer.MAX_VALUE;
			return isActive;
		}

		/**
		 * Out of bounds when the distance is greater than the bound.
		 * @param enlapsedInterval the distance in the granularity of the annotation
		 * @param validationParameter the bound
		 * @return {@code true} when out of bounds
		 */
		boolean isOutOfBounds(Long enlapsedInterval, Integer validationParameter) {
			long validationParameterValue = validationParameter.longValue();
			long enlapsedIntervalValue = enlapsedInterval.longValue();
			boolean outOfBounds = enlapsedIntervalValue > validationParameterValue;
			return outOfBounds;
		}
	},
	/** The distance must equal {@code exactValue}. */
	exact("exact") {
		/**
		 * Reads {@code exactValue}.
		 * @param field the field
		 * @param timeOptions the direction of the comparison
		 * @return the exact value
		 */
		int getValueFromAnnotation(Field field, TimeOptions timeOptions) {
			int exactValue = timeOptions.getExactValue(field);
			return exactValue;
		}

		/**
		 * Active when {@code exactValue} is below {@code Integer.MAX_VALUE}.
		 * @param field the field
		 * @param timeOptions the direction of the comparison
		 * @return {@code true} when configured
		 */
		boolean isActive(Field field, TimeOptions timeOptions) {
			int exactValue = timeOptions.getExactValue(field);
			boolean isActive = exactValue < Integer.MAX_VALUE;
			return isActive;
		}

		/**
		 * Out of bounds when the distance differs from the exact value.
		 * @param enlapsedInterval the distance in the granularity of the annotation
		 * @param validationParameter the exact value
		 * @return {@code true} when out of bounds
		 */
		boolean isOutOfBounds(Long enlapsedInterval, Integer validationParameter) {
			long validationParameterValue = validationParameter.longValue();
			long enlapsedIntervalValue = enlapsedInterval.longValue();
			boolean saoIguais = enlapsedIntervalValue == validationParameterValue;
			boolean outOfBounds = false == saoIguais;
			return outOfBounds;
		}
	},
	/** The distance must not be below {@code minValue}. */
	min("minimum") {
		/**
		 * Reads {@code minValue}.
		 * @param field the field
		 * @param timeOptions the direction of the comparison
		 * @return the bound
		 */
		int getValueFromAnnotation(Field field, TimeOptions timeOptions) {
			int minValue = timeOptions.getMinValue(field);
			return minValue;
		}

		/**
		 * Active when {@code minValue} is above {@code Integer.MIN_VALUE}.
		 * @param field the field
		 * @param timeOptions the direction of the comparison
		 * @return {@code true} when configured
		 */
		boolean isActive(Field field, TimeOptions timeOptions) {
			int minValue = timeOptions.getMinValue(field);
			boolean isActive = minValue > Integer.MIN_VALUE;
			return isActive;
		}

		/**
		 * Out of bounds when the distance is less than the bound.
		 * @param enlapsedInterval the distance in the granularity of the annotation
		 * @param validationParameter the bound
		 * @return {@code true} when out of bounds
		 */
		boolean isOutOfBounds(Long enlapsedInterval, Integer validationParameter) {
			long validationParameterValue = validationParameter.longValue();
			long enlapsedIntervalValue = enlapsedInterval.longValue();
			boolean outOfBounds = enlapsedIntervalValue < validationParameterValue;
			return outOfBounds;
		}
	}
	;

	/** The English name of the bound, used in messages. */
	private final String word;


	/**
	 * Associates the bound with its English name.
	 * @param word the name used in messages
	 */
	private TimeValueExtractorFromAnnotation(String word) {
		this.word = word;
	}

	/**
	 * Value of this bound as written in the annotation of the field.
	 * @param field the field
	 * @param timeOptions the direction of the comparison
	 * @return the bound
	 */
	abstract int getValueFromAnnotation(Field field, TimeOptions timeOptions);

	/**
	 * Whether this bound was really declared in the field, or is at the default value of the annotation.
	 * @param field the field
	 * @param timeOptions the direction of the comparison
	 * @return {@code true} when declared
	 */
	abstract boolean isActive(Field field, TimeOptions timeOptions);

	/**
	 * Compares the distance with the bound, both in the granularity of the annotation.
	 * @param enlapsedInterval the distance
	 * @param validationParameter the bound
	 * @return {@code true} when out of bounds
	 */
	abstract boolean isOutOfBounds(Long enlapsedInterval, Integer validationParameter);

	/**
	 * This bound converted to milliseconds, for diagnostic messages.
	 * @param json the JSON being validated
	 * @param field the field
	 * @return the bound in milliseconds
	 */
	protected Long getValueFromAnnotationInMilliseconds(CcpJsonRepresentation json, Field field) {
		TimeOptions timeOptions = TimeOptions.getTimeOptions(field);
		CcpEntityExpurgableOptions intervalType = timeOptions.getIntervalType(field);
		long currentTimeMillis = System.currentTimeMillis();
		long milliseconds = intervalType.getMilliseconds(currentTimeMillis);
		int value = this.getValueFromAnnotation(field, timeOptions);
		long total = milliseconds * value;
		Long valueOf = Long.valueOf(total);
		return valueOf;
	}

	/**
	 * Tells whether the timestamp breaks this bound; an undeclared bound never fails.
	 * @param json the JSON being validated
	 * @param field the field
	 * @param timeOptions the direction of the comparison
	 * @return {@code true} when the bound is broken
	 */
	public final boolean hasError(CcpJsonRepresentation json, Field field, TimeOptions timeOptions) {

		boolean active = this.isActive(field, timeOptions);

		boolean isNotActive = false == active;

		if(isNotActive) {
			return false;
		}

		Long enlapsedInterval = timeOptions.getEnlapsedInterval(json, field);
		Integer validationParameter = this.getValueFromAnnotation(field, timeOptions);

		boolean outOfBounds = this.isOutOfBounds(enlapsedInterval, validationParameter);

		return outOfBounds;
	}

	/**
	 * Whether this bound produces a rule to be documented and validated for the field.
	 * @param field the field
	 * @param timeOptions the direction of the comparison
	 * @return {@code true} when the bound is declared
	 */
	public final boolean hasRuleExplanation(Field field, TimeOptions timeOptions) {
		boolean active = this.isActive(field, timeOptions);
		return active;
	}

	/**
	 * Describes the provided date, the bound and the actual distance. The provided date is rebuilt as "now minus the
	 * distance", which is right for {@code _before} but mirrors the date for {@code _after}.
	 * @param json the JSON being validated
	 * @param field the field
	 * @param timeOptions the direction of the comparison
	 * @return the error message
	 */
	public final String getErrorMessage(CcpJsonRepresentation json, Field field, TimeOptions timeOptions) {

		String fieldName = field.getName();
		CcpEntityExpurgableOptions intervalType = timeOptions.getIntervalType(field);

		Long providedValue = timeOptions.getEnlapsedTime(json, field);
		long currentTimeMillis = System.currentTimeMillis();
		long providedTimestamp = currentTimeMillis - providedValue;
		CcpTimeDecorator ctd = new CcpTimeDecorator(providedTimestamp);
		String formattedDateTime = ctd.getFormattedDateTime(intervalType.format);

		int valueFromAnnotation = this.getValueFromAnnotation(field, timeOptions);
		Long enlapsedInterval = timeOptions.getEnlapsedInterval(json, field);
		String intervalTypeWord = intervalType.word.toLowerCase();
		String timeOptionsName = timeOptions.name();

		String withField = "The field " + fieldName;
		String withValue = withField + " has a value " + formattedDateTime;
		String withBound = withValue + " and this value has to be in the " + this.word;
		String withAmount = withBound + " " + valueFromAnnotation + " " + intervalTypeWord;
		String withDirection = withAmount + " " + timeOptionsName + " this current time. ";
		String errorMessage = withDirection + "But it is " + enlapsedInterval + " " + intervalTypeWord + " " + timeOptionsName + " this current time. ";

		return errorMessage;
	}

	/**
	 * Explains the bound in the granularity of the annotation.
	 * @param field the field
	 * @param timeOptions the direction of the comparison
	 * @return the explanation
	 */
	public final String getRuleExplanation(Field field, TimeOptions timeOptions) {

		String fieldName = field.getName();
		CcpEntityExpurgableOptions intervalType = timeOptions.getIntervalType(field);

		int valueFromAnnotation = this.getValueFromAnnotation(field, timeOptions);
		String intervalTypeWord = intervalType.word.toLowerCase();
		String timeOptionsName = timeOptions.name();

		String withField = "The field " + fieldName;
		String withBound = withField + " accepts timestamp values that are at " + this.word;
		String withAmount = withBound + " " + valueFromAnnotation + " " + intervalTypeWord;
		String ruleExplanation = withAmount + " " + timeOptionsName + " the current time. ";

		return ruleExplanation;
	}
}
