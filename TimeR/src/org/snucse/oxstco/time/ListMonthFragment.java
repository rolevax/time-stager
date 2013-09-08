package org.snucse.oxstco.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.snucse.oxstco.time.business.Time;



public class ListMonthFragment extends ListGenericFragment {
	public ListMonthFragment() {
		super();
		super.type = ListGenericFragment.MONTH ;
		super.pageStep = Calendar.MONTH ;
	}

	@Override
	protected boolean filterTime(Time t) {
		tempCalendar.setTime(t.datetime);
		int timeMonth = tempCalendar.get(Calendar.MONTH);
		int timeYear = tempCalendar.get(Calendar.YEAR);
		
		int currentPageMonth = getPageCalendar().get(Calendar.MONTH);
		int currentPageYear = getPageCalendar().get(Calendar.YEAR);
		
		return (timeMonth == currentPageMonth) && 
				(timeYear == currentPageYear) ;
	}
	
	
}
