package com.ccp.especifications.db.utils.decorators.engine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ccp.decorators.CcpTimeDecorator;

public enum CcpEntityExpurgableOptions{
	yearly(Calendar.YEAR, "yyyy", 86400, 31_557_600_000L, "years")
	,minute(Calendar.MINUTE, "ddMMyyyy HH:mm", 60, 60_000, "minutes")
	,second(Calendar.SECOND, "ddMMyyyy HH:mm:ss", 1, 1_000, "seconds")
	,monthly(Calendar.MONTH, "yyyyMM", 86400, 2_629_800_000L, "months")
	,daily(Calendar.DAY_OF_MONTH, "ddMMyyyy", 86400, 86_400_000, "days")
	,hourly(Calendar.HOUR_OF_DAY, "ddMMyyyy HH", 3600, 3_600_000, "hours")
	,millisecond(Calendar.MILLISECOND, "dd/MM/yyyy HH:mm:ss.SSS", 1, 1, "milliseconds")
	;
	private final int calendarField;
	public final long milliseconds;
	public final int cacheExpires;
	public final String format;
	public final String word;
	
	
	private CcpEntityExpurgableOptions(int calendarField, String format, int cacheExpires, long milliseconds, String word) {
		this.calendarField = calendarField;
		this.cacheExpires = cacheExpires;
		this.milliseconds = milliseconds;
		this.format = format;
		this.word = word;
	}

	public String getFormattedDate(Long date) {
		Date d = new Date();
		d.setTime(date);
		String format = new SimpleDateFormat(this.format).format(d);
		return format;
	}
	
	public Long getNextTimeStamp() {
		Calendar cal = new CcpTimeDecorator().getBrazilianCalendar();
		cal.add(this.calendarField, 1);
		long timeInMillis = cal.getTimeInMillis();
		return timeInMillis;
	}

	public Long getNextTimeStamp(Long timestamp) {
		Calendar cal = new CcpTimeDecorator(timestamp).getBrazilianCalendar();
		cal.add(this.calendarField, 1);
		long timeInMillis = cal.getTimeInMillis();
		return timeInMillis;
	}
	
	public String getNextDate() {
		Long nextTimeStamp = this.getNextTimeStamp();
		CcpTimeDecorator ctd = new CcpTimeDecorator(nextTimeStamp);
		String formattedDateTime = ctd.getFormattedDateTime("dd/MM/yyyy HH:mm:ss.SSS");
		return formattedDateTime;
	}
	public String getNextDate(Long timestamp) {
		Long nextTimeStamp = this.getNextTimeStamp(timestamp);
		CcpTimeDecorator ctd = new CcpTimeDecorator(nextTimeStamp);
		String formattedDateTime = ctd.getFormattedDateTime("dd/MM/yyyy HH:mm:ss.SSS");
		return formattedDateTime;
	}
} 
