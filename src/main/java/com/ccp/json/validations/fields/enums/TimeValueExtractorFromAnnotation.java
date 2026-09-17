package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;

/**
 * Qual dos limites de {@code @CcpJsonFieldTypeTimeBefore} / {@code @CcpJsonFieldTypeTimeAfter} está
 * sendo avaliado: o máximo, o mínimo ou o valor exato. A comparação é feita na granularidade
 * declarada na anotação (dias, horas, etc), não em milissegundos, que é como os limites são
 * escritos pelo desenvolvedor.
 *
 * <p>O sentido da comparação (para trás ou para frente no tempo) vem do {@code TimeOptions}
 * recebido, que também é quem sabe ler a anotação correta do campo.
 */
enum TimeValueExtractorFromAnnotation{
	max("maximum") {
		int getValueFromAnnotation(Field field, TimeOptions timeOptions) {
			int maxValue = timeOptions.getMaxValue(field);
			return maxValue;
		}

		boolean isActive(Field field, TimeOptions timeOptions) {
			int maxValue = timeOptions.getMaxValue(field);
			boolean isActive = maxValue < Integer.MAX_VALUE;
			return isActive;
		}

		boolean isOutOfBounds(Long enlapsedInterval, Integer validationParameter) {
			long validationParameterValue = validationParameter.longValue();
			long enlapsedIntervalValue = enlapsedInterval.longValue();
			boolean outOfBounds = enlapsedIntervalValue > validationParameterValue;
			return outOfBounds;
		}
	},
	exact("exact") {
		int getValueFromAnnotation(Field field, TimeOptions timeOptions) {
			int exactValue = timeOptions.getExactValue(field);
			return exactValue;
		}

		boolean isActive(Field field, TimeOptions timeOptions) {
			int exactValue = timeOptions.getExactValue(field);
			boolean isActive = exactValue < Integer.MAX_VALUE;
			return isActive;
		}

		boolean isOutOfBounds(Long enlapsedInterval, Integer validationParameter) {
			long validationParameterValue = validationParameter.longValue();
			long enlapsedIntervalValue = enlapsedInterval.longValue();
			boolean saoIguais = enlapsedIntervalValue == validationParameterValue;
			boolean outOfBounds = false == saoIguais;
			return outOfBounds;
		}
	},
	min("minimum") {
		int getValueFromAnnotation(Field field, TimeOptions timeOptions) {
			int minValue = timeOptions.getMinValue(field);
			return minValue;
		}

		boolean isActive(Field field, TimeOptions timeOptions) {
			int minValue = timeOptions.getMinValue(field);
			boolean isActive = minValue > Integer.MIN_VALUE;
			return isActive;
		}

		boolean isOutOfBounds(Long enlapsedInterval, Integer validationParameter) {
			long validationParameterValue = validationParameter.longValue();
			long enlapsedIntervalValue = enlapsedInterval.longValue();
			boolean outOfBounds = enlapsedIntervalValue < validationParameterValue;
			return outOfBounds;
		}
	}
	;

	private final String word;


	private TimeValueExtractorFromAnnotation(String word) {
		this.word = word;
	}

	/** Valor deste limite tal como escrito na anotação do campo. */
	abstract int getValueFromAnnotation(Field field, TimeOptions timeOptions);

	/** Se este limite foi de fato declarado no campo, ou se está no valor padrão da anotação. */
	abstract boolean isActive(Field field, TimeOptions timeOptions);

	/** Compara o tempo decorrido com o limite, ambos na granularidade da anotação. */
	abstract boolean isOutOfBounds(Long enlapsedInterval, Integer validationParameter);

	/** Este limite convertido para milissegundos, usado nas mensagens de diagnóstico. */
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

	/** Se este limite gera regra a ser documentada e validada para o campo. */
	public final boolean hasRuleExplanation(Field field, TimeOptions timeOptions) {
		boolean active = this.isActive(field, timeOptions);
		return active;
	}

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

		String comCampo = "The field " + fieldName;
		String comValor = comCampo + " has a value " + formattedDateTime;
		String comLimite = comValor + " and this value has to be in the " + this.word;
		String comQuantidade = comLimite + " " + valueFromAnnotation + " " + intervalTypeWord;
		String comSentido = comQuantidade + " " + timeOptionsName + " this current time. ";
		String errorMessage = comSentido + "But it is " + enlapsedInterval + " " + intervalTypeWord + " " + timeOptionsName + " this current time. ";

		return errorMessage;
	}

	public final String getRuleExplanation(Field field, TimeOptions timeOptions) {

		String fieldName = field.getName();
		CcpEntityExpurgableOptions intervalType = timeOptions.getIntervalType(field);

		int valueFromAnnotation = this.getValueFromAnnotation(field, timeOptions);
		String intervalTypeWord = intervalType.word.toLowerCase();
		String timeOptionsName = timeOptions.name();

		String comCampo = "The field " + fieldName;
		String comLimite = comCampo + " accepts timestamp values that are at " + this.word;
		String comQuantidade = comLimite + " " + valueFromAnnotation + " " + intervalTypeWord;
		String ruleExplanation = comQuantidade + " " + timeOptionsName + " the current time. ";

		return ruleExplanation;
	}
}
