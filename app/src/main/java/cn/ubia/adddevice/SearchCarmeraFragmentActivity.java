package cn.ubia.adddevice;

import java.io.File;
import java.util.Iterator;

import cn.ubia.util.WifiAdmin;
import voice.encoder.DataEncoder;
import voice.encoder.VoicePlayer;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.manager.NotificationTagManager;
import cn.ubia.util.ByteUtil;
import cn.ubia.util.MyVoicePlayer;
import cn.ubia.widget.RoundProgressBar;
import cn.ubia.widget.ScoreAnimationCtrl;

import com.hisilicon.hisilink.MessageSend;
import com.hisilicon.hisilink.OnlineReciever;
import com.hisilicon.hisilinkapi.HisiLibApi;
import com.ubia.IOTC.IRegisterUBIAListener;
import com.ubia.IOTC.WiFiDirectConfig;

public class SearchCarmeraFragmentActivity extends BaseActivity
		implements OnClickListener,
		IRegisterUBIAListener{
    static {
        System.loadLibrary("voiceRecog");
    }
	private String selectUidStr;
	private int pkg;
	private int TIME = 10 ;
	private int i = 0;
	private ImageView back;
	private TextView title;
	private  VoicePlayer player;

	private WifiAdmin wifiAdmin;

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// handler自带方法实现定时器
			try {
				if(open){
					handler.postDelayed(this, TIME);
				}else{
					return;
				}

				i=i+1;

				//onProgressUpdate(i);

				//tvScore.setText(i);
				//System.out.println("do...");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//System.out.println("exception...");
			}
		}
	};



	private static final int ONLINE_MSG_BY_TCP = 1;
	private MessageSend mMessageSend = null;
	private void doHisiCon(){

		HisiLibApi.setNetworkInfo(wifiAdmin.getSecurity(), WiFiDirectConfig.GetSocketSrcPort(), ONLINE_MSG_BY_TCP,
				wifiAdmin.getIPAddress(),ssidStr, key,
				"hi1131s随身拍5388"); //名称暂时这样


		//发送报文
		sendMessage();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message var1) {
			switch (var1.what) {
			case 2:
				processcount++;
				//onProgressUpdate(processcount);
				roundProgressBar.setProgress(processcount);
				//ivRotateCircleProcess.setText(""+ processcount +"%");
				//ivRotateCircleProcess.setVisibility(View.GONE);
				roundProgressBar.setVisibility(View.VISIBLE);
				break;
			case 1:
				//myProgressBar.dismiss();

				Iterator var5 = CameraManagerment.getInstance().CameraList.iterator();
				Log.i("mycamera",
						"camera:" +  CameraManagerment.getInstance(). CameraList.size());
				boolean var7;
				while (true) {
					boolean var6 = var5.hasNext();
					var7 = false;
					if (!var6) {
						break;
					}

					if (selectUidStr.equalsIgnoreCase(((MyCamera) var5.next())
							.getUID())) {
						var7 = true;
						break;
					}
				}



				if (var7) {
					deleteDeviceFromDB(selectUidStr);
					SearchCarmeraFragmentActivity.this.finish();
					Intent intent = new Intent();
					intent.setClass(SearchCarmeraFragmentActivity.this,
							SetupAddDeviceActivity.class);
					// Log.i("fast", "................." + editUID.getText());
					intent.putExtra("selectUID", selectUidStr);
					intent.putExtra("pkg", pkg);
					startActivity(intent);

				} else {
					SearchCarmeraFragmentActivity.this.finish();
					Intent intent = new Intent();
					intent.setClass(SearchCarmeraFragmentActivity.this,
							SetupAddDeviceActivity.class);
					// Log.i("fast", "................." + editUID.getText());
					intent.putExtra("selectUID", selectUidStr);
					intent.putExtra("pkg", pkg);
					startActivity(intent);
				}

				break;
			case 0:


				Intent intent = new Intent();
				intent.setClass(SearchCarmeraFragmentActivity.this,
						SearchCarmeraFragmentFailActivity.class);
				// Log.i("fast", "................." + editUID.getText());

				startActivity(intent);
				finish();

			/*	Log.e("guo...","连接失败。。。。");
				//display.setText("连接失败");
				display.setVisibility(View.VISIBLE);
				mIvFrame.setVisibility(View.GONE);
				display.setText(R.string.page11__p6_fail_note);
				open=false;
				i= 0;
				 processcount = 0;
				 roundProgressBar.setVisibility(View.GONE);
				 ivRotateCircleProcess.setText(""+ processcount +"%");
				mIvRotateCircle.setVisibility(View.GONE);
				ivRotateCircleProcess.setVisibility(View.GONE);
				findViewById(R.id.research).setVisibility(View.VISIBLE);
				findViewById(R.id.ivRotateCircle).setVisibility(View.GONE);
				player.stop();
				  mIvFrameAnim.stop();*/
				break;
			}
		}
	};
	TextView display,tvScore,ivRotateCircleProcess;
	String ssidStr;
	String key;
	int processcount = 0;
	 private ImageView mIvRotateCircle = null;
     protected void onProgressUpdate(Integer... values) {

         if(values.length > 2){
             mIvRotateCircle.setImageResource(R.drawable.winexperience_rotate_circle_nor);
             return;
         }else{
             mIvRotateCircle.setImageResource(R.drawable.winexperience_rotate_circle);
         }

         float fScorePercent = 0f;
         if(values[0] != 0){
             fScorePercent = values[0]/(float)100;
         }

         float fStep = 1/(float)100;
         int nStepInterval = 20;

         if(values.length > 1){
             if(values[1] != 0){
                 nStepInterval = values[1];
             }
         }

         ScoreAnimationCtrl.rotateBgCircle(mIvRotateCircle, fScorePercent, nStepInterval, fStep);
     }
     boolean open=false;
     ImageView mIvFrame;
     /** 帧动画 */
     private AnimationDrawable mIvFrameAnim;
	private RoundProgressBar roundProgressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setTitle(R.string.fragment_playbackactivity_menu_title);
		setContentView(R.layout.search_camera_config_guide);

		mIvFrame = (ImageView) findViewById(R.id.iv_configure_anim);
		mIvFrame.setBackgroundResource(R.drawable.send_sound_wave);
		getActionBar().hide();
		ivRotateCircleProcess=(TextView)findViewById(R.id.ivRotateCircleProcess);
		 display=(TextView)findViewById(R.id.displayQW);
		 mIvRotateCircle = (ImageView) findViewById(R.id.ivRotateCircle);
		 tvScore=(TextView) findViewById(R.id.tvScore);
		 roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);

		findViewById(R.id.btnNo).setOnClickListener(this);
		findViewById(R.id.btnYes).setOnClickListener(this);
		Bundle var2 = this.getIntent().getExtras();
		ssidStr = var2.getString("ssidStr");
		key = var2.getString("key");
		handler.postDelayed(runnable, TIME); //每隔1s执行

		wifiAdmin = new WifiAdmin(this);


		WiFiDirectConfig.registerUBICListener(this);
		WiFiDirectConfig.StartnetConfig("", ssidStr, key,
				120000,1); //isBell 1：hosiCon和声波，
												 // 0：3个都发

		doHisiCon();


		new Thread(new Runnable() {
			@Override
			public void run() {
				//player = new VoicePlayer();
				player = MyVoicePlayer.getInstance();

				int freqs[] = new int[19];
				int baseFreq = 4000;//16000;
				for(int i = 0; i < freqs.length; i ++){
					freqs[i] = baseFreq + i * 150;
				}
				player.setFreqs(freqs);

				Log.e("wifi","StartConfigPlay,,,,"+ssidStr+"["+key+"]");

				String encodeData = DataEncoder.encodeString(getSendData(ssidStr,key));
				Log.e("wifi","StartConfigPlay,,,,End");
				player.play(encodeData,  100, 3000);


			}
		}).start();

		open = true;
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.page31_tips_wifi_connecting));
		this.findViewById(R.id.left_ll).setOnClickListener(this);
		back.setOnClickListener(this);
		new Thread() {
			public void run() {
				while(open){
					 handler.sendEmptyMessage(2);
					try {
						Thread.sleep(1210);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch
						// block
						e.printStackTrace();
					}
				}

			};
		}.start();
		autoSetAudioVolumn();

		mIvFrameAnim = (AnimationDrawable) mIvFrame.getBackground(); // 获取动画内容
		mIvFrameAnim.start();

	}
	private String getSendData(String  ssidStr,String key){

		Log.e( "getSendData ",ssidStr+".."+key);

		 	int index = 0;
		    int ssidlen = (int)ssidStr.length();
		    int keylen = (int)key.length();
		    short socket_src_port = 0;
		    int socket_src_ipaddr = 0;
		    char sonicTokens[] = new char[128];



		    while (socket_src_port == 0) {
		        try {
					Thread.sleep(200);
				} catch (Exception e) {
				}
		        socket_src_port =  WiFiDirectConfig.GetSocketSrcPort();
		        socket_src_ipaddr = WiFiDirectConfig.GetSocketSrcIPAddr();

				Log.e( "getSendData ","socket_src_port.."+socket_src_port+",,,"+socket_src_ipaddr);

		    }

		    //int sonic_token_size = 1 + ssidlen + 1 + keylen + 4 + 8;
		    for(index = 0; index < 128;index++){
		    	sonicTokens[index] = 0;
		    }

		    sonicTokens[0] = (char)(ssidlen + 48); //'0'
		    char ssidarray[] = ssidStr.toCharArray();

		    for (index = 0; index < ssidlen; index++) {
		        sonicTokens[1 + index] = ssidarray[index];
		    }
		    sonicTokens[1 + ssidlen] = (char)(keylen + 48); //'0'

		    if(keylen > 0){
		    	char keyarray[] = key.toCharArray();
		        for (index = 0; index < keylen; index++) {
		            sonicTokens[1 + ssidlen + 1 + index] = keyarray[index];
		        }
		    }
		    String ssidkey = new String(sonicTokens);
	        String content = String.format("%s%04x%08x",ssidkey.trim(),ByteUtil.htons(socket_src_port),ByteUtil.htonl(socket_src_ipaddr));
		    Log.e( "token ",content);
		    return content;
	 }
	   public void autoSetAudioVolumn()
	    {
	        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
	        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(max*0.5), 0);
	}


	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.left_ll:
			finish();
			break;

		case R.id.back:
			finish();
			break;
		case R.id.btnNo:
            finish();
			break;

		case R.id.btnYes:

//			intent = new Intent();
//			// intent.setClass(this , CameraSetupActivity.class);
//			intent.setClass(this, WIfiAddDeviceActivity.class);
//
//			startActivityForResult(intent, 999);

			display.setText(R.string.page31_p6_stage1_note);
			open=true;
			 handler.postDelayed(runnable, TIME); //每隔1s执行
			 mIvRotateCircle.setVisibility(View.VISIBLE);
			 i=0;
			 processcount = 0;
			 roundProgressBar.setVisibility(View.VISIBLE);
			ivRotateCircleProcess.setVisibility(View.GONE);
			findViewById(R.id.research).setVisibility(View.GONE);
			findViewById(R.id.ivRotateCircle).setVisibility(View.VISIBLE);
			WiFiDirectConfig.StartnetConfig("", ssidStr, key,
					120000,1);
			new Thread() {
				public void run() {
					while(open){
						 handler.sendEmptyMessage(2);
						try {
							Thread.sleep(1210);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
						}
					}

				};
			}.start();
			break;

		default:
			break;
		}
	}

	public void onActivityResult(int var1, int var2, Intent var3) {
		super.onActivityResult(var1, var2, var3);
		Log.i("wifi", "SearchCarmeraFragmentActivity onActivityResult:" + var1
				+ "," + var2);

		switch (var1) {
		case -1:

			SearchCarmeraFragmentActivity.this.finish();

			return;
		case 999:
			setResult(999, null);
			SearchCarmeraFragmentActivity.this.finish();

			return;
		default:
			return;
		}

	}
	@Override
	protected void onPause() {
		open = false;
		super.onPause();
	}
	protected void onDestroy() {

		super.onDestroy();
		// if (sendIP != null) {
		// sendIP.stopApp();
		// }
		Log.i("wifi", "WiFiDirectConfig.unregisterUBICListener(this).......");
//		if (myProgressBar != null)
//			myProgressBar.dismiss();
		mMessageSend.stopMultiCast();
		WiFiDirectConfig.StopConfig();
		WiFiDirectConfig.unregisterUBICListener(this);
		player.stop();

		open = false;
		mIvFrameAnim.stop();
		// this.unregisterReceiver(this.resultStateReceiver);
	}
	@Override
	public void CallbackNetconfigStatus(int Success, String uid,int pkg) {
		// TODO Auto-generated method stub
		Log.i("wifi", "CallbackNetconfigStatus:" + Success + ",uid:" + uid+",pkg="+pkg);
		this.pkg = pkg;
		if (!uid.equals("")) {
			selectUidStr = uid;
		} else {
			selectUidStr = "NPYE7VEZRAJNXUM5MNOQ";
		}

		handler.sendEmptyMessage(Success);

	}

	@Override
	public void CallWifiConfigToAddDevice(int success, Bundle date) {
		// TODO Auto-generated method stub

	}

	private void deleteDeviceFromDB(String checkUID){

		MyCamera selectedCamera = (MyCamera)  CameraManagerment.getInstance().getexistCamera(checkUID) ;
		selectedCamera.stop(0);
		selectedCamera.disconnect();
		CameraManagerment.getInstance().CameraList.remove(selectedCamera);
		DatabaseManager var5 = new DatabaseManager(this);
		SQLiteDatabase var6 = var5.getReadableDatabase();
		Cursor var7 = var6.query("snapshot", new String[] { "_id",
				"dev_uid", "file_path", "time" }, "dev_uid = \'"
				+ checkUID + "\'", (String[]) null,
				(String) null, (String) null, "_id LIMIT 4");

		while (var7.moveToNext()) {
			File var8 = new File(var7.getString(2));
			if (var8.exists()) {
				var8.delete();
			}
		}
	CameraManagerment.getInstance().deleteExistCamera(checkUID);
	 NotificationTagManager.getInstance().removeTag(checkUID);
		var7.close();
		var6.close();
		var5.removeSnapshotByUID(checkUID);
		var5.removeDeviceByUID(checkUID);
		DeviceInfo selectedDevice = MainCameraFragment.getexistDevice(checkUID);
		if(selectedDevice!=null )
		MainCameraFragment.DeviceList.remove(selectedDevice);
	}

	/*public void recieveOnlineMessage(){
		OnlineReciever onlineReciever = new OnlineReciever(new OnlineReciever.onOnlineRecievedListener() {
			@Override
			public void onOnlineReceived(String message) {
				Log.e("doHisiCon","message="+message);
				if(message.length()==13)
				{
					handler.sendEmptyMessage(1);
				}
			}
		});
		onlineReciever.start();
	}
*/
	public int sendMessage(){
		//启动线程发送组播消息
		mMessageSend = new MessageSend(this);
		mMessageSend.multiCastThread();
		return 0;
	}
}
