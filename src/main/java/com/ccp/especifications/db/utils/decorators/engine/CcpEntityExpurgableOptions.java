package com.ccp.especifications.db.utils.decorators.engine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ccp.decorators.CcpTimeDecorator;

public enum CcpEntityExpurgableOptions{
	yearly(Calendar.YEAR, "yyyy", 86400)
	,monthly(Calendar.MONTH, "yyyyMM", 86400)
	,minute(Calendar.MINUTE, "ddMMyyyy HH:mm", 60)
	,daily(Calendar.DAY_OF_MONTH, "ddMMyyyy", 86400)
	,second(Calendar.SECOND, "ddMMyyyy HH:mm:ss", 1)
	,hourly(Calendar.HOUR_OF_DAY, "ddMMyyyy HH", 3600)
	,millisecond(Calendar.MILLISECOND, "dd/MM/yyyy HH:mm:ss.SSS", 1)
	;
	private final int calendarField;
	public final int cacheExpires;
	public final String format;
	
	private CcpEntityExpurgableOptions(int calendarField, String format, int cacheExpires) {
		this.calendarField = calendarField;
		this.cacheExpires = cacheExpires;
		this.format = format;
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
