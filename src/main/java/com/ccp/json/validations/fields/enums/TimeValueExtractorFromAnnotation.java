package com.ccp.json.validations.fields.enums;

import java.lang.reflect.Field;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;
import com.ccp.json.validations.fields.annotations.type.CcpJsonFieldTypeTimeBefore;

enum TimeValueExtractorFromAnnotation{
	max("maximum") {
		int getValueFromAnnotation(CcpJsonFieldTypeTimeBefore annotation) {
			int value = annotation.maxValue();
			return value;
		}

		boolean hasError(Long enlapsedTime, Long validationParameter) {
			boolean enlapsedTimeMaior = enlapsedTime > validationParameter;
			return enlapsedTimeMaior;
		}

	},
	exact("exact") {
		int getValueFromAnnotation(CcpJsonFieldTypeTimeBefore annotation) {
			int value = annotation.exactValue();
			return value;
		}

		boolean hasError(Long enlapsedTime, Long validationParameter) {
			boolean enlapsedTimeDiferente = enlapsedTime != validationParameter;
			return enlapsedTimeDiferente;
		}

	},
	min("minimum") {
		int getValueFromAnnotation(CcpJsonFieldTypeTimeBefore annotation) {
			int value = annotation.minValue();
			return value;
		}

		boolean hasError(Long enlapsedTime, Long validationParameter) {
			boolean enlapsedTimeMenor = enlapsedTime < validationParameter;
			return enlapsedTimeMenor;
		}
	}
	;
	
	private final String word;
	
	
	private TimeValueExtractorFromAnnotation(String word) {
		this.word = word;
	}

	abstract int getValueFromAnnotation(CcpJsonFieldTypeTimeBefore annotation); 
	
	protected Long getValueFromAnnotationInMilliseconds(CcpJsonRepresentation json, Field field) {
		CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
		CcpEntityExpurgableOptions intervalType = annotation.intervalType();
		long currentTimeMillis = System.currentTimeMillis();
		Long milliseconds = intervalType.getMilliseconds(currentTimeMillis);
		Integer value = this.getValueFromAnnotation(annotation);
		Long millisecondsVezes = milliseconds * value;
		Long valueOf = Long.valueOf(millisecondsVezes);
		return valueOf;
	}
	
	public final boolean hasError(CcpJsonRepresentation json, Field field, TimeOptions timeOptions) {
		
		Long valueFromAnnotationInMilliseconds = this.getValueFromAnnotationInMilliseconds(json, field);
		String fieldName2 = field.getName();
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName2);

		CcpJsonRepresentation put = json.put(ccpFieldName, valueFromAnnotationInMilliseconds);
		
		Long enlapsedTime = timeOptions.getEnlapsedTime(put, field);
		
		long subtractNumber = timeOptions.subtractNumber(valueFromAnnotationInMilliseconds);
		boolean enlapsedTimeMaior2 = enlapsedTime > subtractNumber;

		return enlapsedTimeMaior2;
	}
	
	public final String getErrorMessage(CcpJsonRepresentation json, Field field, TimeOptions timeOptions) {
		
		String fieldName = field.getName();
		Long valueFromAnnotationInMilliseconds = this.getValueFromAnnotationInMilliseconds(json, field);
		
		CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
		CcpEntityExpurgableOptions intervalType = annotation.intervalType();
		CcpFieldName ccpFieldName2 = new CcpFieldName(fieldName);

		Long providedValue = json.getAsLongNumber(ccpFieldName2);
		CcpTimeDecorator ctd = new CcpTimeDecorator(providedValue);
		String formattedDateTime = ctd.getFormattedDateTime(intervalType.format);
		
		int valueFromAnnotation = this.getValueFromAnnotation(annotation);
		String intervalTypeWord = intervalType.word.toLowerCase();
		String timeOptionsName = timeOptions.name();
		String valorMais = "The field " + fieldName;
		String valorMaisMais = valorMais + " has a value ";
		String valorMaisMaisMais = valorMaisMais + formattedDateTime;
		String valorMaisMaisMaisMais = valorMaisMaisMais
				+ " and this value has to be in the ";
				String valorMaisMaisMaisMaisMais = valorMaisMaisMaisMais + this.word;
				String valorMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMais + " ";
				String valorMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMais 
				+ valueFromAnnotation;
				String valorMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMais + " ";
				String valorMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMais + intervalTypeWord;
				String valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMaisMais + " ";
				String valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMais + timeOptionsName;
				String valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais + " this current time. ";
				String valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais
						+ "But it is ";
						String valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais + valueFromAnnotationInMilliseconds;
						String valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais + " ";
						String valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais + intervalTypeWord;
						String valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais + " ";
						String valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais
						+ timeOptionsName;
						String errorMessage = valorMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMaisMais + " this current time. ";
		return errorMessage;
	}

	public final String getRuleExplanation(Field field, TimeOptions timeOptions) {
		
		String fieldName = field.getName();
		
		CcpJsonFieldTypeTimeBefore annotation = field.getAnnotation(CcpJsonFieldTypeTimeBefore.class);
		CcpEntityExpurgableOptions intervalType = annotation.intervalType();
		
		int valueFromAnnotation = this.getValueFromAnnotation(annotation);
		String intervalTypeWord = intervalType.word.toLowerCase();
		String timeOptionsName = timeOptions.name();
		String valorMais2 = "The field " + fieldName;
		String valorMais2Mais = valorMais2 + " accepts timestamp values that are at ";
		String valorMais2MaisMais = valorMais2Mais + this.word;
		String valorMais2MaisMaisMais = valorMais2MaisMais + " ";
		String valorMais2MaisMaisMaisMais = valorMais2MaisMaisMais 
				+ valueFromAnnotation;
				String valorMais2MaisMaisMaisMaisMais = valorMais2MaisMaisMaisMais + " ";
				String valorMais2MaisMaisMaisMaisMaisMais = valorMais2MaisMaisMaisMaisMais + intervalTypeWord;
				String valorMais2MaisMaisMaisMaisMaisMaisMais = valorMais2MaisMaisMaisMaisMaisMais + " ";
				String valorMais2MaisMaisMaisMaisMaisMaisMaisMais = valorMais2MaisMaisMaisMaisMaisMaisMais + timeOptionsName;
				String errorMessage = valorMais2MaisMaisMaisMaisMaisMaisMaisMais + " the current time. "
						;
		return errorMessage;
	}
	
	abstract boolean hasError(Long enlapsedTime, Long validationParameter);
}
