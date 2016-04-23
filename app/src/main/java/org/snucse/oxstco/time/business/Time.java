package org.snucse.oxstco.time.business;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.snucse.oxstco.time.ListGenericFragment;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class Time implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id, type;
	public String subject;
	public Timestamp datetime;

	private static SimpleDateFormat hourFormat = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SimpleDateFormat dayFormat =
			new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat monthFormat=
			new SimpleDateFormat("yyyy-MM");
	private static SimpleDateFormat weekFormat=
			new SimpleDateFormat("yyyy-ww周");
	private static SimpleDateFormat yearFormat=
			new SimpleDateFormat("yyyy");
	
	static { 
		hourFormat.setCalendar(ListGenericFragment.getTempCalendar());
		dayFormat.setCalendar(ListGenericFragment.getTempCalendar());
		weekFormat.setCalendar(ListGenericFragment.getTempCalendar());
		monthFormat.setCalendar(ListGenericFragment.getTempCalendar());
		yearFormat.setCalendar(ListGenericFragment.getTempCalendar());
	}
	
	public Time() {
		this.datetime = new Timestamp(System.currentTimeMillis());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Time) {
			return ((Time) o).id == this.id;
		}
		return false;
	}

	public Time(int type, String subject, Timestamp datetime) {
		super();
		this.type = type;
		this.subject = subject;
		this.datetime = datetime;
	}

	public static SimpleDateFormat getFormatByType(int type, boolean forTitle) {
		switch (type) {
		case ListGenericFragment.TODAY:
			if (forTitle) {
				return dayFormat ;
			} else {
				return hourFormat;
			}
		case ListGenericFragment.WEEK:
			return weekFormat;
		case ListGenericFragment.MONTH:
			return monthFormat;
		case ListGenericFragment.YEAR:
			return yearFormat;
		default:
			return null;
		}
	}
}
