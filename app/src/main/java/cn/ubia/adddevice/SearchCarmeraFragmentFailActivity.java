package cn.ubia.adddevice;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;

public class SearchCarmeraFragmentFailActivity extends BaseActivity
        implements OnClickListener {

    private TextView title;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setTitle(R.string.page26_page8_connstus_connecting);
        setContentView(R.layout.search_camera_config_guide_fail);
        getActionBar().hide();
        // findViewById(R.id.rlfast_search).setOnClickListener(this);
        // findViewById(R.id.rlfirst_search).setOnClickListener(this);
        // findViewById(R.id.rllan_serach).setOnClickListener(this);
        // findViewById(R.id.rltwo_search).setOnClickListener(this);

        title = (TextView) this.findViewById(R.id.title);
        title.setText(getString(R.string.page26_page8_connstus_connecting));
        findViewById(R.id.btnNo).setOnClickListener(this);
        findViewById(R.id.btnYes).setOnClickListener(this);

        back = (ImageView) this.findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_action_left);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnNo:

                finish();

			/*intent = new Intent();
            intent.setClass(this, AddCarmeraNoBlueLedFragmentActivity.class);
			intent.putExtra("selectUID", "");

			startActivityForResult(intent, 556);*/
                break;

            case R.id.btnYes:
                intent = new Intent();
                // intent.setClass(this , CameraSetupActivity.class);
                intent.setClass(this, WIfiAddDeviceActivity.class);

                startActivityForResult(intent, 999);
                break;
            case R.id.back:

                finish();
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

                SearchCarmeraFragmentFailActivity.this.finish();

                return;
            case 999:
                setResult(99, null);
                SearchCarmeraFragmentFailActivity.this.finish();

                return;
            default:
                return;
        }

    }
}
