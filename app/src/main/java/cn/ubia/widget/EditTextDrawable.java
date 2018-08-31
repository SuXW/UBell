/**
 * 
 */
package cn.ubia.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * EditTextDrawable
 * 
 * @author dFTzhisheng 2014-5-5 : 响应Drawable点击事件
 */
public class EditTextDrawable extends EditText {
	DrawableListener listener;

	public EditTextDrawable(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public EditTextDrawable(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public EditTextDrawable(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Drawable[] ds = getCompoundDrawables();
		Drawable drawableL = ds[0];
		Drawable drawableR = ds[2];
		if (event.getAction() == MotionEvent.ACTION_UP) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			if (listener != null) {
				boolean onLeft = x > getPaddingLeft()
						&& x < getTotalPaddingLeft();
				boolean onRight = x > (getWidth() - getTotalPaddingRight())
						&& x < (getWidth() - getPaddingRight());
				if (drawableL != null && onLeft) {
					listener.onLeft();
				} else if (drawableR != null && onRight) {
					listener.onRight();
				}
			}
		}
		return super.onTouchEvent(event);
	}
	
	@Override 
	protected void onDraw(Canvas canvas) {
		setTextSize(14);
		super.onDraw(canvas);
	}

	public void setDrawableListener(DrawableListener listener) {
		this.listener = listener;
	}

	public interface DrawableListener {
		public void onLeft();
		public void onRight();
	}
}
