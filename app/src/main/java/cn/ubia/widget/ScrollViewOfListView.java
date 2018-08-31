package cn.ubia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ListView;

public class ScrollViewOfListView extends ListView {

	public ScrollViewOfListView(Context context) {
		super(context);
	}

	public ScrollViewOfListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollViewOfListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightExpandSpec = MeasureSpec.makeMeasureSpec(
				Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightExpandSpec);
	}

}
