package cn.ubia.adddevice;

//import android.app.Dialog;
//import android.app.AlertDialog.Builder;
//import android.content.ActivityNotFoundException;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.DialogInterface.OnCancelListener;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.Toast;
//import android.widget.CompoundButton.OnCheckedChangeListener;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.ubia.UBell.Manifest;
import cn.ubia.UBell.R;
import cn.ubia.UbiaApplication;
import cn.ubia.base.BaseActivity;
import cn.ubia.widget.DialogUtil;

import static cn.ubia.UbiaApplication.context;

public class AddCarmeraFragmentYesOrNoActivity extends BaseActivity
		implements OnClickListener {
	private ImageView back;
	private Button yesBtn;
	private Timer timer = new Timer();
	private int time = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setup_camera_config_guide);
		getActionBar().hide();
		// findViewById(R.id.rlfast_search).setOnClickListener(this);
		// findViewById(R.id.rlfirst_search).setOnClickListener(this);
		// findViewById(R.id.rllan_serach).setOnClickListener(this);
		// findViewById(R.id.rltwo_search).setOnClickListener(this);
		findViewById(R.id.btnNo).setOnClickListener(this);
		findViewById(R.id.btnYes).setOnClickListener(this);
		yesBtn = (Button) findViewById(R.id.btnYes);
		yesBtn.setText(String.valueOf(time));
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(this);
	 // ((TextView)	findViewById(R.id.title)).setText(getString(R.string.fragment_playbackactivity_menu_title));
		this.findViewById(R.id.left_ll).setOnClickListener(this);

		timer.schedule(task, 1000, 1000);
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					time--;
					yesBtn.setText(String.valueOf(time));
					if (time <= 0) {
						yesBtn.setEnabled(true);
						yesBtn.setBackgroundResource(R.drawable.selector_round_btn);
						yesBtn.setText(R.string.page15_yes_btn);
					}
				}
			});
		}
	};
	@Override
	protected void onResume() {
		super.onResume();
		try {
			UbiaApplication. myCountryCode = "--";
		//	getLngAndLat(this);

			/*if(latitude==0 && longitude==0){
				DialogUtil.getInstance().showDelDialog(this,getString(R.string.request_location_right),getString(R.string.location_right), new DialogUtil.Dialogcallback(){

					@Override
					public void cancel() {

					}

					@Override
					public void commit(String str) {

					}

					@Override
					public void commit() {

					}
				});
			}*/
		}catch (Exception e){
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {

	    		StringBuilder stringBuilder = new StringBuilder();

				try {
					final Geocoder geocoder = new Geocoder(AddCarmeraFragmentYesOrNoActivity.this);
					if(geocoder!=null){
						List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
		//				List<Address> addresses = geocoder.getFromLocationName(name[ii], 1);

						if (addresses!=null && addresses.size() > 0) {
							Address address = addresses.get(0);
							if(address!=null){
								for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
									stringBuilder.append(address.getAddressLine(i)).append("\n");
								}
								stringBuilder.append(address.getLocale().getISO3Country()).append("++");

								stringBuilder.append(address.getCountryName()).append("_");//??
								stringBuilder.append(address.getFeatureName()).append("_");//????
								stringBuilder.append(address.getLocality()).append("_");//?
								stringBuilder.append(address.getPostalCode()).append("_");
								stringBuilder.append(address.getCountryCode()).append("_");//????
								stringBuilder.append(address.getAdminArea()).append("_");//??
								stringBuilder.append(address.getSubAdminArea()).append("_");
								stringBuilder.append(address.getThoroughfare()).append("_");//??
								stringBuilder.append(address.getSubLocality()).append("_");//???
								stringBuilder.append(address.getLatitude()).append("_");//??
								stringBuilder.append(address.getLongitude());//??

								String getAdminAreaString = address.getAdminArea();
								if(getAdminAreaString!=null && getAdminAreaString.contains("香港")|| getAdminAreaString.toLowerCase().trim().contains("hongkong")){
									UbiaApplication.myCountryCode = "HK";
								}else{
									UbiaApplication.myCountryCode = address.getCountryCode();
								}
							}

							System.out.println(stringBuilder.toString()+"getCountryCode:"+UbiaApplication.myCountryCode);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	double latitude = 0.0;
	double longitude = 0.0;
	public String getLngAndLatWithNetwork() {

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(locationManager!=null){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		}
		return longitude + "," + latitude;
	}
	private String getLngAndLat(Context context) {

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(locationManager!=null){
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  //?gps?????
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				} else {//?GPS??????????????????
					return getLngAndLatWithNetwork();
				}
			} else {    //????????
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
				Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				}
			}
		}
		return longitude + "," + latitude;
	}

	LocationListener locationListener = new LocationListener() {

		// Provider??????????????????????????????
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		// Provider?enable??????,??GPS???
		@Override
		public void onProviderEnabled(String provider) {

		}

		// Provider?disable??????,??GPS???
		@Override
		public void onProviderDisabled(String provider) {

		}

		//???????????,??Provider???????,???????
		@Override
		public void onLocationChanged(Location location) {
		}
	};
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.left_ll:
				this.finish();
				break;
			case R.id.back:
				this.finish();
				break;
			case R.id.btnNo:

			intent = new Intent();
			intent.setClass(this, AddCarmeraNoBlueLedFragmentActivity.class);
			intent.putExtra("selectUID", "");

			startActivityForResult(intent, 556);
			break;

		case R.id.btnYes:
			intent = new Intent();
			// intent.setClass(this , CameraSetupActivity.class);
			intent.setClass(this, WIfiAddDeviceActivity.class);

			startActivityForResult(intent, 999);
			break;

		default:
			break;
		}
	}

	public void onActivityResult(int var1, int var2, Intent var3) {
		super.onActivityResult(var1, var2, var3);
		Log.i("wifi", "AddCarmeraFragmentYesOrNoActivity onActivityResult:" + var1
				+ "," + var2);

		switch (var1) {
		case -1:

			AddCarmeraFragmentYesOrNoActivity.this.finish();
			return;
		case 999: 
			setResult(99, null);
			AddCarmeraFragmentYesOrNoActivity.this.finish();
		
			return;
		default:
			return;
		}

	}
}
