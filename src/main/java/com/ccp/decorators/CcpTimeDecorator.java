package com.ccp.decorators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Decorator sobre um timestamp em milissegundos ({@code Long}) que oferece operações de calendário com
 * fuso horário brasileiro (America/Sao_Paulo), formatação de data/hora, cálculo de meia-noite e controle
 * de pausa de thread.
 */
public class CcpTimeDecorator implements CcpDecorator<Long> {

	public final Long content;

	/**
	 * Encapsula o timestamp fornecido.
	 */
	public CcpTimeDecorator(Long time) {
		this.content = time;
	}

	/**
	 * Usa {@code System.currentTimeMillis()} como timestamp atual.
	 */
	public CcpTimeDecorator() {
		this(System.currentTimeMillis());
	}

	/**
	 * Calcula quantos segundos se passaram desde a meia-noite do dia atual (fuso de São Paulo).
	 */
	public long getSecondsEnlapsedSinceMidnight() {
		Long meiaNoite = this.getMidnight();
		long tempo = (this.content - meiaNoite) / 1000L;
		return tempo;
	}

	/**
	 * Retorna o ano do timestamp.
	 */
	public int getYear() {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(this.content);
		int year = instance.get(Calendar.YEAR);
		return year;
	}

	/**
	 * Retorna o timestamp da meia-noite do dia atual no fuso de São Paulo.
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
	 * Formata o timestamp usando o padrão fornecido ({@code SimpleDateFormat}).
	 */
	public String getFormattedDateTime(String pattern) {
		Date d = new Date();
		d.setTime(this.content);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String format = sdf.format(d);
		return format;
	}

	/**
	 * Retorna um {@code Calendar} configurado com o fuso horário {@code America/Sao_Paulo}.
	 */
	public Calendar getBrazilianCalendar() {
		TimeZone timeZone = TimeZone.getTimeZone("America/Sao_Paulo");
		Calendar cal = Calendar.getInstance(timeZone);
		return (Calendar)cal.clone();
	}

	/**
	 * Pausa a thread por {@code i} milissegundos. Retorna {@code true} se a pausa foi concluída;
	 * {@code false} se o valor for &lt;= 0 ou se a thread for interrompida.
	 */
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

	/**
	 * Implementação de {@code CcpDecorator}; retorna o timestamp.
	 */
	public Long getContent() {
		return this.content;
	}
}
