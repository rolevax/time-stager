package org.snucse.oxstco.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.snucse.oxstco.time.business.Time;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class AddActivity extends Activity implements OnTimeChangedListener,
		OnDateChangedListener, OnItemSelectedListener, OnValueChangeListener {
	public static final int RESULT_ADD = 122, RESULT_EDIT = 123;

	private View todayFrag;
	private View weekFrag;
	private View monthFrag;
	private View yearFrag;
	private Spinner typeSpinner;
	private EditText addDatetime;
	private EditText addSubject;

	private DatePicker datePickerInToday;
	private TimePicker timePickerInToday;
	private NumberPicker yearPickerInWeek;
	private NumberPicker weekPickerInWeek;
	private NumberPicker yearPickerInYear;
	private NumberPicker yearPickerInMonth;
	private NumberPicker monthPickerInMonth;

	private Calendar calendar;

	private Time time;

	private int request = -1;

	private void findViews() {
		this.typeSpinner = (Spinner) findViewById(R.id.add_type);
		this.addDatetime = (EditText) findViewById(R.id.add_datetime);
		this.addSubject = (EditText) findViewById(R.id.add_subject);
		this.datePickerInToday = (DatePicker) findViewById(R.id.add_date);
		this.timePickerInToday = (TimePicker) findViewById(R.id.add_time);
		this.calendar = Calendar.getInstance();
		this.todayFrag = findViewById(R.id.add_today_fragment);
		this.weekFrag = findViewById(R.id.add_week_fragment);
		this.monthFrag = findViewById(R.id.add_month_fragment);
		this.yearFrag = findViewById(R.id.add_year_fragment);
		this.yearPickerInWeek = (NumberPicker) findViewById(R.id.picker_year_in_week);
		this.weekPickerInWeek = (NumberPicker) findViewById(R.id.picker_week_in_week);
		this.yearPickerInYear = (NumberPicker) findViewById(R.id.picker_year_in_year);
		this.yearPickerInMonth = (NumberPicker) findViewById(R.id.picker_year_in_month);
		this.monthPickerInMonth = (NumberPicker) findViewById(R.id.picker_month_in_month);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		this.findViews();

		this.yearPickerInWeek.setMinValue(2013);
		this.yearPickerInWeek.setMaxValue(2093);
		this.yearPickerInYear.setMinValue(2013);
		this.yearPickerInYear.setMaxValue(2093);
		this.yearPickerInMonth.setMinValue(2013);
		this.yearPickerInMonth.setMaxValue(2093);
		this.weekPickerInWeek.setMinValue(1);
		this.weekPickerInWeek.setMaxValue(53);
		this.monthPickerInMonth.setMinValue(1);
		this.monthPickerInMonth.setMaxValue(12);

		this.yearPickerInWeek.setOnValueChangedListener(this);
		this.weekPickerInWeek.setOnValueChangedListener(this);
		this.yearPickerInYear.setOnValueChangedListener(this);
		this.yearPickerInMonth.setOnValueChangedListener(this);
		this.monthPickerInMonth.setOnValueChangedListener(this);

		todayFrag.setVisibility(View.GONE);
		weekFrag.setVisibility(View.GONE);
		monthFrag.setVisibility(View.GONE);
		yearFrag.setVisibility(View.GONE);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.type_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(adapter);
		typeSpinner.setOnItemSelectedListener(this);

		Intent intent = this.getIntent();
		int intType = intent.getIntExtra("type", -1);
		// type从1开始，而下拉菜单中的pos从0开始，因此减1
		this.typeSpinner.setSelection(intType - 1);

		this.request = intent.getIntExtra("requestCode", -1);
		if (this.request == MenuActivity.REQUEST_EDIT) {
			time = (Time) intent.getSerializableExtra("time");
			this.addSubject.setText(time.subject);
		} else if (this.request == MainActivity.REQUEST_ADD) {
			time = new Time();
			time.type = intType;
		}

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String firstDay = sharedPref.getString("pref_firstDayOfWeek", "fuck");
		calendar.setFirstDayOfWeek(Integer.parseInt(firstDay));
		calendar.setTime(this.time.datetime);

		this.datePickerInToday.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
		this.timePickerInToday.setOnTimeChangedListener(this);

		this.updateDatetimeText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}

	public void ok(View v) {
		time.subject = addSubject.getText().toString();
		time.datetime.setTime(calendar.getTimeInMillis());
		Intent data = new Intent();
		data.putExtra("time", this.time);
		switch (this.request) {
		case MainActivity.REQUEST_ADD:
			this.setResult(AddActivity.RESULT_ADD, data);
			break;
		case MenuActivity.REQUEST_EDIT:
			this.setResult(AddActivity.RESULT_EDIT, data);
			break;
		}
		this.finish();
	}

	@Override
	public void onTimeChanged(TimePicker arg0, int hour, int minute) {
		this.calendar.set(Calendar.HOUR_OF_DAY, hour);
		this.calendar.set(Calendar.MINUTE, minute);
		this.updateDatetimeText();
	}

	@Override
	public void onDateChanged(DatePicker arg0, int year, int month, int day) {
		this.calendar.set(year, month, day);
		this.updateDatetimeText();
	}
	
	private void updateDatetimeText() {
		String formatString = Time.getFormatByType(this.time.type, false);
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		// 为避免琐碎重复，time对象中的timestamp在结束时统一更新，所以这里使用calendar中的时间。
		this.addDatetime.setText(format.format(this.calendar.getTime()));
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
		// 不碰菜单，也会触发该方法。
		// type是从1开始的。
		this.time.type = pos + 1;
		this.setForegroundByType();
		/*
		 * 每次修改下拉菜单项，即时更新time对象中的type， 因此结束时不进行更新time的type属性操作。
		 */
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void setForegroundByType() {
		switch (this.time.type) {
		case ListGenericFragment.TODAY:
			todayFrag.setVisibility(View.VISIBLE);
			weekFrag.setVisibility(View.GONE);
			monthFrag.setVisibility(View.GONE);
			yearFrag.setVisibility(View.GONE);
			this.datePickerInToday.updateDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			this.timePickerInToday.setCurrentHour(calendar
					.get(Calendar.HOUR_OF_DAY));
			this.timePickerInToday.setCurrentMinute(calendar
					.get(Calendar.MINUTE));
			break;
		case ListGenericFragment.WEEK:
			todayFrag.setVisibility(View.GONE);
			weekFrag.setVisibility(View.VISIBLE);
			monthFrag.setVisibility(View.GONE);
			yearFrag.setVisibility(View.GONE);
			this.yearPickerInWeek.setValue(this.calendar.get(Calendar.YEAR));
			this.weekPickerInWeek.setValue(this.calendar
					.get(Calendar.WEEK_OF_YEAR));
			break;
		case ListGenericFragment.MONTH:
			todayFrag.setVisibility(View.GONE);
			weekFrag.setVisibility(View.GONE);
			monthFrag.setVisibility(View.VISIBLE);
			yearFrag.setVisibility(View.GONE);
			this.yearPickerInMonth.setValue(this.calendar.get(Calendar.YEAR));
			this.monthPickerInMonth
					.setValue(this.calendar.get(Calendar.MONTH) + 1);
			break;
		case ListGenericFragment.YEAR:
			todayFrag.setVisibility(View.GONE);
			weekFrag.setVisibility(View.GONE);
			monthFrag.setVisibility(View.GONE);
			yearFrag.setVisibility(View.VISIBLE);
			this.yearPickerInYear.setValue(this.calendar.get(Calendar.YEAR));
			break;
		}
		this.updateDatetimeText();
	}

	@Override
	public void onValueChange(NumberPicker n, int oldVal, int newVal) {
		int id = n.getId();
		switch (id) {
		case R.id.picker_year_in_week:
		case R.id.picker_year_in_year:
		case R.id.picker_year_in_month:
			this.calendar.set(Calendar.YEAR, newVal);
			break;
		case R.id.picker_week_in_week:
			this.calendar.set(Calendar.WEEK_OF_YEAR, newVal);
			Log.i("add","spinner new value="+newVal);
			Log.i("add","cal new week="+this.calendar.get(Calendar.WEEK_OF_YEAR));
			break;
		case R.id.picker_month_in_month:
			this.calendar.set(Calendar.MONTH, newVal - 1);
			break;
		}
		this.updateDatetimeText();
	}

}
