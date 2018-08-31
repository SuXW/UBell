package cn.ubia.widget;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.ubia.UBell.R;

public class MyProgressBar {

	private Context mContext;
	private LayoutInflater mInflater;
	private ProgressBar mProgressBar;
	private TextView mTextView;
	private ViewGroup rootView;
	private FrameLayout mFrameLayout;

	public MyProgressBar(Context context, int id) {
		this(context, null);
	}

	public MyProgressBar(Context context) {
		this(context, null);
	}

	public MyProgressBar(Context context, ViewGroup rootView) {
		this.mContext = context;
		this.rootView = rootView;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (rootView == null) {
			Activity activity = (Activity) this.mContext;
			rootView = (ViewGroup) activity.getWindow().getDecorView();
		}
		mFrameLayout = (FrameLayout) mInflater.inflate(R.layout.progress_bar,
				null);
		mProgressBar = (ProgressBar) mFrameLayout
				.findViewById(R.id.progress_bar);
		mTextView = (TextView) mFrameLayout
				.findViewById(R.id.text);
		rootView.addView(mFrameLayout);
	}

	public void removeSelf() {
		if (mFrameLayout != null && rootView != null) {
			rootView.removeView(mFrameLayout);
		}
	}

	public boolean isShowing() {
		return mProgressBar.getVisibility() == View.VISIBLE;
	}

	public void show() {
		mProgressBar.setVisibility(View.VISIBLE);
		mTextView.setVisibility(View.VISIBLE);
	}
	public void show(String txt) {
		if(txt!=null)
			mTextView.setText(txt);
		mProgressBar.setVisibility(View.VISIBLE);
		mTextView.setVisibility(View.VISIBLE);
	}
	public void hide() {
		mProgressBar.setVisibility(View.GONE);
		mTextView.setVisibility(View.GONE);
	}

	public void dismiss() {
		mTextView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.GONE);
	}
	
	public void setText(String txt){
		
		if(txt!=null)
			mTextView.setText(txt);
		
	}
	public String getText(){
		return (String) mTextView.getText();
	}
}