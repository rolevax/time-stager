package org.snucse.oxstco.time.business;

import java.io.Serializable;
import java.sql.Timestamp;

import org.snucse.oxstco.time.ListGenericFragment;

import android.util.Log;

public class Time implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id, type;
	public String subject;
	public Timestamp datetime;

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

	public static String getFormatByType(int type, boolean forTitle) {
		String formatString = null;
		switch (type) {
		case ListGenericFragment.TODAY:
			if (forTitle) {
				formatString = "yyyy-MM-dd";
			} else {
				formatString = "yyyy-MM-dd HH:mm";
			}
			break;
		case ListGenericFragment.WEEK:
			formatString = "yyyy-ww";
			break;
		case ListGenericFragment.MONTH:
			formatString = "yyyy-MM";
			break;
		case ListGenericFragment.YEAR:
			formatString = "yyyy";
			break;
		default:
			Log.w("time", "我草");
			break;
		}
		return formatString;
	}
}
