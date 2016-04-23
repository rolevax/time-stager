package org.snucse.oxstco.time;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MenuActivity extends Activity implements OnClickListener {

	private Button deleteButton, editButton, btn_cancel;
	private LinearLayout layout;

	public static final int RESULT_DELETE = 41, RESULT_EDIT = 42,
			REQUEST_EDIT = 43;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		deleteButton = (Button) this.findViewById(R.id.button_delete);
		editButton = (Button) this.findViewById(R.id.button_edit);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

		layout = (LinearLayout) findViewById(R.id.pop_layout);

		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});
		// 添加按钮监听
		btn_cancel.setOnClickListener(this);
		editButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_delete:
			this.delete();
			break;
		case R.id.button_edit:
			this.edit();
			break;
		case R.id.btn_cancel:
			break;
		default:
			break;
		}
		finish();
	}

	private void delete() {
		Intent intent = this.getIntent();
		this.setResult(RESULT_DELETE, intent);
	}

	private void edit() {
		Intent intent = this.getIntent();
		this.setResult(RESULT_EDIT, intent);
	}

}