package cn.ubia.widget;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;

import cn.ubia.UBell.R;
import com.ubia.util.DateUtil;

/**
 * Widget that lets users select a minimum and maximum value on a given
 * numerical range. The range value types can be one of Long, Double, Integer,
 * Float, Short, Byte or BigDecimal.<br />
 * <br />
 * Improved {@link MotionEvent} handling for smoother use, anti-aliased painting
 * for improved aesthetics.
 * 
 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
 * @author Peter Sinnott (psinnott@gmail.com)
 * @author Thomas Barrasso (tbarrasso@sevenplusandroid.org)
 * 
 * @param <T>
 *            The Number type of the range values. One of Long, Double, Integer,
 *            Float, Short, Byte or BigDecimal.
 */
public class VideoSeekBar<T extends Number> extends ImageView {
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Bitmap thumbImage = BitmapFactory.decodeResource(
			getResources(), R.drawable.seek_thumb_normal);
	private final Bitmap thumbPressedImage = BitmapFactory.decodeResource(
			getResources(), R.drawable.seek_thumb_pressed);
	private final Bitmap thumbCutLeftImage = BitmapFactory.decodeResource(
			getResources(), R.drawable.ic_mylauncher);
	private final Bitmap thumbCutLeftPressedImage = BitmapFactory
			.decodeResource(getResources(), R.drawable.ic_mylauncher);

	private final Bitmap thumbCutRightImage = BitmapFactory.decodeResource(
			getResources(), R.drawable.ic_mylauncher);
	private final Bitmap thumbCutRightPressedImage = BitmapFactory
			.decodeResource(getResources(), R.drawable.ic_mylauncher);

	private final float thumbWidth = thumbImage.getWidth();
	private final float thumbHalfWidth = 0.5f * thumbWidth;
	private final float thumbHalfHeight = 0.5f * thumbImage.getHeight();

	private final float thumbCutLeftHeight = thumbCutLeftImage.getHeight();

	private float mLineHeight = 2;
	private final float padding = 0;
	private int absoluteMinValue, absoluteMaxValue;
	private final NumberType numberType;
	private int absoluteMinValuePrim, absoluteMaxValuePrim;
	private double normalizedMinValue = 0d;
	private double normalizedMaxValue = 1d;
	private Thumb pressedThumb = null;
	private boolean notifyWhileDragging = false;
	private OnRangeSeekBarChangeListener<T> onRangeChangelistener;
	private float mCutRangeHeight = 20;
	private long mProgress;

	/**
	 * Default color of a {@link VideoSeekBar}, #FF33B5E5. This is also known as
	 * "Ice Cream Sandwich" blue.
	 */
	public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5);

	public static final int CUT_RANGE_DEFAULT_COLOR = Color.argb(0x8F, 0x33,
			0xB5, 0xE5);

	/**
	 * An invalid pointer id.
	 */
	public static final int INVALID_POINTER_ID = 255;

	/**
	 * 刻度宽度
	 */
	private float mScaleWidth = 2;

	/**
	 * 事件高度
	 */
	private float mEventHeight = 5;

	/**
	 * 小刻度高度
	 */
	private float mSmallScaleHeight = 8;

	/**
	 * 大刻度高度
	 */
	private float mBigScaleHeight = 15;

	// Localized constants from MotionEvent for compatibility
	// with API < 8 "Froyo".
	public static final int ACTION_POINTER_UP = 0x6,
			ACTION_POINTER_INDEX_MASK = 0x0000ff00,
			ACTION_POINTER_INDEX_SHIFT = 8;

	// 坐标轴基于中心的偏移
	private static final int TOP_OFFSET = 60;

	private static final int BUTTOM_OFFSET = 20;
	private float mFontSize = 14;

	private static final int TYPE_FIVE_MINUTE = 0;
	private static final int TYPE_HOUR = 1;
	private static final int TYPE_DAY = 2;

	private float mDownMotionX;
	private int mActivePointerId = INVALID_POINTER_ID;

	/**
	 * On touch, this offset plus the scaled value from the position of the
	 * touch will form the progress value. Usually 0.
	 */
	float mTouchProgressOffset;

	private int mScaledTouchSlop;
	private boolean mIsDragging;

	public static ScaleMode FIVE_MINUTE_MODE = new ScaleMode(/* type= */0, 5);
	public static ScaleMode HOUR_MODE = new ScaleMode(/* type= */1, 4);
	public static ScaleMode DAY_MODE = new ScaleMode(/* type= */2, 6);
	public static ScaleMode ONE_MINUTE_MODE = new ScaleMode(/* type= */3, 4);
	public static ScaleMode TWENTY_SECOND_MODE = new ScaleMode(/* type= */4, 4);

	private ScaleMode mScaleMode;
	private Calendar mCalendar;
	private OnDateChangedListener<T> onDateChangedListener;
	private boolean mCutMode;
	private double normalizedProgressValue;
	private OnProgressChangeListener<T> progresslistener;
	private List<Event> mEvents;
	private float mMoveX;
	private boolean mIsViewDraging;
	private float mOldMoveX;
	private long mDownTime;
	private long mUpTime;
	private int mEventColor;
	private int mEventSelectedColor;

	/**
	 * Creates a new RangeSeekBar.
	 * 
	 * @param absoluteMinValue
	 *            The minimum value of the selectable range.
	 * @param absoluteMaxValue
	 *            The maximum value of the selectable range.
	 * @param context
	 * @throws IllegalArgumentException
	 *             Will be thrown if min/max value type is not one of Long,
	 *             Double, Integer, Float, Short, Byte or BigDecimal.
	 */
	public VideoSeekBar(Calendar cal, ScaleMode scaleMode, Context context)
			throws IllegalArgumentException {
		super(context);

		initSize(context);

		mEventColor = getContext().getResources().getColor(R.color.event_color);
		mEventSelectedColor = getContext().getResources().getColor(
				R.color.event_selected_color);

		numberType = NumberType.fromNumber(absoluteMinValue);
		mScaleMode = scaleMode;
		this.mCalendar = cal;
		mProgress = this.mCalendar.getTimeInMillis();
		// int minute = mCalendar.get(Calendar.MINUTE);
		// minute = minute / 5 * 5;
		// mCalendar.set(Calendar.MINUTE, minute);
		mCalendar.set(Calendar.SECOND, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);
		setupValues(cal);
		normalizedMinValue = 0.4;
		normalizedMaxValue = 0.6;
		initProgressValue();
		// make RangeSeekBar focusable. This solves focus handling issues in
		// within ScollViews.
		setFocusable(true);
		setFocusableInTouchMode(true);
		init();
	}

	// 根据屏幕调整各种尺寸
	private void initSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		mFontSize = mFontSize * metric.density;
		mLineHeight = mLineHeight * metric.density;
		mCutRangeHeight = mCutRangeHeight * metric.density;
		mScaleWidth = mScaleWidth * metric.density;
		mEventHeight = mEventHeight * metric.density;
		mSmallScaleHeight = mSmallScaleHeight * metric.density;
		mBigScaleHeight = mBigScaleHeight * metric.density;
	}

	public void setEvents(List<Event> events) {
		mEvents = events;
		invalidate();
	}

	public ScaleMode getScaleMode() {
		return mScaleMode;
	}

	private void initProgressValue() {
		long smallStepRange = (long) (1.0 * mScaleMode.getSize()
				* mScaleMode.getTimeStep() / mScaleMode.getBigScaleCount() / 5 * mScaleMode
				.getSmallScaleCount());
		long start = mCalendar.getTimeInMillis();
		long pos = mProgress - start + 3 * smallStepRange
				/ mScaleMode.getSmallScaleCount();
		normalizedProgressValue = 1.0f * pos / smallStepRange;
	}

	// 设置进度
	public void setProgress(long progress) {
		this.mProgress = progress;
		initProgressValue();
		if (normalizedProgressValue >= 1) {
			mCalendar.add(Calendar.MILLISECOND,
					(int) (mScaleMode.getSize() * mScaleMode.getTimeStep()));
			initProgressValue();
		}
		invalidate();
	}

	public void setStartProgress(long progress) {
		long smallSteplSize = mScaleMode.getSize() * mScaleMode.getTimeStep()
				/ mScaleMode.getBigScaleCount() / 5;
		progress = progress - 3 * smallSteplSize;
		setProgress(progress);
	}

	private void resetCalender() {
		float smallScaleSize = (float) (getWidth() - padding * 2)
				/ mScaleMode.getSmallScaleCount();
		float bigScaleCount = mScaleMode.getBigScaleCount();
		float bigSaleSize = (float) (getWidth() - padding * 2 - 6 * smallScaleSize)
				/ bigScaleCount;
		int timeChanged = (int) (mMoveX / bigSaleSize)
				* (int) (mScaleMode.getSize() / mScaleMode.getBigScaleCount() * mScaleMode
						.getTimeStep());
		mCalendar.add(Calendar.MILLISECOND, -1 * timeChanged);
		setupValues(mCalendar);
		float afterMove = normalizedToScreen(this.normalizedProgressValue)
				+ mMoveX - mMoveX % bigSaleSize;
		this.normalizedProgressValue = screenToNormalized2(afterMove);
		mMoveX = mMoveX % bigSaleSize;
		mOldMoveX = mMoveX;
		invalidate();
	}

	// 进度比例
	public void setNormalizedProgressValue(double value) {
		normalizedProgressValue = value;
		invalidate();
	}

	// 设置刻度类型,并重画
	public void setScaleMode(ScaleMode scaleMode) {
		if (this.mScaleMode != scaleMode) {
			mCalendar.setTimeInMillis(getProgress());
			this.mScaleMode = scaleMode;
			if (mScaleMode == HOUR_MODE) {
				int minute = mCalendar.get(Calendar.MINUTE);
				minute = minute / 5 * 5;
				mCalendar.set(Calendar.MINUTE, minute);
			} else if (mScaleMode == ONE_MINUTE_MODE) {
				mCalendar.set(Calendar.SECOND, 0);
			}
			setupValues(mCalendar);
			initProgressValue();
			invalidate();
		}
	}

	private void setupValues(Calendar cal) {
		switch (mScaleMode.type) {
		case 0: // 5分钟
			this.absoluteMinValue = cal.get(Calendar.MINUTE);
			break;
		case 1: // 1小时
			this.absoluteMinValue = cal.get(Calendar.MINUTE);
			break;
		case 2: // 1天
			this.absoluteMinValue = cal.get(Calendar.HOUR_OF_DAY);
			break;
		case 3: // 1分钟
			this.absoluteMinValue = cal.get(Calendar.SECOND);
			break;
		case 4: // 20分钟
			this.absoluteMinValue = cal.get(Calendar.SECOND);
			break;
		default:
			break;
		}
		this.absoluteMaxValue = absoluteMinValue + mScaleMode.size;
		absoluteMinValuePrim = absoluteMinValue;
		absoluteMaxValuePrim = absoluteMaxValue;
	}

	private final void init() {
		mScaledTouchSlop = ViewConfiguration.get(getContext())
				.getScaledTouchSlop();
	}

	public boolean isNotifyWhileDragging() {
		return notifyWhileDragging;
	}

	/**
	 * Should the widget notify the listener callback while the user is still
	 * dragging a thumb? Default is false.
	 * 
	 * @param flag
	 */
	public void setNotifyWhileDragging(boolean flag) {
		this.notifyWhileDragging = flag;
	}

	/**
	 * Returns the absolute minimum value of the range that has been set at
	 * construction time.
	 * 
	 * @return The absolute minimum value of the range.
	 */
	public int getAbsoluteMinValue() {
		return absoluteMinValue;
	}

	/**
	 * Returns the absolute maximum value of the range that has been set at
	 * construction time.
	 * 
	 * @return The absolute maximum value of the range.
	 */
	public int getAbsoluteMaxValue() {
		return absoluteMaxValue;
	}

	/**
	 * Returns the currently selected min value.
	 * 
	 * @return The currently selected min value.
	 */
	public T getSelectedMinValue() {
		return normalizedToValue(normalizedMinValue);
	}

	/**
	 * Sets the currently selected minimum value. The widget will be invalidated
	 * and redrawn.
	 * 
	 * @param value
	 *            The Number value to set the minimum value to. Will be clamped
	 *            to given absolute minimum/maximum range.
	 */
	public void setSelectedMinValue(T value) {
		// in case absoluteMinValue == absoluteMaxValue, avoid division by zero
		// when normalizing.
		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			setNormalizedMinValue(0d);
		} else {
			setNormalizedMinValue(valueToNormalized(value));
		}
	}

	/**
	 * Returns the currently selected max value.
	 * 
	 * @return The currently selected max value.
	 */
	public T getSelectedMaxValue() {
		return normalizedToValue(normalizedMaxValue);
	}

	/**
	 * Sets the currently selected maximum value. The widget will be invalidated
	 * and redrawn.
	 * 
	 * @param value
	 *            The Number value to set the maximum value to. Will be clamped
	 *            to given absolute minimum/maximum range.
	 */
	public void setSelectedMaxValue(T value) {
		// in case absoluteMinValue == absoluteMaxValue, avoid division by zero
		// when normalizing.
		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			setNormalizedMaxValue(1d);
		} else {
			setNormalizedMaxValue(valueToNormalized(value));
		}
	}

	/**
	 * Registers given listener callback to notify about changed selected
	 * values.
	 * 
	 * @param listener
	 *            The listener to notify about changed selected values.
	 */
	public void setOnRangeSeekBarChangeListener(
			OnRangeSeekBarChangeListener<T> listener) {
		this.onRangeChangelistener = listener;
	}

	// 设置进度变动监听
	public void setOnProgressChangeListener(OnProgressChangeListener<T> listener) {
		this.progresslistener = listener;
	}

	public void setOnDateChangedListener(OnDateChangedListener<T> listener) {
		this.onDateChangedListener = listener;
	}

	/**
	 * Handles thumb selection and movement. Notifies listener callback on
	 * certain events.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled())
			return false;

		int pointerIndex;

		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:
			// Remember where the motion event started
			mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
			pointerIndex = event.findPointerIndex(mActivePointerId);
			mDownMotionX = event.getX(pointerIndex);

			pressedThumb = evalPressedThumb(mDownMotionX);

			mDownTime = System.currentTimeMillis();

			// Only handle thumb presses.
			if (pressedThumb != null) {
				setPressed(true);
				invalidate();
				onStartTrackingTouch();
				trackTouchEvent(event);
				attemptClaimDrag();
			} else {
				onStartViewTouch();
				attemptClaimDrag();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (pressedThumb != null) {
				if (mIsDragging) {
					trackTouchEvent(event);
				} else {
					// Scroll to follow the motion event
					pointerIndex = event.findPointerIndex(mActivePointerId);
					final float x = event.getX(pointerIndex);

					if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
						setPressed(true);
						invalidate();
						onStartTrackingTouch();
						trackTouchEvent(event);
						attemptClaimDrag();
					}
				}

				// if (notifyWhileDragging && onRangeChangelistener != null
				// && mCutMode) {
				// onRangeChangelistener.onRangeSeekBarValuesChanged(this,
				// getCutStartTime(), getCutEndTime());
				// }
			} else {
				if (mIsViewDraging) {
					pointerIndex = event.findPointerIndex(mActivePointerId);
					final float x = event.getX(pointerIndex);
					if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
						mMoveX = mOldMoveX + x - mDownMotionX;
						invalidate();
						onStartViewTouch();
						attemptClaimDrag();
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mUpTime = System.currentTimeMillis();
			if (mIsDragging) {
				trackTouchEvent(event);
				onStopTrackingTouch();
				setPressed(false);
			} else {
				// Touch up when we never crossed the touch slop threshold
				// should be interpreted as a tap-seek to that location.
				onStartTrackingTouch();
				trackTouchEvent(event);
				onStopTrackingTouch();
			}

			pointerIndex = event.findPointerIndex(mActivePointerId);
			final float x = event.getX(pointerIndex);
			if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
				if (!mCutMode) {
					if (!mIsViewDraging && pressedThumb != null) {
						mProgress = getProgress();
						if (progresslistener != null) {
							progresslistener.onProgressChanged(this, mProgress);
						}
					}
				}
			} else {
				// 处理单击进度切换
				if (!mCutMode) {
					if (mUpTime - mDownTime < 1000) {
						doProressChanged(x);
					}
				}
			}

			if (mIsViewDraging) {
				onStopViewTouch();
				resetCalender();
			}

			if (mCutMode) {
				if (onRangeChangelistener != null) {
					boolean isStartChanged = false;
					if (pressedThumb == Thumb.MIN) {
						isStartChanged = true;
					}
					onRangeChangelistener.onRangeSeekBarValuesChanged(this,
							getCutStartTime(), getCutEndTime(), isStartChanged);
				}
			}
			pressedThumb = null;
			invalidate();
			break;
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int index = event.getPointerCount() - 1;
			// final int index = ev.getActionIndex();
			mDownMotionX = event.getX(index);
			mActivePointerId = event.getPointerId(index);
			invalidate();
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(event);
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsDragging) {
				onStopTrackingTouch();
				setPressed(false);
			}
			invalidate(); // see above explanation
			break;
		}
		return true;
	}

	// 单击处理，切合进度条到当前位置
	private void doProressChanged(float x) {
		this.normalizedProgressValue = screenToNormalized(x - mMoveX);
		invalidate();
		mProgress = getProgress();
		if (progresslistener != null) {
			progresslistener.onProgressChanged(this, mProgress);
		}
	}

	private void onStartViewTouch() {
		mIsViewDraging = true;
	}

	private void onStopViewTouch() {
		mIsViewDraging = false;
	}

	/** 返回播放进度时间 **/
	public long getProgress() {
		return getTime(normalizedProgressValue);
	}

	/** 返回剪切开始时间 **/
	public long getCutStartTime() {
		double min = screenToNormalized2(normalizedToScreen(normalizedMinValue)
				- mMoveX);
		return getTime(min);
	}

	/** 返回剪切结束时间 **/
	public long getCutEndTime() {
		double max = screenToNormalized2(normalizedToScreen(normalizedMaxValue)
				- mMoveX);
		return getTime(max);
	}

	/** 返回剪切事件的实际开始时间 **/
	public long getRealCutStartTime() {
		if (mEvents == null || mEvents.size() == 0) {
			return 0;
		}
		long time = getCutStartTime();
		for (Event event : mEvents) {

			// 在某一事件当中，直接返回
			if (time >= event.startTime && time <= event.endTime) {
				break;
			}

			// 在事件的左边，设置为事件的开始时间
			if (time < event.startTime) {
				time = event.startTime;
				break;
			}

		}
		return time;
	}

	/** 返回剪切事件的实际结束时间 **/
	private long getRealCutEndTime() {
		if (mEvents == null || mEvents.size() == 0) {
			return 0;
		}
		long time = getCutEndTime();
		long endTime = 0;
		for (Event event : mEvents) {

			// 在某一事件当中，直接返回
			if (time >= event.startTime && time <= event.endTime) {
				endTime = time;
				break;
			}

			// 在事件的左边，设置为事件的结束时间
			if (time > event.endTime) {
				endTime = event.endTime;
				continue;
			}
		}
		time = endTime;
		return time;
	}

	/** 返回剪切范围时长，单位毫秒 **/
	public int getRealCutRangeLength() {
		if (mEvents == null || mEvents.size() == 0) {
			return 0;
		}
		long start = getRealCutStartTime();
		long end = getRealCutEndTime();

		if (start > end) {
			return 0;
		}

		// 剪切点之间的距离
		int length = (int) (end - start);

		int emptyLength = 0;
		// 计算多个事件之间的空白区域时间长度
		for (int i = 0; i < mEvents.size(); i++) {
			Event event = mEvents.get(i);
			// 在剪切 范围左边，忽略
			if (event.endTime < start) {
				continue;
			}

			// 在剪切 范围右边，结束
			if (event.startTime > end) {
				break;
			}

			if (i + 1 < mEvents.size()) {
				Event nextEvent = mEvents.get(i + 1);
				if (nextEvent.startTime <= end) {
					emptyLength += nextEvent.startTime - event.endTime;
				}
			}
		}
		return length - emptyLength;

	}

	/** 检查剪切长度是否合法 **/
	public boolean checkCutRangeLengthValid(int min, int max) {
		int length = getRealCutRangeLength();
		if (length == 0) {
			return false;
		}
		if (length >= min && length <= max) {
			return true;
		}
		return false;
	}

	// 计算时间
	private long getTime(double normalizedValue) {
		long range = mScaleMode.getSize() * mScaleMode.getTimeStep()
				/ mScaleMode.getBigScaleCount() / 5
				* mScaleMode.getSmallScaleCount();
		long pos = (long) (normalizedValue * range) - 3 * range
				/ mScaleMode.getSmallScaleCount();
		long start = mCalendar.getTimeInMillis();
		return start + pos;
	}

	private final void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose
			// a new active pointer and adjust accordingly.
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mDownMotionX = ev.getX(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
		}
	}

	private final void trackTouchEvent(MotionEvent event) {
		final int pointerIndex = event.findPointerIndex(mActivePointerId);
		final float x = event.getX(pointerIndex);

		if (Thumb.MIN.equals(pressedThumb)) {
			setNormalizedMinValue(screenToNormalized(x));
		} else if (Thumb.MAX.equals(pressedThumb)) {
			setNormalizedMaxValue(screenToNormalized(x));
		} else if (Thumb.PROGRESS.equals(pressedThumb)) {
			setNormalizedProgressValue(screenToNormalized(x - mMoveX));
		}
	}

	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 */
	private void attemptClaimDrag() {
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
	}

	/**
	 * This is called when the user has started touching this widget.
	 */
	void onStartTrackingTouch() {
		mIsDragging = true;
	}

	/**
	 * This is called when the user either releases his touch or the touch is
	 * canceled.
	 */
	void onStopTrackingTouch() {
		mIsDragging = false;
	}

	/**
	 * Ensures correct size of the widget.
	 */
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		int width = 200;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}

		int height = (int) (thumbImage.getHeight() + TOP_OFFSET
				+ mBigScaleHeight + getFontHeight() + BUTTOM_OFFSET);
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
			height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
		}
		setMeasuredDimension(width, height);
	}

	private void drawEvent(Canvas canvas, float baseline, Event event) {
		float smallScaleSize = (float) (getWidth() - padding * 2)
				/ mScaleMode.getSmallScaleCount();

		long start = event.startTime;
		long end = event.endTime;
		long current = mCalendar.getTimeInMillis();
		// Log.d("current", DateUtil.formatToNormalStyle(current));
		start = start - current;
		end = end - current;
		long range = mScaleMode.getSize() * mScaleMode.getTimeStep();
		float totalLength = getWidth() - 2 * padding - 6 * smallScaleSize;
		float width = 1.0f * (end - start) / range * totalLength;

		float leftPadding = 3 * smallScaleSize + padding + mMoveX;
		float left = leftPadding + (float) (start * 1.0 / range) * totalLength;
		float right = left + width;

		if (right > getWidth() - padding) {
			right = getWidth() - padding;
		}

		if (left < padding) {
			left = padding;
		}

		float buttom = baseline - mLineHeight * 0.5f;
		float top = buttom - mEventHeight;
		final RectF rect = new RectF(left, top, right, buttom);
		paint.setStyle(Style.FILL);
		paint.setColor(mEventColor);
		paint.setAntiAlias(true);
		canvas.drawRect(rect, paint);
	}

	public static String formatToDateStyle(long time) {
		Date date = new Date(time);
		String pattern = "yyyy-MM-dd";
		java.text.DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	private void drawDate(Canvas canvas, float baseLine) {
		String startDate = formatToDateStyle(mCalendar.getTimeInMillis());
		GregorianCalendar endCal = new GregorianCalendar();
		endCal.setTimeInMillis(mCalendar.getTimeInMillis());

		switch (mScaleMode.type) {
		case 0:
			endCal.add(Calendar.MINUTE, mScaleMode.getSize());
			break;
		case 1:
			endCal.add(Calendar.MINUTE, mScaleMode.getSize());
			break;
		case 2:
			endCal.add(Calendar.HOUR_OF_DAY, mScaleMode.getSize());
			break;
		case 3:
			endCal.add(Calendar.SECOND, mScaleMode.getSize());
			break;
		case 4:
			endCal.add(Calendar.SECOND, mScaleMode.getSize());
			break;
		default:
			break;
		}
		String endDate = formatToDateStyle(endCal.getTimeInMillis());
		float top = getFontHeight();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setTextSize(mFontSize);
		if (mScaleMode.type == TYPE_DAY) {
			canvas.drawText(startDate, padding, top, paint);
			canvas.drawText(endDate, getWidth() - padding
					- getStringWidth(endDate), top, paint);
		} else {
			canvas.drawText(startDate,
					(getWidth() - getStringWidth(startDate)) / 2, top, paint);
		}
	}

	private float getBaseLinePos() {
		return (float) (getHeight() * 0.5);
	}

	public void setCutMode(boolean cutMode) {
		this.mCutMode = cutMode;
		// this.normalizedMinValue =
		invalidate();
	}

	public boolean isCutMode() {
		return mCutMode;
	}

	/**
	 * Draws the widget on the given canvas.
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float baseLine = getBaseLinePos();
		// draw seek bar background line
		final RectF rect = new RectF(padding, baseLine - mLineHeight * 0.5f,
				getWidth() - padding, baseLine + mLineHeight * 0.5f);
		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		canvas.drawRect(rect, paint);

		// 画小刻度
		drawSmallScale(canvas, baseLine);

		// 画大刻度
		drawBigScale(canvas, baseLine);

		// Log.d("start", DateUtil.formatToNormalStyle(start));
		// Log.d("end", DateUtil.formatToNormalStyle(end));

		// 画事件
		if (mEvents != null) {
			for (Event event : mEvents) {
				drawEvent(canvas, baseLine, event);
			}
		}
		// 画日期
		drawDate(canvas, baseLine);

		// 画剪切选择按钮
		if (mCutMode) {
			// draw seek bar active range line
			rect.left = normalizedToScreen(normalizedMinValue);
			rect.right = normalizedToScreen(normalizedMaxValue);
			rect.top = baseLine + mLineHeight * 0.5f - mCutRangeHeight;
			// orange color
			paint.setColor(mEventSelectedColor);
			canvas.drawRect(rect, paint);

			// // draw left thumb
			drawRightThumb(normalizedToScreen(normalizedMinValue),
					Thumb.MIN.equals(pressedThumb), canvas);
			//
			// // draw right thumb
			drawRightThumb(normalizedToScreen(normalizedMaxValue),
					Thumb.MAX.equals(pressedThumb), canvas);
		}

		if (!mCutMode) {
			// 画进度按钮
			drawProgressThumb(canvas);
		}
	}

	private void drawProgressThumb(Canvas canvas) {
		if (normalizedProgressValue > 1 || normalizedProgressValue < 0) {
			return;
		}
		paint.setColor(DEFAULT_COLOR);
		float linePos = getBaseLinePos();
		float left = normalizedToScreen(normalizedProgressValue) + mMoveX
				- thumbHalfWidth;
		canvas.drawBitmap(
				Thumb.PROGRESS.equals(pressedThumb) ? thumbPressedImage
						: thumbImage, left,
				(float) (linePos - thumbHalfHeight), paint);
	}

	private void drawBigScale(Canvas canvas, float baseLine) {
		float smallScaleSize = (float) (getWidth() - padding * 2)
				/ mScaleMode.getSmallScaleCount();
		float scaleSize = (float) (getWidth() - padding * 2 - 6 * smallScaleSize)
				/ mScaleMode.getBigScaleCount();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setTextSize(mFontSize);
		int fontHeight = getFontHeight();
		int timeChanged = (int) (mMoveX / scaleSize)
				* (int) (mScaleMode.getSize() / mScaleMode.getBigScaleCount() * mScaleMode
						.getTimeStep());

		int i = 0;
		if (mMoveX / smallScaleSize >= 2) {
			i = -1;
		}

		int count = mScaleMode.getBigScaleCount();
		if (mMoveX / smallScaleSize <= -2) {
			count = count + 1;
		}

		for (; i <= count; i++) {
			float left = 3 * smallScaleSize + padding + i * scaleSize;
			left = left + mMoveX % scaleSize;

			if (left < padding) {
				continue;
			}

			if (left + mScaleWidth > getWidth() - padding) {
				continue;
			}

			float top = baseLine + mLineHeight * 0.5f;
			final RectF rect = new RectF(left, top, left + mScaleWidth, top
					+ mBigScaleHeight);
			canvas.drawRect(rect, paint);

			int intTxt = (mScaleMode.getMax() + absoluteMinValuePrim
					- (int) (timeChanged / mScaleMode.timeStep) + (int) (i
					* mScaleMode.getSize() / mScaleMode.getBigScaleCount()))
					% mScaleMode.getMax();

			String text = "";
			// 1天
			if (mScaleMode.type == 2) {
				text = intTxt + ":00:00";
			}
			// 5分钟或1小时
			else if (mScaleMode.type == 0 || mScaleMode.type == 1) {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTimeInMillis(mCalendar.getTimeInMillis());
				calendar.add(Calendar.MILLISECOND, -1 * timeChanged);
				if (mScaleMode.type == 0) {
					calendar.add(Calendar.MINUTE, i);
				} else {
					calendar.add(Calendar.MINUTE, 60 * i
							/ mScaleMode.bigScaleCount);
				}
				if (intTxt < 10) {
					text = calendar.get(Calendar.HOUR_OF_DAY) + ":0" + intTxt
							+ ":00";
				} else {
					text = calendar.get(Calendar.HOUR_OF_DAY) + ":" + intTxt
							+ ":00";
				}
			}
			// 1分钟
			else if (mScaleMode.type == 3 || mScaleMode.type == 4) {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTimeInMillis(mCalendar.getTimeInMillis());
				calendar.add(Calendar.MILLISECOND, -1 * timeChanged);
				calendar.add(Calendar.SECOND, mScaleMode.getSize() * i
						/ mScaleMode.bigScaleCount);
				text = DateUtil.formatNormalTimeStyle(calendar
						.getTimeInMillis());
			}

			int strWidth = getStringWidth(text);
			float fontLeft = 3 * smallScaleSize + padding + i * scaleSize
					- strWidth / 2;
			fontLeft = fontLeft + mMoveX % scaleSize;
			float fontTop = baseLine + mLineHeight * 0.5f + mBigScaleHeight
					+ fontHeight / 2 + 20;
			canvas.drawText(text, fontLeft, fontTop, paint);
		}
	}

	private int getStringWidth(String text) {
		return (int) paint.measureText(text);
	}

	private int getFontHeight() {
		Paint paint = new Paint();
		paint.setTextSize(mFontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) (Math.ceil((fm.descent - fm.top)) + 2);
	}

	// 画刻度
	private void drawSmallScale(Canvas canvas, float baseLine) {
		float scaleSize = (float) (getWidth() - padding * 2)
				/ mScaleMode.getSmallScaleCount();
		float moveStep = mMoveX % scaleSize;

		for (int i = 0; i <= mScaleMode.getSmallScaleCount(); i++) {
			float left = padding + i * scaleSize + moveStep;
			float top = baseLine + mLineHeight * 0.5f;
			float right = left + mScaleWidth;
			float buttom = top + mSmallScaleHeight;
			if (left < padding) {
				continue;
			}
			if (right > getWidth() - padding) {
				return;
			}
			final RectF rect = new RectF(left, top, right, buttom);
			paint.setStyle(Style.FILL);
			paint.setColor(Color.WHITE);
			paint.setAntiAlias(true);
			canvas.drawRect(rect, paint);
		}

	}

	/**
	 * Overridden to save instance state when device orientation changes. This
	 * method is called automatically if you assign an id to the RangeSeekBar
	 * widget using the {@link #setId(int)} method. Other members of this class
	 * than the normalized min and max values don't need to be saved.
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable("SUPER", super.onSaveInstanceState());
		bundle.putDouble("MIN", normalizedMinValue);
		bundle.putDouble("MAX", normalizedMaxValue);
		bundle.putDouble("PROGRESS", normalizedProgressValue);
		return bundle;
	}

	/**
	 * Overridden to restore instance state when device orientation changes.
	 * This method is called automatically if you assign an id to the
	 * RangeSeekBar widget using the {@link #setId(int)} method.
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable parcel) {
		final Bundle bundle = (Bundle) parcel;
		super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
		normalizedMinValue = bundle.getDouble("MIN");
		normalizedMaxValue = bundle.getDouble("MAX");
		normalizedProgressValue = bundle.getDouble("PROGRESS");
	}

	/**
	 * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
	 * 
	 * @param screenCoord
	 *            The x-coordinate in screen space where to draw the image.
	 * @param pressed
	 *            Is the thumb currently in "pressed" state?
	 * @param canvas
	 *            The canvas to draw upon.
	 */
	private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
		paint.setColor(DEFAULT_COLOR);
		float linePos = getBaseLinePos();
		canvas.drawBitmap(pressed ? thumbPressedImage : thumbImage, screenCoord
				- thumbHalfWidth, (float) (linePos - thumbHalfHeight), paint);
	}

	private void drawLeftThumb(float screenCoord, boolean pressed, Canvas canvas) {
		paint.setColor(DEFAULT_COLOR);
		float linePos = getBaseLinePos();
		canvas.drawBitmap(pressed ? thumbCutLeftPressedImage
				: thumbCutLeftImage, screenCoord - thumbHalfWidth,
				(float) (linePos - thumbCutLeftHeight + mLineHeight + 5), paint);
	}

	private void drawRightThumb(float screenCoord, boolean pressed,
			Canvas canvas) {
		paint.setColor(DEFAULT_COLOR);
		float linePos = getBaseLinePos();
		canvas.drawBitmap(pressed ? thumbCutRightPressedImage
				: thumbCutRightImage, screenCoord - thumbHalfWidth,
				(float) (linePos - mCutRangeHeight - 5), paint);
	}

	/**
	 * Decides which (if any) thumb is touched by the given x-coordinate.
	 * 
	 * @param touchX
	 *            The x-coordinate of a touch event in screen space.
	 * @return The pressed thumb or null if none has been touched.
	 */
	private Thumb evalPressedThumb(float touchX) {
		Thumb result = null;
		if (mCutMode) {
			boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
			boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);
			if (minThumbPressed && maxThumbPressed) {
				// if both thumbs are pressed (they lie on top of each other),
				// choose the one with more room to drag. this avoids "stalling"
				// the
				// thumbs in a corner, not being able to drag them apart
				// anymore.
				result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
			} else if (minThumbPressed) {
				result = Thumb.MIN;
			} else if (maxThumbPressed) {
				result = Thumb.MAX;
			}
		} else {

			boolean progressThumbPressed = isInProgressThumbRange(touchX,
					normalizedProgressValue);
			if (progressThumbPressed) {
				result = Thumb.PROGRESS;
			}
		}

		return result;
	}

	private boolean isInProgressThumbRange(float touchX,
			double normalizedThumbValue) {
		float progressPos = normalizedToScreen(normalizedThumbValue) + mMoveX;
		return Math.abs(touchX - progressPos) <= thumbHalfWidth;
	}

	/**
	 * Decides if given x-coordinate in screen space needs to be interpreted as
	 * "within" the normalized thumb x-coordinate.
	 * 
	 * @param touchX
	 *            The x-coordinate in screen space to check.
	 * @param normalizedThumbValue
	 *            The normalized x-coordinate of the thumb to check.
	 * @return true if x-coordinate is in thumb range, false otherwise.
	 */
	private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
		return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth;
	}

	/**
	 * Sets normalized min value to value so that 0 <= value <= normalized max
	 * value <= 1. The View will get invalidated when calling this method.
	 * 
	 * @param value
	 *            The new normalized min value to set.
	 */
	public void setNormalizedMinValue(double value) {
		normalizedMinValue = Math.max(0d,
				Math.min(1d, Math.min(value, normalizedMaxValue)));
		invalidate();
	}

	/**
	 * Sets normalized max value to value so that 0 <= normalized min value <=
	 * value <= 1. The View will get invalidated when calling this method.
	 * 
	 * @param value
	 *            The new normalized max value to set.
	 */
	public void setNormalizedMaxValue(double value) {
		normalizedMaxValue = Math.max(0d,
				Math.min(1d, Math.max(value, normalizedMinValue)));
		invalidate();
	}

	public double getNormalizedMaxValue() {
		return normalizedMaxValue;
	}

	public double getNormalizedMinValue() {
		return normalizedMinValue;
	}

	public double getNormalizedProgressValue() {
		return normalizedProgressValue;
	}

	/**
	 * Converts a normalized value to a Number object in the value space between
	 * absolute minimum and maximum.
	 * 
	 * @param normalized
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T normalizedToValue(double normalized) {
		return (T) numberType.toNumber(absoluteMinValuePrim + normalized
				* (absoluteMaxValuePrim - absoluteMinValuePrim));
	}

	/**
	 * Converts the given Number value to a normalized double.
	 * 
	 * @param value
	 *            The Number value to normalize.
	 * @return The normalized double.
	 */
	private double valueToNormalized(T value) {
		if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
			// prevent division by zero, simply return 0.
			return 0d;
		}
		return (value.doubleValue() - absoluteMinValuePrim)
				/ (absoluteMaxValuePrim - absoluteMinValuePrim);
	}

	/**
	 * Converts a normalized value into screen space.
	 * 
	 * @param normalizedCoord
	 *            The normalized value to convert.
	 * @return The converted value in screen space.
	 */
	private float normalizedToScreen(double normalizedCoord) {
		return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
	}

	/**
	 * Converts screen space x-coordinates into normalized values.
	 * 
	 * @param screenCoord
	 *            The x-coordinate in screen space to convert.
	 * @return The normalized value.
	 */
	private double screenToNormalized(float screenCoord) {
		int width = getWidth();
		if (width <= 2 * padding) {
			// prevent division by zero, simply return 0.
			return 0d;
		} else {
			double result = (screenCoord - padding) / (width - 2 * padding);
			return Math.min(1d, Math.max(0d, result));
		}
	}

	// 支持超出范围,用以计算时间
	private double screenToNormalized2(float screenCoord) {
		int width = getWidth();
		if (width <= 2 * padding) {
			// prevent division by zero, simply return 0.
			return 0d;
		} else {
			double result = (screenCoord - padding) / (width - 2 * padding);
			return result;
		}
	}

	public long getCalendarTime() {
		return mCalendar.getTimeInMillis();
	}

	/**
	 * Callback listener interface to notify about changed range values.
	 * 
	 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
	 * 
	 * @param <T>
	 *            The Number type the RangeSeekBar has been declared with.
	 */
	public interface OnRangeSeekBarChangeListener<T> {
		public void onRangeSeekBarValuesChanged(VideoSeekBar<?> bar,
                                                long startTime, long endTime, boolean isStartChanged);
	}

	// 进度条监听
	public interface OnProgressChangeListener<T> {
		public void onProgressChanged(VideoSeekBar<?> bar, long time);
	}

	// 日期变动
	public interface OnDateChangedListener<T> {
		public void onDateChanged(VideoSeekBar<?> bar, Calendar cal);
	}

	/**
	 * Thumb constants (min and max).
	 */
	private static enum Thumb {
		MIN, MAX, PROGRESS
	};

	/**
	 * Utility enumaration used to convert between Numbers and doubles.
	 * 
	 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
	 * 
	 */
	private static enum NumberType {
		LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

		public static <E extends Number> NumberType fromNumber(E value)
				throws IllegalArgumentException {
			if (value instanceof Long) {
				return LONG;
			}
			if (value instanceof Double) {
				return DOUBLE;
			}
			if (value instanceof Integer) {
				return INTEGER;
			}
			if (value instanceof Float) {
				return FLOAT;
			}
			if (value instanceof Short) {
				return SHORT;
			}
			if (value instanceof Byte) {
				return BYTE;
			}
			if (value instanceof BigDecimal) {
				return BIG_DECIMAL;
			}
			throw new IllegalArgumentException("Number class '"
					+ value.getClass().getName() + "' is not supported");
		}

		public Number toNumber(double value) {
			switch (this) {
			case LONG:
				return new Long((long) value);
			case DOUBLE:
				return value;
			case INTEGER:
				return new Integer((int) value);
			case FLOAT:
				return new Float(value);
			case SHORT:
				return new Short((short) value);
			case BYTE:
				return new Byte((byte) value);
			case BIG_DECIMAL:
				return new BigDecimal(value);
			}
			throw new InstantiationError("can't convert " + this
					+ " to a Number object");
		}
	}

	// 时间刻度模式
	public static class ScaleMode {
		private int type; // type=0:5分钟,type=1:小时,type=2:天，type=3:1分钟,type=4:20秒
		private int smallScaleCount; // 小刻度个数
		private int bigScaleCount; // 大刻度个数
		private int size;
		private int max;
		private long timeStep;

		public ScaleMode(int type, int bigScaleCount) {
			super();
			this.type = type;
			this.bigScaleCount = bigScaleCount;
			this.smallScaleCount = bigScaleCount * 5 + 6;

			switch (type) {
			case 0: // 5分钟
				size = 5;
				max = 60;
				timeStep = 60 * 1000;
				break;
			case 1: // 1小时
				size = 60;
				max = 60;
				timeStep = 60 * 1000;
				break;
			case 2: // 1天
				size = 24;
				max = 24;
				timeStep = 60 * 60 * 1000;
				break;
			case 3: // 1分钟
				size = 60;
				max = 60;
				timeStep = 1000;
				break;
			case 4: // 20秒
				size = 20;
				max = 60;
				timeStep = 1000;
				break;
			default:
				break;
			}
		}

		public int getType() {
			return type;
		}

		public int getSmallScaleCount() {
			return smallScaleCount;
		}

		public int getBigScaleCount() {
			return bigScaleCount;
		}

		public int getSize() {
			return size;
		}

		public int getMax() {
			return max;
		};

		public long getTimeStep() {
			return timeStep;
		}

	}

	// 事件类型
	public static class Event implements Serializable {
		private static final long serialVersionUID = 1L;
		public long startTime;
		public long endTime;
	}

}
