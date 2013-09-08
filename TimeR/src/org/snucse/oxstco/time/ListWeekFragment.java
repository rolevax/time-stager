package org.snucse.oxstco.time;

import java.util.Calendar;

import org.snucse.oxstco.time.business.Time;


public class ListWeekFragment extends ListGenericFragment {
	public ListWeekFragment() {
		super();
		super.type = ListGenericFragment.WEEK;
		super.pageStep = Calendar.WEEK_OF_YEAR ;
	}
	
	@Override
	protected boolean filterTime(Time t) {
		tempCalendar.setTime(t.datetime);
		int timeWeek = tempCalendar.get(Calendar.WEEK_OF_YEAR);
		int timeYear = tempCalendar.get(Calendar.YEAR);
		
		int currWeek = getPageCalendar().get(Calendar.WEEK_OF_YEAR);
		int currYear = getPageCalendar().get(Calendar.YEAR);
		
		return (timeWeek==currWeek) && (timeYear==currYear);
	}

}
