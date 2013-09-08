package org.snucse.oxstco.time;

import java.lang.reflect.Field;
import java.util.Calendar;

import org.snucse.oxstco.time.business.Time;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class MainActivity extends FragmentActivity {
	private TabHost tabHost;
	public static final int REQUEST_ADD = 422;
	private Calendar pageCalendar ;

	/**
	 * 在onCreate方法中被初始化为当前对象，
	 * 使得其他类中可直接通过该静态引用获得该MainActivity。
	 */
	public static MainActivity activity ;
	
	public Calendar getPageCalendar() {
		return pageCalendar;
	}

	/*
	 * 未来功能： 设置每周第一天
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MainActivity.activity = this ;
		
		this.pageCalendar = Calendar.getInstance();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String firstDay = sharedPref.getString("pref_firstDayOfWeek", "fuck");
		this.pageCalendar.setFirstDayOfWeek(Integer.parseInt(firstDay));
		
		setContentView(R.layout.activity_main);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		tabHost.addTab(tabHost.newTabSpec("today")
				.setIndicator(getString(R.string.tab_today))
				.setContent(R.id.fragment_today));
		tabHost.addTab(tabHost.newTabSpec("week")
				.setIndicator(getString(R.string.tab_week))
				.setContent(R.id.fragment_week));
		tabHost.addTab(tabHost.newTabSpec("month")
				.setIndicator(getString(R.string.tab_month))
				.setContent(R.id.fragment_month));
		tabHost.addTab(tabHost.newTabSpec("year")
				.setIndicator(getString(R.string.tab_year))
				.setContent(R.id.fragment_year));
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				MainActivity.this.getCurrentFragment().updateListFromTimes();
			}
		});
		this.getOverflowMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about:
			return true;
		case R.id.action_settings:
			Intent i = new Intent(this, SettingsActivity.class);
			this.startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void getOverflowMenu() {

	     try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); 
	    }
	}
	
	// on click method of the add button
	public void add(View v) {
		int type = tabHost.getCurrentTab() + 1;
		Intent i = new Intent(this, AddActivity.class);
		i.putExtra("type", type);
		startActivityForResult(i, MainActivity.REQUEST_ADD);
	}

	public void prev(View v) {
		this.getCurrentFragment().onMovingPage(-1);
	}

	public void next(View v) {
		this.getCurrentFragment().onMovingPage(1);
	}

	public void returnToNow(View v) {
		this.pageCalendar.setTimeInMillis(System.currentTimeMillis());
		this.getCurrentFragment().onMovingPage(0);
	}

	public void all(View v) {
		this.getCurrentFragment().seeAll();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("main act", "on res act, code=" + resultCode);
		switch (resultCode) {
		case MenuActivity.RESULT_DELETE:
			Time time = (Time) data.getSerializableExtra("time");
			ListGenericFragment mlf = getCurrentFragment();
			mlf.delete(time);
			break;
		case AddActivity.RESULT_ADD:
			time = (Time) data.getSerializableExtra("time");
			mlf = this.getFragmentByType(time.type);
			mlf.add(time);
			break;
		case MenuActivity.RESULT_EDIT:
			data.setClass(this, AddActivity.class);
			startActivityForResult(data, MenuActivity.REQUEST_EDIT);
			break;
		case AddActivity.RESULT_EDIT:
			time = (Time) data.getSerializableExtra("time");
			mlf = getCurrentFragment();
			mlf.update(time);
			mlf = this.getFragmentByType(time.type);
			mlf.updateListFromDatabase();
			break;
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		intent.putExtra("requestCode", requestCode);
		super.startActivityForResult(intent, requestCode);
	}

	public static int getFragmentIdByType(int type) {
		switch (type) {
		case ListGenericFragment.TODAY:
			return R.id.fragment_today;
		case ListGenericFragment.WEEK:
			return R.id.fragment_week;
		case ListGenericFragment.MONTH:
			return R.id.fragment_month;
		case ListGenericFragment.YEAR:
			return R.id.fragment_year;
		default:
			return -1;
		}
	}

	private ListGenericFragment getFragmentByType(int type) {
		return (ListGenericFragment) this.getSupportFragmentManager()
				.findFragmentById(getFragmentIdByType(type));
	}

	protected ListGenericFragment getCurrentFragment() {
		int type = tabHost.getCurrentTab() + 1;
		return getFragmentByType(type);
	}
}