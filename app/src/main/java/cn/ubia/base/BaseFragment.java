package cn.ubia.base;









import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;


import cn.ubia.MainActivity;
import cn.ubia.util.ActivityHelper;

import static cn.ubia.UbiaApplication.isPad;


public class BaseFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(!isPad){
			getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}

		super.onCreate(savedInstanceState);
	}

	private ActivityHelper mHelper;

	public ActivityHelper getHelper() {
		if (mHelper == null) {
			mHelper = new ActivityHelper(getActivity());
		}
		return mHelper;
	}

	public void onResume() {
		super.onResume();
		//MobclickAgent.onPageStart(getClass().getSimpleName());
	}

	public void onPause() {
		super.onPause();
		//MobclickAgent.onPageEnd(getClass().getSimpleName());
	}

}
