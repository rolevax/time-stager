package org.snucse.oxstco.time;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;

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
			
			MainActivity.activity.getPageCalendar().setFirstDayOfWeek(iDay);
			ListGenericFragment.tempCalendar.setFirstDayOfWeek(iDay);
			
			/*
			 * 下一个目标：
			 * 1.优化ListGenericFrag中的update系列方法
			 * 2.实现更改本配置后返回时即时更新列表
			 */
		}
	}
}
