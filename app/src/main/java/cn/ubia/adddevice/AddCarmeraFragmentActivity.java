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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.zbar.lib.CaptureActivity;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;


public class AddCarmeraFragmentActivity extends BaseActivity implements
		OnClickListener {
	private ImageView back;
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.fragment_playbackactivity_menu_title);
		setContentView(R.layout.setup_camera_config);
		getActionBar().hide();

		findViewById(R.id.btnSetup).setOnClickListener(this);
		findViewById(R.id.btnQRCodeAdd).setOnClickListener(this);	
		findViewById(R.id.btnLanSearch).setOnClickListener(this);	
		findViewById(R.id.btnSetup_ll).setOnClickListener(this);
		findViewById(R.id.btnQRCodeAdd_ll).setOnClickListener(this);	
		findViewById(R.id.btnLanSearch_ll).setOnClickListener(this);	
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(this);
		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.page14_p3_setup_camera));
		this.findViewById(R.id.left_ll).setOnClickListener(this);
		findViewById(R.id.btnHelpMenu).setOnClickListener(this);
	}

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
			case R.id.rlfast_search:
			case R.id.btnSetup:
			case R.id.btnSetup_ll:
				intent = new Intent();
				intent.setClass(this, AddCarmeraFragmentYesOrNoActivity.class);
				intent.putExtra("selectUID", "");

				startActivityForResult(intent, 99);
				break;

			case R.id.btnQRCodeAdd_ll:
			case R.id.btnQRCodeAdd:
				intent = new Intent(getApplicationContext(), CaptureActivity.class);
				startActivityForResult(intent, 100);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// ���붯��
				break;
			case R.id.btnLanSearch_ll:
			case R.id.btnLanSearch:
				intent = new Intent();
				intent.setClass(this, AddCarmeraLanSearchActivity.class);
				this.startActivityForResult(intent, 102);
				break;

			case R.id.btnHelpMenu:
				Uri uri = Uri.parse("http://southerntelecom.com/smartcam");
				Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent1);
				break;
			default:
			break;
		}
	}
	
	public void onActivityResult(int var1, int var2, Intent intent) {
		super.onActivityResult(var1, var2, intent);
		Log.e("AddCam++", "AddCameraFragmentActivity onActivityResult:" + var1 + "," + var2);
	 
			if (var1 == 100 && var2 == -1) {
				Log.e("qrscan", "finished");
				String scanResult = intent.getStringExtra("SCAN_RESULT");
				String[] str =null;
				if (scanResult == null) {
					Bundle bundle = intent.getExtras();
					if (bundle != null) {
						scanResult = bundle.getString("result");
						str  = scanResult.split(":");
					}
					
				}
				
				if (scanResult != null) {

					scanResult = str[0];
			
					Intent i = new Intent();
					i.setClass(getApplicationContext(),
							LoginAddDeviceActivity.class);
					// Log.i("fast", "................." + editUID.getText());
					i.putExtra("selectUID", scanResult);

					if(str!=null && str.length==3){
						i.putExtra("user", str[1]);
						i.putExtra("devpwd", str[2]);
					}
					if(str!=null && str.length> 3){
						i.putExtra("user", str[1]);
						i.putExtra("devpwd", str[2]);
						i.putExtra("installmode", str[3]);
						if(str.length == 6){
							i.putExtra("pkg", str[4]);
							i.putExtra("country", str[5]);
						}

					}
					//startActivity(intent);
					startActivityForResult(i, 101);

					return;
				}
			}else if(var1 == 101){
				Log.e("AddCam++", "from 101 LoginAddDeviceActivity");
				AddCarmeraFragmentActivity.this.finish();
				return;
			}else if(var1 == 102){
				
				Log.e("AddCam++", "from 101 LanSearch");
				AddCarmeraFragmentActivity.this.finish();
			}
			
			switch (var2) {				
			case 999:

				AddCarmeraFragmentActivity.this.finish();

				return;
			case 99:
				AddCarmeraFragmentActivity.this.finish();
				return;

				
			default:
				return;
			}
	 

	}
}
