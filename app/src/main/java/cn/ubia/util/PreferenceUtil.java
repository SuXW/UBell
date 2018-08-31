package cn.ubia.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtil {
	private static PreferenceUtil mInstance;
	private Context mContext;
	private SharedPreferences mPref;

	private PreferenceUtil() {
	}

	public synchronized static PreferenceUtil getInstance() {
		if (null == mInstance) {
			mInstance = new PreferenceUtil();
		}
		return mInstance;
	}

	public void init(Context context) {
		if (mContext == null) {
			mContext = context;
		}
		if (mPref == null) {
			mPref = context.getSharedPreferences("profile",
					Context.MODE_PRIVATE);
		}
	}

	public void putString(String key, String value) {
		Editor editor = mPref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void putLong(String key, long value) {
		Editor editor = mPref.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public void putInt(String key, int value) {
		Editor editor = mPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void putBoolean(String key, boolean value) {
		Editor editor = mPref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public String getString(String key) {
		return mPref.getString(key, "");
	}

	public long getLong(String key) {
		return mPref.getLong(key, 0);
	}

	public int getInt(String key) {
		return mPref.getInt(key, 0);
	}
	public int getInt(String key ,int defaultValue) {
		return mPref.getInt(key, defaultValue);
	}
	public boolean getBoolean(String key) {
		return mPref.getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaultVal) {
		return mPref.getBoolean(key, defaultVal);
	}

	public boolean contains(String key) {
		return mPref.contains(key);
	}

	public void remove(String key) {
		Editor editor = mPref.edit();
		editor.remove(key);
		editor.commit();
	}

	public void clear() {
		Editor editor = mPref.edit();
		editor.clear();
		editor.commit();
	}
}
