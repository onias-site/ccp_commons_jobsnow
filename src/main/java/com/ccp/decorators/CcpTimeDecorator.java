package com.ccp.decorators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CcpTimeDecorator implements CcpDecorator<Long> {
	
	public final Long content;
	
	public CcpTimeDecorator(Long time) {
		this.content = time;
	}
	public CcpTimeDecorator() {
		this(System.currentTimeMillis());
	}

	public long getSecondsEnlapsedSinceMidnight() {
		Long meiaNoite = this.getMidnight();
		long tempo = (this.content - meiaNoite) / 1000L;
		return tempo;
	}
	
	public int getYear() {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(this.content);
		int year = instance.get(Calendar.YEAR);
		return year;
	}
	
	public Long getMidnight() {
		Calendar cal = this.getBrazilianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long timeInMillis = cal.getTimeInMillis();
		return timeInMillis;
		
	}
	

	public String getFormattedDateTime(String pattern) {
		Date d = new Date();
		d.setTime(this.content);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String format = sdf.format(d);
		return format;
	}


	public Calendar getBrazilianCalendar() {
		TimeZone timeZone = TimeZone.getTimeZone("America/Sao_Paulo");
		Calendar cal = Calendar.getInstance(timeZone);
		return (Calendar)cal.clone();
	}
	
	public boolean sleep(int i) {

		if (i <= 0) {
			return false;
		}

		try {
			Thread.sleep(i);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Long getContent() {
		return this.content;
	}
}
