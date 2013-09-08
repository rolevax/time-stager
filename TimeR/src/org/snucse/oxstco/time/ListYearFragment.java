package org.snucse.oxstco.time;

import java.util.Calendar;

import org.snucse.oxstco.time.business.Time;



public class ListYearFragment extends ListGenericFragment {
	public ListYearFragment() {
		super();
		super.type = ListGenericFragment.YEAR ;
		super.pageStep = Calendar.YEAR ;
	}

	@Override
	protected boolean filterTime(Time t) {
		tempCalendar.setTime(t.datetime);
		int timeYear = tempCalendar.get(pageStep);
		
		int currentPageYear = getPageCalendar().get(Calendar.YEAR);
		return timeYear == currentPageYear ;
	}
}
