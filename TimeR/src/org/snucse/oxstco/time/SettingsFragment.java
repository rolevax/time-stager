package org.snucse.oxstco.time;

import java.util.Calendar;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("pref_firstDayOfWeek")) {
			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			String firstDay = sharedPref.getString(key,
					"fuck");
			int iDay = Integer.parseInt(firstDay);
			
			MainActivity.getPageCalendar().setFirstDayOfWeek(iDay);
			//更改每周第一天后，需动一下日期以更新当前周数。
			MainActivity.getPageCalendar().roll(Calendar.DAY_OF_MONTH, true);
			MainActivity.getPageCalendar().roll(Calendar.DAY_OF_MONTH, false);
			ListGenericFragment.tempCalendar.setFirstDayOfWeek(iDay);
			ListGenericFragment.tempCalendar.roll(Calendar.DAY_OF_MONTH, true);
			ListGenericFragment.tempCalendar.roll(Calendar.DAY_OF_MONTH, false);
			
			MainActivity.activity.getCurrentFragment().updateListFromTimes();
		}
	}
}
