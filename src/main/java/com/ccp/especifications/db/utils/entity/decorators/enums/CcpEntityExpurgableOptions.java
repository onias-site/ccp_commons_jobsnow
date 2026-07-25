package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpTimeDecorator;

/**
 * Granularidades de expiração para entidades descartáveis ({@code @CcpEntityDisposable}). Cada
 * constante encapsula o campo de calendário correspondente, o formato de data, o tempo de expiração
 * de cache em segundos e o equivalente em milissegundos.
 */
public enum CcpEntityExpurgableOptions{
	yearly(Calendar.YEAR, "yyyy", 86400, 31_536_000_000L, "years"){
		public long getMilliseconds(Long milliSeconds) {
			long total = getMilliseconds(milliSeconds, Calendar.DAY_OF_YEAR);
			return total;
		}

	}
	,minute(Calendar.MINUTE, "ddMMyyyy HH:mm", 60, 60_000, "minutes")
	,second(Calendar.SECOND, "ddMMyyyy HH:mm:ss", 1, 1_000, "seconds")
	,monthly(Calendar.MONTH, "yyyyMM", 86400, 2_592_000_000L, "months"){
		public long getMilliseconds(Long milliSeconds) {
			long total = getMilliseconds(milliSeconds, Calendar.DAY_OF_MONTH);
			return total;
		}
	}
	,daily(Calendar.DAY_OF_MONTH, "ddMMyyyy", 86400, 86_400_000, "days")
	,hourly(Calendar.HOUR_OF_DAY, "ddMMyyyy HH", 3600, 3_600_000, "hours")
	,millisecond(Calendar.MILLISECOND, "dd/MM/yyyy HH:mm:ss.SSS", 1, 1, "milliseconds")
	;
	private final int calendarField;
	private final long milliseconds;
	public final int cacheExpires;
	public final String format;
	public final String word;
	
	
	public long getMilliseconds(Long milliSeconds) {
		return this.milliseconds;
	}

	private CcpEntityExpurgableOptions(int calendarField, String format, int cacheExpires, long milliseconds, String word) {
		this.calendarField = calendarField;
		this.cacheExpires = cacheExpires;
		this.milliseconds = milliseconds;
		this.format = format;
		this.word = word;
	}

	/**
	 * Formata o timestamp {@code date} (em milissegundos) usando o padrão desta granularidade.
	 * @param date timestamp em milissegundos
	 */
	public String getFormattedDate(Long date) {
		Date d = new Date();
		d.setTime(date);
		String format = new SimpleDateFormat(this.format).format(d);
		return format;
	}
	
	/** Formata o instante atual usando o padrão desta granularidade. */
	public String getFormattedDate() {
		String formattedDate = getFormattedDate(System.currentTimeMillis());
		return formattedDate;
	}

	/** Retorna o timestamp do próximo período nesta granularidade a partir do instante atual. */
	public Long getNextTimeStamp() {
		Calendar cal = new CcpTimeDecorator().getBrazilianCalendar();
		cal.add(this.calendarField, 1);
		long timeInMillis = cal.getTimeInMillis();
		return timeInMillis;
	}

	/**
	 * Retorna o timestamp do próximo período nesta granularidade a partir de {@code timestamp}.
	 * @param timestamp timestamp de referência em milissegundos
	 */
	public Long getNextTimeStamp(Long timestamp) {
		Calendar cal = new CcpTimeDecorator(timestamp).getBrazilianCalendar();
		cal.add(this.calendarField, 1);
		long timeInMillis = cal.getTimeInMillis();
		return timeInMillis;
	}
	
	/** Retorna o próximo período (a partir do instante atual) formatado como data completa. */
	public String getNextDate() {
		Long nextTimeStamp = this.getNextTimeStamp();
		CcpTimeDecorator ctd = new CcpTimeDecorator(nextTimeStamp);
		String formattedDateTime = ctd.getFormattedDateTime("dd/MM/yyyy HH:mm:ss.SSS");
		return formattedDateTime;
	}
	
	/**
	 * Retorna o próximo período a partir de {@code timestamp} formatado como data completa.
	 * @param timestamp timestamp de referência em milissegundos
	 */
	public String getNextDate(Long timestamp) {
		Long nextTimeStamp = this.getNextTimeStamp(timestamp);
		CcpTimeDecorator ctd = new CcpTimeDecorator(nextTimeStamp);
		String formattedDateTime = ctd.getFormattedDateTime("dd/MM/yyyy HH:mm:ss.SSS");
		return formattedDateTime;
	}

	public final long getMilliseconds(Long milliSeconds, int field) {
		Calendar instance = new GregorianCalendar();
		instance.setTimeInMillis(milliSeconds);
		int actualMaximum = instance.getActualMaximum(field);
		long total = 86_400_000L * actualMaximum;
		return total;
	}

	@SuppressWarnings("serial")
	public static class CcpExpurgableOptionNotFound extends RuntimeException {
		public final String format;
		private CcpExpurgableOptionNotFound(String format) {
			super("The format '" + format + "' whas not found in the following list: " + Arrays.asList(CcpEntityExpurgableOptions.values())
			.stream().map(x -> x.format).collect(Collectors.toList())
			);
			this.format = format;
		}
	}

}
