package org.snucse.oxstco.time;

import java.util.Calendar;

import org.snucse.oxstco.time.business.Time;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class MainActivity extends FragmentActivity {
	private TabHost tabHost;
	public static final int REQUEST_ADD = 422;
	private static Calendar pageCalendar = null;

	/**
	 * 在onCreate方法中被初始化为当前对象， </br>
	 * 使得其他类中可直接通过该静态引用获得该MainActivity。
	 */
	public static MainActivity activity;

	public static Calendar getPageCalendar() {
		return pageCalendar;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MainActivity.activity = this;

		// set calendars
		if (pageCalendar == null) {
			pageCalendar = Calendar.getInstance();
			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(this);
			//String firstDay = sharedPref.getString("pref_firstDayOfWeek", "fuck");
			String firstDay = sharedPref.getString("pref_firstDayOfWeek", "2");
			pageCalendar.setFirstDayOfWeek(Integer.parseInt(firstDay));
			
			// 改动每周第一天后，变动一下当前时间以刷新当前周数。
			pageCalendar.roll(Calendar.DAY_OF_MONTH, true);
			pageCalendar.roll(Calendar.DAY_OF_MONTH, false);
		}

		setContentView(R.layout.activity_main);

		// set tabs
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
			this.dialog(R.id.action_about);
			return true;
		case R.id.action_next:
			this.dialog(R.id.action_next);
			return true;
		case R.id.action_settings:
			Intent i = new Intent(this, SettingsActivity.class);
			this.startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void dialog(int id) {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		Resources r = this.getResources();
		switch (id) {
		case R.id.action_about:
			builder.setMessage(r.getString(R.string.about_content));
			builder.setTitle(r.getString(R.string.about_title));
			break;
		case R.id.action_next:
			builder.setMessage(r.getString(R.string.next_content));
			builder.setTitle(r.getString(R.string.next_title));
			break;
		}
		builder.setPositiveButton(r.getString(R.string.button_confirm),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
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
		pageCalendar.setTimeInMillis(System.currentTimeMillis());
		this.getCurrentFragment().onMovingPage(0);
	}

	private boolean seeAllBtnDown = false ;
	public void all(View v) {
		Button b = (Button)v ;
		if (!seeAllBtnDown) {
			this.getCurrentFragment().seeAll();
			b.setText(R.string.button_return) ;
		} else {
			// if pressed again, return to now
			// (a better choice is just return to previous)
			this.returnToNow(v) ;
			b.setText(R.string.button_all) ;
		}
		this.seeAllBtnDown = !this.seeAllBtnDown ;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("main act", "on res act, code=" + resultCode);
		ListGenericFragment mlf;
		Time time;
		switch (resultCode) {
		case MenuActivity.RESULT_DELETE:
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