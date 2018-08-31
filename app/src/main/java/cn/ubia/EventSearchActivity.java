package cn.ubia;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;

import com.ubia.util.DateUtil;

public class EventSearchActivity extends BaseActivity implements
		OnClickListener {

	private static final int MENU_CONFIRM = 0;
	private Button mBtnEndTime;
	private Button mBtnStartDate;
	private Button mBtnStartTime;
	private Button mBtnEndDate;

	private long startTime;
	private long endTime;
 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.EventSearchActivity_event_search);
		setContentView(R.layout.event_search);
		startTime = getIntent().getLongExtra("startTime", 0);
		endTime = getIntent().getLongExtra("endTime", 0);
		mBtnStartDate = (Button) findViewById(R.id.btnStartDate);
		mBtnStartTime = (Button) findViewById(R.id.btnStartTime);
		mBtnEndDate = (Button) findViewById(R.id.btnEndDate);
		mBtnEndTime = (Button) findViewById(R.id.btnEndTime);
		mBtnStartDate.setOnClickListener(this);
		mBtnEndDate.setOnClickListener(this);
		mBtnStartTime.setOnClickListener(this);
		mBtnEndTime.setOnClickListener(this);
		mBtnStartDate.setText(DateUtil.formatToEventDateStyle(startTime));
		mBtnStartTime.setText(DateUtil.formatToEventTimeStyle(startTime));
		mBtnEndDate.setText(DateUtil.formatToEventDateStyle(endTime));
		mBtnEndTime.setText(DateUtil.formatToEventTimeStyle(endTime));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_CONFIRM, 0, "");
		item.setIcon(R.drawable.ic_mylauncher);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case MENU_CONFIRM:
			doConfirm();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void doConfirm() {
		if (endTime < startTime) {
			getHelper().showMessage(R.string.EventSearchActivity_endtime_must_less_than_starttime);
			return;
		}

//		if (endTime > System.currentTimeMillis()) {
//			getHelper().showMessage(
//					R.string.endtime_must_less_than_current_time);
//			return;
//		}

		Intent intent = new Intent();
		intent.putExtra("startTime", startTime);
		intent.putExtra("endTime", endTime);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnStartDate:
			showDateDialog((Button) v);
			break;
		case R.id.btnEndDate:
			showDateDialog((Button) v);
			break;
		case R.id.btnStartTime:
			showTimeDialog((Button) v);
			break;
		case R.id.btnEndTime:
			showTimeDialog((Button) v);
			break;
		default:
			break;
		}

	}

	private void showTimeDialog(final Button button) {
		int hour, minute;
		Calendar cal = new GregorianCalendar();
		if (button.getId() == R.id.btnStartTime) {
			cal.setTimeInMillis(startTime);
		} else {
			cal.setTimeInMillis(endTime);
		}

		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);

		TimePickerDialog timePicker = new TimePickerDialog(
				EventSearchActivity.this, new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						Calendar cal = new GregorianCalendar();
						cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
						cal.set(Calendar.MINUTE, minute);
						long time = cal.getTimeInMillis();
						button.setText(DateUtil.formatToEventTimeStyle(time));
						if (button.getId() == R.id.btnStartTime) {
							startTime = time;
						} else {
							endTime = time;
						}
					}
				}, hour, minute, true);
		timePicker.show();
	}

	// 显示日期选择
	private void showDateDialog(final Button button) {

		int year, month, day;
		Calendar cal = new GregorianCalendar();
		if (button.getId() == R.id.btnStartDate) {
			cal.setTimeInMillis(startTime);
		} else {
			cal.setTimeInMillis(endTime);
		}

		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog dialog = new DatePickerDialog(this,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Calendar cal = new GregorianCalendar();
						cal.set(Calendar.YEAR, year);
						cal.set(Calendar.MONTH, monthOfYear);
						cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						long time = cal.getTimeInMillis();
						button.setText(DateUtil.formatToEventDateStyle(time));
						if (button.getId() == R.id.btnStartDate) {
							startTime = time;
						} else {
							endTime = time;
						}

					}
				}, year, month, day);
		dialog.show();
	}
}
