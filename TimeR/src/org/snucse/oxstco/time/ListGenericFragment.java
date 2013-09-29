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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
// 国际化时应解决此问题
public abstract class ListGenericFragment extends ListFragment {

	protected static Calendar tempCalendar = null;
	protected static final int MENU_EDIT = 401, MENU_DELETE = 402,
			REQUEST_EDIT = 403, MENU_TO_DAY = 404,
			MENU_TO_WEEK = 405, MENU_TO_MONTH = 406,
			MENU_TO_YEAR = 407 ;
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

	private void delete(int position) {
		Time toDel = (Time) data.get(position).get("time");
		this.delete(toDel);
	}

	protected abstract boolean filterTime(Time t);

	protected static Calendar getPageCalendar() {
		return MainActivity.getPageCalendar();
	}

	public static Calendar getTempCalendar() {
		return ListGenericFragment.tempCalendar;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//menu.add(0, MENU_EDIT, 2, "Edit");
		menu.add(0, MENU_DELETE, 0, "完成／撤销");
		menu.add(1, MENU_EDIT, 1, "提前／推迟");
		if (this.type > ListGenericFragment.TODAY) {
			menu.add(1, MENU_TO_DAY, 5, "安排到某日");
		}
		if (this.type > ListGenericFragment.WEEK) {
			menu.add(1, MENU_TO_WEEK, 4, "安排到某周");
		}
		if (this.type > ListGenericFragment.MONTH) {
			menu.add(1, MENU_TO_MONTH, 3, "安排到某月");
		}
		if (this.type > ListGenericFragment.YEAR) {
			menu.add(1, MENU_TO_YEAR, 2, "安排到某年");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		/*
		 * 这个万恶的方法，并不真正属于任何Fragment， 而是属于Activity的。这个方法根本不关心是哪个
		 * Fragment在调用它，因为他只为Activity服务 至于Fragment里的this指针究竟指谁，它根本不在乎
		 * 因此，只能用这个realThis指针去代替this发挥作用了。
		 */
		ListGenericFragment realThis = MainActivity.activity
				.getCurrentFragment();
		Intent intent = new Intent(MainActivity.activity, AddActivity.class);
		
		//一下有待优化，太恶心了。
		switch (item.getItemId()) {
		case ListGenericFragment.MENU_DELETE:
			realThis.delete(info.position);
			return true;
		case ListGenericFragment.MENU_TO_DAY:
			intent.putExtra("time",
					(Serializable) realThis.data.get(info.position).get("time"));
			intent.putExtra("type", ListGenericFragment.TODAY);
			MainActivity.activity.startActivityForResult(intent, REQUEST_EDIT);
			return true;
		case ListGenericFragment.MENU_TO_WEEK:
			intent.putExtra("time",
					(Serializable) realThis.data.get(info.position).get("time"));
			intent.putExtra("type", ListGenericFragment.WEEK);
			MainActivity.activity.startActivityForResult(intent, REQUEST_EDIT);
			return true;
		case ListGenericFragment.MENU_TO_MONTH:
			intent.putExtra("time",
					(Serializable) realThis.data.get(info.position).get("time"));
			intent.putExtra("type", ListGenericFragment.MONTH);
			MainActivity.activity.startActivityForResult(intent, REQUEST_EDIT);
			return true;
		case ListGenericFragment.MENU_TO_YEAR:
			intent.putExtra("time",
					(Serializable) realThis.data.get(info.position).get("time"));
			intent.putExtra("type", ListGenericFragment.YEAR);
			MainActivity.activity.startActivityForResult(intent, REQUEST_EDIT);
			return true;
		case ListGenericFragment.MENU_EDIT:
			intent.putExtra("time",
					(Serializable) realThis.data.get(info.position).get("time"));
			intent.putExtra("type", realThis.type);
			MainActivity.activity.startActivityForResult(intent, REQUEST_EDIT);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
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

		this.pageTextFormat = Time.getFormatByType(this.type, true);
		this.listTextFormat = Time.getFormatByType(this.type, false);

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

	/*
	 * 暂时只支持中文，以后实现多语言，并修改所有的hard-code.
	 * 下一个目标：设置picker界限，不能安排到过去。
	 * 
	 *  远期目标：Time对象可具有deadline属性，自带提醒，制约推迟。
	 *  (详情参考最初设计笔记)
	 *  添加对过期未完成项目的一系列处理(可设置自动删除，可查看)
	 */

	public void onListItemClick(ListView parent, View v, int position, long id) {
		this.getActivity().openContextMenu(v);
	}

	protected void onMovingPage(int direction) {
		this.seeAll = false;
		ListGenericFragment.getPageCalendar().add(this.pageStep, direction);
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
		this.registerForContextMenu(this.getListView());
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
	 * 从数据库中取出列表三部曲之第二步，</br> 将Times列表中符合当前页的项目放到待显示的data列表中。
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
	 * 从数据库中取出列表三部曲之第三步，</br> 提醒data列表已被更改，使其刷新,</br> 并更新指示框中的显示时间。
	 */
	private void updateListFromData() {
		// 只有在data仍指向原来的对象时才起作用
		((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
		this.updatePageText();
	}

	/**
	 * 从数据库中取出列表三部曲三合一版，</br> 从数据库从拿数据，并刷新当前页列表。
	 */
	protected void updateListFromDatabase() {
		this.updateTimesFromDatabase();
		this.updateDataFromTimes();
		this.updateListFromData();
	}

	/**
	 * 通过重新过滤times列表，改变当前页显示内容。</br> 翻页、切换标签，或配置变动时调用。</br>
	 * 要不是当Fragment显示出来是并不会</br> 调用onResume方法，才不用这么费劲地</br> 把这个方法可哪儿都调一下呢。
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

		String pageTextString = pageTextFormat.format(getPageCalendar()
				.getTime());
		this.pageTextView.setText(pageTextString);
	}

	/**
	 * 从数据库中获取列表三部曲中第一步，</br> 将数据库中属于该type的time对象取出， 保存在times列表中。
	 */
	private void updateTimesFromDatabase() {
		DBMgr dbm = DBMgr.getInstance(getActivity());
		this.times = dbm.getListByType(this.type);
	}
}