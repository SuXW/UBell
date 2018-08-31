package cn.ubia.adddevice;

 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.bean.MyCamera;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.widget.MyProgressBar;

import com.my.IOTC.UBICAPIs;
import com.tutk.IOTC.st_LanSearchInfo2;


public class AddCarmeraLanSearchActivity extends BaseActivity implements
		OnClickListener {
	private ImageView back;
	private TextView title,right_tv;
	st_LanSearchInfo2[] var6=null;
	private final int SEARCH=999;
	private final int LAN_SEARCH=9999;
	ListView listview;
	private MyProgressBar mProgressBar;
	SearchResultListAdapter mListAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.fragment_playbackactivity_menu_title);
		setContentView(R.layout.popo_device_lansearch);
		getActionBar().hide();
		mProgressBar = new MyProgressBar(this);
		mProgressBar.show();
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(this);

		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.page14_lan_serach));
		
		right_tv = (TextView) this.findViewById(R.id.right_tv);
		right_tv.setText(getString(R.string.refresh));
		right_tv.setVisibility(View.VISIBLE);
		right_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread() {
					public void run() {
						 
						var6 = UBICAPIs.IOTC_Lan_Search(new int[1], 3000); 
						handler.sendEmptyMessage(LAN_SEARCH);
						
					};
				}.start();
			}
		});
		this.findViewById(R.id.left_ll).setOnClickListener(this);
		new Thread() {
			public void run() {
				var6 = UBICAPIs.IOTC_Lan_Search(new int[1], 3000); 
				handler.sendEmptyMessage(LAN_SEARCH);
			};
		}.start();
		  mListAdapter = new SearchResultListAdapter(this);
	 
		listview  = (ListView) findViewById(R.id.list);
		listview.setAdapter(mListAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SearchResult var6 = AddCarmeraLanSearchActivity.this.list
						.get(position);
			  
				Intent intent = new Intent();
				intent.setClass(AddCarmeraLanSearchActivity.this,
						LoginAddDeviceActivity.class); 
				intent.putExtra("selectUID", var6.UID);
				intent.putExtra("pkg", var6.pkg); 
				startActivityForResult(intent, 9999);
				
		 
			}
		});
	}
	
	private List<SearchResult> list = new ArrayList<SearchResult>();
	private void addSearchResult(SearchResult mSearchResult){
		boolean contain = false;
		for(int i = 0;i<list.size();i++){
			if(mSearchResult.UID.equals(list.get(i).UID)){
				contain = true;
				break;
			}
		}
		if(!contain){
			list.add(mSearchResult);
		}
	}
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			mListAdapter.notifyDataSetChanged(); 
		  if (msg.what == LAN_SEARCH) {

				mProgressBar.dismiss();
				if (var6 != null && var6.length > 0) {
					int var8 = var6.length;

					for (int var9 = 0; var9 < var8; ++var9) {
						st_LanSearchInfo2 var10 = var6[var9];
						SearchResult result = AddCarmeraLanSearchActivity.this.new SearchResult(
								(new String(var10.UID)).trim(), (new String(
										var10.IP)).trim(), var10.port);
						int pkg = var10.DeviceName[128];
						result.pkg =pkg;
						addSearchResult(result);
						Log.e("","===>>>>IOTC_Lan_Search2:MAC="+ String.format("%02x:%02x:%02x:%02x:%02x:%02x",
								(var10.DeviceName[0]&0xFF),(var10.DeviceName[1]&0xFF),(var10.DeviceName[2]&0xFF),(var10.DeviceName[0]&0xFF)
								,(var10.DeviceName[3]&0xFF),(var10.DeviceName[5]&0xFF)));
					
						Iterator cameraList = CameraManagerment.getInstance().CameraList
								.iterator();
						Log.i("mycamera",
								"camera:" + new String(var10.UID).trim());
						boolean var7;
						while (true) {
							boolean isadded = cameraList.hasNext();
							var7 = false;
							if (!isadded) {
								result.ADDFLAG = "";
								break;
							}

							if (new String(var10.UID).trim().equalsIgnoreCase(
									((MyCamera) cameraList.next()).getUID())) {
								var7 = true;
								result.ADDFLAG = AddCarmeraLanSearchActivity.this.getString(R.string.page35_p10_cell_status); //"已添加"; 
								break;
							}
						}

					}
				}  
				mListAdapter.notifyDataSetChanged(); 
			}
			 
		};
	};
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.left_ll:
				this.finish();
				break;
			case R.id.back:
				this.finish();
				break;
		 
		default:
			break;
		}
	}
	
 
	
	private class SearchResultListAdapter extends BaseAdapter {

		private Context mcontext;

		public SearchResultListAdapter(Context var2) {
			this.mcontext = var2;
		}

		public int getCount() {
			return AddCarmeraLanSearchActivity.this.list.size();
		}

		public Object getItem(int var1) {
			return AddCarmeraLanSearchActivity.this.list.get(var1);
		}

		public long getItemId(int var1) {
			return (long) var1;
		}

		public View getView(int var1, View var2, ViewGroup var3) {
			SearchResult var4 = (SearchResult) this
					.getItem(var1);
			ViewHolder var5;
			if (var2 == null) {
				var2 = LayoutInflater.from(mcontext).inflate(
			                R.layout.search_device_result, null);
			 
				var5 = new ViewHolder();
				var5.uid = (TextView) var2.findViewById(R.id.uid);
				var5.ip = (TextView) var2.findViewById(R.id.ip);
				var5.addflag = (TextView) var2.findViewById(R.id.addflag);
				var2.setTag(var5);
			} else {
				var5 = (ViewHolder) var2.getTag();
			}

			var5.uid.setText(var4.UID);
			var5.ip.setText(var4.IP);
			var5.addflag.setText(var4.ADDFLAG);
			return var2;
		}
	}
	public final class ViewHolder {

		public TextView ip;
		public TextView uid;
		public TextView addflag;

	}
	private class SearchResult {

		public String IP;
		public String UID;
		public String ADDFLAG;
		public int pkg;
		public SearchResult(String var2, String var3, int var4) {
			this.UID = var2;
			this.IP = var3;
		}
	}
	

	public void onActivityResult(int var1, int var2, Intent var3) {
		  
		if (var1 == LAN_SEARCH)
		{
			setResult(SEARCH, null);
			finish();
		}
		

	}
	
}
