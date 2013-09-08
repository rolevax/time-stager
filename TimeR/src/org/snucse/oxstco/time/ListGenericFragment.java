package org.snucse.oxstco.time;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snucse.oxstco.time.business.DBMgr;
import org.snucse.oxstco.time.business.Time;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat") //国际化时应解决此问题
public abstract class ListGenericFragment extends ListFragment {

	protected static Calendar tempCalendar = null;
	public static final int TODAY = 1, WEEK = 2, MONTH = 3, YEAR = 4;
	private TextView pageTextView;
	// 屏幕列表中显示项的列表。
	public List<Map<String, Object>> data;
	protected SimpleDateFormat listTextFormat;
	protected SimpleDateFormat pageTextFormat;
	private boolean seeAll = false;

	// 保存属于该类别fragment的所有Time对象的列表
	public List<Time> times;

	protected int type, pageStep;

	public ListGenericFragment() {
		super();
	}

	public void add(Time t) {
		DBMgr dbm = DBMgr.getInstance(getActivity());
		dbm.add(t);

		this.updateListFromDatabase();
	}

	public void delete(Time t) {
		DBMgr dbm = DBMgr.getInstance(getActivity());
		dbm.delete(t);

		this.updateListFromDatabase();
		this.updatePageText();
	}

	protected abstract boolean filterTime(Time t);

	protected Calendar getPageCalendar() {
		return ((MainActivity) getActivity()).getPageCalendar();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (tempCalendar == null) {
			tempCalendar = Calendar.getInstance();
			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			String firstDay = sharedPref.getString("pref_firstDayOfWeek",
					"fuck");
			tempCalendar.setFirstDayOfWeek(Integer.parseInt(firstDay));
		}

		this.pageTextFormat = new SimpleDateFormat(Time.getFormatByType(
				this.type, true));
		this.listTextFormat = new SimpleDateFormat(Time.getFormatByType(
				this.type, false));

		this.updateTimesFromDatabase();
		this.updateDataFromTimes();
		SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), data,
				R.layout.vlist, new String[] { "text_subject", "img_type" },
				new int[] { R.id.text_subject, R.id.img_type });
		setListAdapter(simpleAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_week, container, false);
		return v;
	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), MenuActivity.class);
		intent.putExtra("time", (Serializable) data.get(position).get("time"));
		intent.putExtra("type", type);
		getActivity().startActivityForResult(intent, 0);
	}

	protected void onMovingPage(int direction) {
		this.seeAll = false;
		this.getPageCalendar().add(this.pageStep, direction);
		this.updateListFromTimes();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		pageTextView = (TextView) getView().findViewById(R.id.text_current);
		if (pageTextView == null) {
			Log.i("mlf", "一个时有时无的错误：current为空引用");
		}
		this.updateListFromTimes();
	}

	public void seeAll() {
		this.seeAll = true;
		this.updateListFromTimes();
	}

	public void update(Time t) {
		DBMgr dbm = DBMgr.getInstance(getActivity());
		dbm.update(t);

		this.updateListFromDatabase();
	}

	/**
	 * 从数据库中取出列表三部曲之第二步，</br>
	 * 将Times列表中符合当前页的项目放到待显示的data列表中。
	 */
	private void updateDataFromTimes() {
		if (this.data == null) {
			this.data = new ArrayList<Map<String, Object>>();
		}
		this.data.clear();
		Map<String, Object> map;
		for (Time t : times) {
			if (this.seeAll || this.filterTime(t)) {
				map = new HashMap<String, Object>();
				map.put("text_subject",
						t.subject + "\n" + listTextFormat.format(t.datetime));
				map.put("img_type", R.drawable.ic_launcher);
				map.put("time", t);
				this.data.add(map);
			}
		}
	}

	/**
	 * 从数据库中取出列表三部曲之第三步，</br>
	 * 提醒data列表已被更改，使其刷新,</br>
	 * 并更新指示框中的显示时间。
	 */
	private void updateListFromData() {
		// 只有在data仍指向原来的对象时才起作用
		((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
		this.updatePageText();
	}

	/**
	 * 从数据库中取出列表三部曲三合一版，</br>
	 * 从数据库从拿数据，并刷新当前页列表。
	 */
	protected void updateListFromDatabase() {
		this.updateTimesFromDatabase();
		this.updateDataFromTimes();
		this.updateListFromData();
	}
	
	/**
	 * 通过重新过滤times列表，改变当前页显示内容。</br>
	 * 翻页、切换标签，或配置变动时调用。</br>
	 * 要不是当Fragment显示出来是并不会</br>
	 * 调用onResume方法，才不用这么费劲地</br>
	 * 把这个方法可哪儿都调一下呢。
	 */
	public void updateListFromTimes() {
		this.updateDataFromTimes();
		this.updateListFromData();
	}

	private void updatePageText() {
		if (this.seeAll) {
			this.pageTextView.setText("All");
			return;
		}

		this.pageTextView
				.setText(pageTextFormat.format(getPageCalendar().getTime()));
	}

	/**
	 * 从数据库中获取列表三部曲中第一步，</br>
	 * 将数据库中属于该type的time对象取出， 保存在times列表中。
	 */
	private void updateTimesFromDatabase() {
		DBMgr dbm = DBMgr.getInstance(getActivity());
		this.times = dbm.getListByType(this.type);
	}
}