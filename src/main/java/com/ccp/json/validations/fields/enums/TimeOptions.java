package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeAfter;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;

/**
 * Sentido da comparação temporal de um campo: {@code _before} mede quanto tempo se passou desde o
 * timestamp informado e {@code _after} mede quanto falta até ele. Cada constante sabe ler a sua
 * própria anotação ({@code @CcpJsonFieldTypeTimeBefore} ou {@code @CcpJsonFieldTypeTimeAfter}), de
 * modo que o resto do mecanismo de validação não precisa saber qual das duas está em uso.
 */
enum TimeOptions{
	_before {
		long subtractNumber(long time) {
			long currentTimeMillis = System.currentTimeMillis();
			long enlapsedTime = currentTimeMillis - time;
			return enlapsedTime;
		}

		public CcpEntityExpurgableOptions getIntervalType(Field field) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			CcpEntityExpurgableOptions intervalType = annotation.intervalType();
			return intervalType;
		}

		public int getMaxValue(Field field) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			int maxValue = annotation.maxValue();
			return maxValue;
		}

		public int getMinValue(Field field) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			int minValue = annotation.minValue();
			return minValue;
		}

		public int getExactValue(Field field) {
			CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
			int exactValue = annotation.exactValue();
			return exactValue;
		}
	},
	_after {
		long subtractNumber(long time) {
			long currentTimeMillis = System.currentTimeMillis();
			long enlapsedTime = time - currentTimeMillis;
			return enlapsedTime;
		}

		public CcpEntityExpurgableOptions getIntervalType(Field field) {
			CcpJsonFieldTypeTimeAfter annotation = field.getAnnotation(CcpJsonFieldTypeTimeAfter.class);
			CcpEntityExpurgableOptions intervalType = annotation.intervalType();
			return intervalType;
		}

		public int getMaxValue(Field field) {
			CcpJsonFieldTypeTimeAfter annotation = field.getAnnotation(CcpJsonFieldTypeTimeAfter.class);
			int maxValue = annotation.maxValue();
			return maxValue;
		}

		public int getMinValue(Field field) {
			CcpJsonFieldTypeTimeAfter annotation = field.getAnnotation(CcpJsonFieldTypeTimeAfter.class);
			int minValue = annotation.minValue();
			return minValue;
		}

		public int getExactValue(Field field) {
			CcpJsonFieldTypeTimeAfter annotation = field.getAnnotation(CcpJsonFieldTypeTimeAfter.class);
			int exactValue = annotation.exactValue();
			return exactValue;
		}
	}
	;
	abstract long subtractNumber(long time);

	/** Granularidade declarada na anotação deste campo (dias, horas, etc). */
	public abstract CcpEntityExpurgableOptions getIntervalType(Field field);

	/** Limite superior declarado na anotação deste campo, na granularidade dela. */
	public abstract int getMaxValue(Field field);

	/** Limite inferior declarado na anotação deste campo, na granularidade dela. */
	public abstract int getMinValue(Field field);

	/** Valor exato declarado na anotação deste campo, na granularidade dela. */
	public abstract int getExactValue(Field field);

	/**
	 * Descobre o sentido da comparação pela anotação presente no campo. Usado onde só o campo está
	 * disponível.
	 */
	public static TimeOptions getTimeOptions(Field field) {
		boolean annotationPresent = field.isAnnotationPresent(CcpJsonFieldTypeTimeBefore.class);

		if(annotationPresent) {
			return _before;
		}
		return _after;
	}

	/** Tempo decorrido, em milissegundos, entre o timestamp do campo e o momento atual. */
	public Long getEnlapsedTime(CcpJsonRepresentation json, Field field) {
		String fieldName = field.getName();
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName);

		Long time = json.getAsLongNumber(ccpFieldName);

		long providedValue = this.subtractNumber(time);

		return providedValue;
	}

	/**
	 * O mesmo tempo decorrido, convertido para a granularidade declarada na anotação. É nesta
	 * unidade que os limites da anotação são expressos: {@code maxValue = 7} com
	 * {@code intervalType = daily} quer dizer sete dias.
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
