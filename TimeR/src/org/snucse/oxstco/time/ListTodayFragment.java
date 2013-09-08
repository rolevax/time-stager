package org.snucse.oxstco.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.snucse.oxstco.time.business.Time;



public class ListTodayFragment extends ListGenericFragment {
	public ListTodayFragment() {
		super();
		super.type = ListGenericFragment.TODAY;
		super.pageStep = Calendar.DAY_OF_MONTH ;
	}
	
	@Override
	protected boolean filterTime(Time t) {
		tempCalendar.setTime(t.datetime);
		int timeMonth = tempCalendar.get(Calendar.MONTH);
		int timeYear = tempCalendar.get(Calendar.YEAR);
		int timeDay = tempCalendar.get(Calendar.DAY_OF_MONTH);
		
		int currentPageMonth = getPageCalendar().get(Calendar.MONTH);
		int currentPageYear = getPageCalendar().get(Calendar.YEAR);
		int currentPageDay = getPageCalendar().get(Calendar.DAY_OF_MONTH);
		
		return (timeMonth == currentPageMonth) && 
				(timeYear == currentPageYear) &&
				(timeDay == currentPageDay);
	}
}
