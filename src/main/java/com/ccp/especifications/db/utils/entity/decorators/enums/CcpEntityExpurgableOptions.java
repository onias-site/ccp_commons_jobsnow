package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ccp.decorators.CcpTimeDecorator;

/**
 * Expiration granularities of the disposable entities. Each constant holds its calendar field, its date format (which
 * becomes part of the id of the disposable record), its cache expiration in seconds and its length in milliseconds.
 */
public enum CcpEntityExpurgableOptions{
	/** One year; its length is the number of days of the year of the given timestamp. */
	yearly(Calendar.YEAR, "yyyy", 86400, 31_536_000_000L, "years"){
		/**
		 * Returns the length of the year of the timestamp (365 or 366 days).
		 * @param milliSeconds the reference timestamp
		 * @return the length in milliseconds
		 */
		public long getMilliseconds(Long milliSeconds) {
			long total = getMilliseconds(milliSeconds, Calendar.DAY_OF_YEAR);
			return total;
		}

	}
	,
	/** One minute. */
	minute(Calendar.MINUTE, "ddMMyyyy HH:mm", 60, 60_000, "minutes")
	,
	/** One second. */
	second(Calendar.SECOND, "ddMMyyyy HH:mm:ss", 1, 1_000, "seconds")
	/** One month; its length is the number of days of the month of the given timestamp. */
	,monthly(Calendar.MONTH, "yyyyMM", 86400, 2_592_000_000L, "months"){
		/**
		 * Returns the length of the month of the timestamp (28 to 31 days).
		 * @param milliSeconds the reference timestamp
		 * @return the length in milliseconds
		 */
		public long getMilliseconds(Long milliSeconds) {
			long total = getMilliseconds(milliSeconds, Calendar.DAY_OF_MONTH);
			return total;
		}
	}
	,
	/** One day. */
	daily(Calendar.DAY_OF_MONTH, "ddMMyyyy", 86400, 86_400_000, "days")
	,
	/** One hour. */
	hourly(Calendar.HOUR_OF_DAY, "ddMMyyyy HH", 3600, 3_600_000, "hours")
	,
	/** One millisecond. */
	millisecond(Calendar.MILLISECOND, "dd/MM/yyyy HH:mm:ss.SSS", 1, 1, "milliseconds")
	;
	/** The {@code Calendar} field advanced by one period. */
	private final int calendarField;
	/** The fixed length of one period in milliseconds. */
	private final long milliseconds;
	/** How long, in seconds, a disposable record of this granularity stays cached. */
	public final int cacheExpires;
	/** The {@code SimpleDateFormat} pattern of one period. */
	public final String format;
	/** The plural English name of the period (e.g. "minutes"), used in messages. */
	public final String word;
	
	
	/**
	 * Returns the length of one period in milliseconds.
	 * @param milliSeconds the reference timestamp (used only by the yearly and monthly granularities)
	 * @return the length in milliseconds
	 */
	public long getMilliseconds(Long milliSeconds) {
		return this.milliseconds;
	}

	/**
	 * Associates the granularity with its settings.
	 * @param calendarField the calendar field advanced by one period
	 * @param format the date pattern of one period
	 * @param cacheExpires the cache expiration in seconds
	 * @param milliseconds the fixed length of one period
	 * @param word the plural name of the period
	 */
	private CcpEntityExpurgableOptions(int calendarField, String format, int cacheExpires, long milliseconds, String word) {
		this.calendarField = calendarField;
		this.cacheExpires = cacheExpires;
		this.milliseconds = milliseconds;
		this.format = format;
		this.word = word;
	}

	/**
	 * Formats the timestamp with the pattern of this granularity, in America/Sao_Paulo (see
	 * {@code CcpTimeDecorator.getFormattedDateTime}); until 2026-10-07 it used the time zone of the JVM.
	 * @param date the timestamp in milliseconds
	 * @return the formatted date
	 */
	public String getFormattedDate(Long date) {
		CcpTimeDecorator timeDecorator = new CcpTimeDecorator(date);
		String format = timeDecorator.getFormattedDateTime(this.format);
		return format;
	}
	
	/**
	 * Formats the current instant with the pattern of this granularity.
	 * @return the formatted date
	 */
	public String getFormattedDate() {
		long currentTimeMillis = System.currentTimeMillis();
		String formattedDate = getFormattedDate(currentTimeMillis);
		return formattedDate;
	}

	/**
	 * Returns the current instant plus one period, in the America/Sao_Paulo calendar.
	 * @return the timestamp of the next period
	 */
	public Long getNextTimeStamp() {
		CcpTimeDecorator ccpTimeDecorator = new CcpTimeDecorator();
		Calendar cal = ccpTimeDecorator.getBrazilianCalendar();
		cal.add(this.calendarField, 1);
		long timeInMillis = cal.getTimeInMillis();
		return timeInMillis;
	}

	/**
	 * Returns the given timestamp plus one period, in the America/Sao_Paulo calendar.
	 * @param timestamp the reference timestamp
	 * @return the timestamp of the period after the reference
	 */
	public Long getNextTimeStamp(Long timestamp) {
		CcpTimeDecorator ccpTimeDecorator2 = new CcpTimeDecorator(timestamp);
		Calendar cal = ccpTimeDecorator2.getBrazilianCalendar();
		cal.add(this.calendarField, 1);
		long timeInMillis = cal.getTimeInMillis();
		return timeInMillis;
	}
	
	/**
	 * Returns the current instant plus one period, formatted as {@code dd/MM/yyyy HH:mm:ss.SSS}.
	 * @return the formatted date
	 */
	public String getNextDate() {
		Long nextTimeStamp = this.getNextTimeStamp();
		CcpTimeDecorator ctd = new CcpTimeDecorator(nextTimeStamp);
		String formattedDateTime = ctd.getFormattedDateTime(millisecond.format);
		return formattedDateTime;
	}
	
	/**
	 * Returns {@link #getNextTimeStamp(Long)} formatted as {@code dd/MM/yyyy HH:mm:ss.SSS}.
	 * @param timestamp the reference timestamp
	 * @return the formatted date
	 */
	public String getNextDate(Long timestamp) {
		Long nextTimeStamp = this.getNextTimeStamp(timestamp);
		CcpTimeDecorator ctd = new CcpTimeDecorator(nextTimeStamp);
		String formattedDateTime = ctd.getFormattedDateTime(millisecond.format);
		return formattedDateTime;
	}

	/**
	 * Returns as many days as the maximum value of the calendar field in the timestamp's period (e.g. the days of its month).
	 * @param milliSeconds the reference timestamp
	 * @param field {@code DAY_OF_MONTH} or {@code DAY_OF_YEAR}
	 * @return the length in milliseconds
	 */
	public final long getMilliseconds(Long milliSeconds, int field) {
		Calendar instance = new GregorianCalendar();
		instance.setTimeInMillis(milliSeconds);
		int actualMaximum = instance.getActualMaximum(field);
		long total = 86_400_000L * actualMaximum;
		return total;
	}

	// until 2026-10-07 a CcpExpurgableOptionNotFound lived here, for a format that matched no granularity; nothing looks
	// an option up by its format, so it was never thrown and was removed
}
