package cn.ubia.widget;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import cn.ubia.UBell.R;

/**
 * 日期和时间同时选择的对话框
 * 
 * @author great.maronghua@gmail.com
 * @since 2013-7-4
 */
public class DateTimePickerDialog extends Dialog {

	private static DateTimePickerDialog mDialog;

	private DatePicker mDatePicker;
	private TimePicker mTimePicker;

	private int year, monthOfYear, dayOfMonth, currentHour, currentMinute;

	public DateTimePickerDialog(Context context) {
		super(context);
	}

	private DateTimePickerDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	private DateTimePickerDialog(Context context, int theme) {
		super(context, theme);
	}

	public final static DateTimePickerDialog createDialog(Context ctx) {
		mDialog = new DateTimePickerDialog(ctx,
				R.style.date_and_time_picker_dialog);
		mDialog.setContentView(R.layout.date_and_time_picker);
		mDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		mDialog.mDatePicker = (DatePicker) mDialog
				.findViewById(R.id.DatePicker);
		mDialog.mTimePicker = (TimePicker) mDialog
				.findViewById(R.id.TimePicker);
		mDialog.findViewById(R.id.button_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mDialog.dismiss();
					}
				});
		return mDialog;
	}
	

	public DateTimePickerDialog init(long milliseconds){
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(milliseconds);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		DateTimePickerDialog datePicker = initDatePicker(year,month,day);
		return datePicker.initTimePicker(hour, minute);
	}
	

	/** 设置时间空间的初始小时，分钟 */
	public DateTimePickerDialog initTimePicker(int currentHour,
			int currentMinute) {
		this.currentHour = currentHour;
		this.currentMinute = currentMinute;
		mTimePicker.setCurrentHour(currentHour);
		mTimePicker.setCurrentMinute(currentMinute);
		return this;
	}

	/** 设置日期选择空间的初始年月日 */
	public DateTimePickerDialog initDatePicker(int year, int monthOfYear,
			int dayOfMonth) {
		this.year = year;
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		mDatePicker.init(year, monthOfYear, dayOfMonth, null);
		return this;
	}

	public DateTimePickerDialog setButtonOkOnClickListener(
			View.OnClickListener l) {
		findViewById(R.id.button_ok).setOnClickListener(l);
		return this;
	}

	/** 获取当前对话框中，选择的值 */
	private void getCurrentDialogValue() {
		this.year = mDatePicker.getYear();
		this.monthOfYear = mDatePicker.getMonth();
		this.dayOfMonth = mDatePicker.getDayOfMonth();
		this.currentHour = mTimePicker.getCurrentHour();
		this.currentMinute = mTimePicker.getCurrentMinute();
	}

	/**
	 * 返回当前的设置时间
	 * 
	 * @return
	 */
	public final GregorianCalendar getCurrentCalendar() {
		getCurrentDialogValue();
		GregorianCalendar mCalendar = new GregorianCalendar(year, monthOfYear,
				dayOfMonth, currentHour, currentMinute);
		return mCalendar;
	}

	/**
	 * 返回当前的设置时间
	 * 
	 * @return
	 */
	public final long getCurrentMillisTime() {
		return getCurrentCalendar().getTimeInMillis();
	}

}
