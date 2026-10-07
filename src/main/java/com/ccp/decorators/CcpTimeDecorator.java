package com.ccp.decorators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Decorator over a timestamp in milliseconds ({@code Long}) offering calendar operations, date/time formatting,
 * midnight computation and thread sleeping. Midnight is computed in the Brazilian time zone (America/Sao_Paulo); year
 * and formatting use the JVM default time zone.
 */
public class CcpTimeDecorator implements CcpDecorator<Long> {

	/** The wrapped timestamp, in milliseconds since the epoch. */
	public final Long content;

	/**
	 * Wraps the given timestamp.
	 * @param time the timestamp in milliseconds
	 */
	public CcpTimeDecorator(Long time) {
		this.content = time;
	}

	/** Wraps the current time ({@code System.currentTimeMillis()}). */
	public CcpTimeDecorator() {
		this(System.currentTimeMillis());
	}

	/**
	 * Computes how many whole seconds separate the wrapped timestamp from the midnight of its own day in the
	 * America/Sao_Paulo time zone (see {@link #getMidnight()}).
	 * @return the elapsed seconds
	 */
	public long getSecondsEnlapsedSinceMidnight() {
		Long midnight = this.getMidnight();
		Long millisSinceMidnight = this.content - midnight;
		long secondsSinceMidnight = (millisSinceMidnight) / 1000L;
		return secondsSinceMidnight;
	}

	/**
	 * Returns the year of the wrapped timestamp, in the JVM default time zone.
	 * @return the year
	 */
	public int getYear() {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(this.content);
		int year = instance.get(Calendar.YEAR);
		return year;
	}

	/**
	 * Returns the timestamp of the midnight of the day of the wrapped timestamp in the America/Sao_Paulo time zone.
	 * @return the midnight timestamp in milliseconds
	 */
	public Long getMidnight() {
		Calendar cal = this.getBrazilianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long timeInMillis = cal.getTimeInMillis();
		return timeInMillis;

	}

	/**
	 * Formats the wrapped timestamp with the given {@code SimpleDateFormat} pattern, in the JVM default time zone.
	 * @param pattern the {@code SimpleDateFormat} pattern
	 * @return the formatted date/time
	 */
	public String getFormattedDateTime(String pattern) {
		Date d = new Date();
		d.setTime(this.content);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String format = sdf.format(d);
		return format;
	}

	/**
	 * Returns a new {@code Calendar} set to the wrapped timestamp in the {@code America/Sao_Paulo} time zone.
	 * @return the calendar
	 */
	public Calendar getBrazilianCalendar() {
		TimeZone timeZone = TimeZone.getTimeZone("America/Sao_Paulo");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(this.content);
		return calendar;
	}

	/**
	 * Pauses the current thread.
	 * @param i the pause in milliseconds
	 * @return {@code true} when the pause completed; {@code false} when {@code i <= 0} or the thread was interrupted
	 */
	public boolean sleep(int i) {
		boolean noMoreDays = i <= 0;

		if (noMoreDays) {
			return false;
		}

		try {
			Thread.sleep(i);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Returns the wrapped timestamp.
	 * @return the timestamp in milliseconds
	 */
	public Long getContent() {
		return this.content;
	}
}
