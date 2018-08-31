package cn.ubia.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.ubia.LiveViewGLviewActivity;
import cn.ubia.UbiaApplication;
import cn.ubia.UBell.R;
import cn.ubia.adddevice.SetupAddDeviceActivity;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.UbiaUtil;
import cn.ubia.widget.EditTextDrawable.DrawableListener;

import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.Camera;

import java.util.TimeZone;

/**
 * @author 作者  : WISE
 * @version 创建时间：2017/9/19  下午2:59
 * @description 类描述：
 * @project 项目： HomeCloud
 * @天天都是星期一
 */


public class DialogUtil {
    static DialogUtil mDialogUtil;

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    PopupWindow popupWindow = null;

    public interface Dialogcallback {
        void cancel();

        void commit(String str);

        void commit();
    }

    public interface DialogChooseItemcallback {
        void chooseItem(int chooseItem);
    }

    public interface DialogChooseItemStringcallback {
        void chooseItemString(String chooseItem);
    }

    public interface DialogChooseCloudSaveDateCallback {
        void chooseDate(String date);
    }

    public synchronized static DialogUtil getInstance() {

        if (null == mDialogUtil) {
            synchronized (DialogUtil.class) {
                if (null == mDialogUtil) {
                    mDialogUtil = new DialogUtil();
                }
            }
        }
        return mDialogUtil;
    }

    public void hidePopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void onKeyDown(int var1, KeyEvent var2) {
        if (var1 == 4) {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        }
    }

    public void showDeleteCloudDialog(final Context mContext,final  DialogChooseItemcallback mDialogcallback){
        final Dialog dialog = new Dialog(mContext, R.style.Theme_Transparent);

        final View view = dialog.getLayoutInflater().inflate(R.layout.dialog_delete_cloud, null);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_delete_all = (TextView) view.findViewById(R.id.all_btn);
        text_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDelDialog(mContext,mContext.getString( R.string.page26_ctxRemoveCamera)+"", mContext.getString( R.string.delete_file_tip)+"",
                        new DialogUtil.Dialogcallback(){

                            @Override
                            public void cancel() {
                            }

                            @Override
                            public void commit(String str) {
                            }

                            @Override
                            public void commit() {
                                mDialogcallback.chooseItem(0);
                            }

                        });
            }
        });

        TextView text_delete_day = (TextView) view.findViewById(R.id.day_btn);
        text_delete_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDelDialog(mContext,mContext.getString( R.string.page26_ctxRemoveCamera)+"", mContext.getString( R.string.delete_file_tip)+"",
                        new DialogUtil.Dialogcallback(){

                            @Override
                            public void cancel() {
                            }

                            @Override
                            public void commit(String str) {
                            }

                            @Override
                            public void commit() {
                                mDialogcallback.chooseItem(1);
                            }

                        });
            }
        });



        TextView cancel = (TextView) view.findViewById(R.id.cancel_btn);

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.setContentView(view);
        dialog.show();

        Window window = dialog.getWindow();

        WindowManager.LayoutParams lp = window.getAttributes();
        window.getDecorView().setPadding(100, 0, 100, 0);
        lp.gravity = Gravity.CENTER;
        lp.width =  ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);



    }

    public void showNamePWdDialo(final Context mcontext, final Camera mCamera, String titleStr, final Dialogcallback mDialogcallback_Name, final Dialogcallback mDialogcallback_pwd) {

        View mView = LayoutInflater.from(mcontext).inflate(
                R.layout.popo_device_nameinfo_setting, null);
        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        this.popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setContentView(mView);
        ImageView back = (ImageView) mView.findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_action_left);
        back.setVisibility(View.VISIBLE);
        TextView title = (TextView) mView.findViewById(R.id.title);
        title.setText("" + titleStr);
        final EditText setting_devicename_et = (EditText) mView.findViewById(R.id.setting_devicename_et);
        final EditText setting_newpwd_et = (EditText) mView.findViewById(R.id.setting_newpwd_et);
        final EditText setting_confimpwd_et = (EditText) mView.findViewById(R.id.setting_confimpwd_et);

        if (mDialogcallback_pwd == null) {

            mView.findViewById(R.id.btnok).setVisibility(View.GONE);
            mView.findViewById(R.id.pw1_rl).setVisibility(View.GONE);
            mView.findViewById(R.id.pw2_rl).setVisibility(View.GONE);
        }


        mView.findViewById(R.id.btnok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (setting_newpwd_et.getText().toString().equals("") || setting_newpwd_et.getText().toString().equals("")) {

                    Toast.makeText(mcontext, mcontext.getText(R.string.page1_pass_null), 0).show();
                    return;
                }

                if(setting_newpwd_et.getText().toString().contains(":")){
                    Toast.makeText(mcontext,R.string.password_illegal,Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!setting_confimpwd_et.getText().toString().equals(setting_newpwd_et.getText().toString())) {

                    Toast.makeText(mcontext, mcontext.getText(R.string.page12_p11_confirmpassword_toast), 0).show();
                    return;
                }
                if (setting_newpwd_et.getText().toString().getBytes().length > 12) {

                    Toast.makeText(mcontext, mcontext.getText(R.string.page13_tips_key_length), 0).show();
                    return;
                }


                if (mDialogcallback_pwd != null) {
                    mDialogcallback_pwd.commit(setting_confimpwd_et.getText().toString());
                }
            }
        });


        mView.findViewById(R.id.btnokName).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDialogcallback_Name != null) {
                    if (setting_devicename_et.getText().toString().equals("")) {

                        Toast.makeText(mcontext, mcontext.getText(R.string.page12_p11_name_new_hit), 0).show();
                        return;
                    }
                    mDialogcallback_Name.commit(setting_devicename_et.getText() + "");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(mView.findViewById(R.id.title), Gravity.TOP, 0, 0);

    }

    public void showpwdErrorDialo(final Context mcontext, final DialogChooseItemStringcallback mDialogChooseItemStringcallback,final LiveViewGLviewActivity activity) {
        View mView = LayoutInflater.from(mcontext).inflate(
                R.layout.popo_device_pwd_error_setting, null);

        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        this.popupWindow = popupWindow;

        final EditTextDrawable setting_newpwd_et = (EditTextDrawable) mView.findViewById(R.id.setting_newpwd_et);
        this.devpwd = setting_newpwd_et;
        this.devpwd.setDrawableListener(drawableListener);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setContentView(mView);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(activity != null){
                    if(devpwd.getText().toString().length()==0){
                        activity.finish();
                    }
                }
            }
        });


        showpsdOn = mcontext.getResources().getDrawable(
                R.drawable.add_icon_seen_press);
        showpsdOff = mcontext.getResources().getDrawable(
                R.drawable.add_icon_seen);
        showpsdOn.setBounds(0, 0, showpsdOn.getIntrinsicWidth(),
                showpsdOn.getIntrinsicHeight());
        showpsdOff.setBounds(0, 0, showpsdOff.getIntrinsicWidth(),
                showpsdOff.getIntrinsicHeight());
        mView.findViewById(R.id.btnok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (setting_newpwd_et.getText().toString().equals("") || setting_newpwd_et.getText().toString().equals("")) {

                    Toast.makeText(mcontext, mcontext.getText(R.string.page1_pass_null), 0).show();
                    return;
                }


                if (setting_newpwd_et.getText().toString().getBytes().length > 12) {

                    Toast.makeText(mcontext, mcontext.getText(R.string.page13_tips_key_length), 0).show();
                    return;
                }


                if (mDialogChooseItemStringcallback != null) {
                    mDialogChooseItemStringcallback.chooseItemString(setting_newpwd_et.getText().toString());
                    popupWindow.dismiss();
                }
            }
        });

        ImageView back = (ImageView) mView.findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_action_left);
        back.setVisibility(View.VISIBLE);
        TextView title = (TextView) mView.findViewById(R.id.title);
        title.setText("" + mcontext.getText(R.string.page10_tips_wifi_wrongpassword));
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        popupWindow.showAtLocation(mView.findViewById(R.id.title), Gravity.TOP, 0, 0);

    }

    public void showChoseNameDialo(final Context mcontext, final DialogChooseItemStringcallback mDialogChooseItemStringcallback) {
        View mView = LayoutInflater.from(mcontext).inflate(
                R.layout.popo_setup_device_showname, null);
        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(mView);
        this.popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        mView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        mView.findViewById(R.id.sel1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialogChooseItemStringcallback.chooseItemString(mcontext.getString(R.string.Frontdoor));
                popupWindow.dismiss();
            }
        });

        mView.findViewById(R.id.sel2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialogChooseItemStringcallback.chooseItemString(mcontext.getString(R.string.Livingroom));

                popupWindow.dismiss();
            }
        });

        mView.findViewById(R.id.sel3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialogChooseItemStringcallback.chooseItemString(mcontext.getString(R.string.Backdoor));

                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(mView.findViewById(R.id.device_set_root), Gravity.TOP, 0, 0);

    }

    private boolean isCloudSave;

    public void showformatSDDialo(final Context mcontext, final Camera mCamera, String titleStr, int total, int free, int firmwareVersionP, final int dstenable, final Dialogcallback mDialogcallback_) {
        isCloudSave = mcontext.getSharedPreferences("isCloudSave", Context.MODE_PRIVATE).getBoolean(mCamera.getmDevUID(), false);
        final View mView = LayoutInflater.from(mcontext).inflate(
                R.layout.popo_device_sd_setting, null);
        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(mView);
        this.popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        ImageView back = (ImageView) mView.findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_action_left);
        back.setVisibility(View.VISIBLE);
        TextView title = (TextView) mView.findViewById(R.id.title);
        title.setText("" + titleStr);

        TextView setting_devicetotal_tv = (TextView) mView.findViewById(R.id.setting_devicetotal_tv);
        TextView setting_devicefree_tv = (TextView) mView.findViewById(R.id.setting_devicefree_tv);
        if(total/1024>128 || total < 0){
            setting_devicetotal_tv.setText(R.string.setting_sdcord_noformat);
            setting_devicefree_tv.setText(R.string.setting_sdcord_noformat);
        }else{
            float f1 = (float)total/(float)1024;
            float f2 = (float)free/(float)1024;
            setting_devicetotal_tv.setText(String.format("%.2f", f1)+" GB");
            setting_devicefree_tv.setText(String.format("%.2f", f2)+" GB");
        }


        if (firmwareVersionP < 100) {
            mView.findViewById(R.id.cloud_save_rlayout).setVisibility(View.GONE);
        }

        final ImageView cloud_save_switch_img = (ImageView) mView.findViewById(R.id.cloud_save_switch_img);
        final LinearLayout sdcard_save_llayout = (LinearLayout) mView.findViewById(R.id.sdcard_save_llayout);
        if (isCloudSave) {
            cloud_save_switch_img.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.live_btn_switch_on));
            sdcard_save_llayout.setVisibility(View.VISIBLE);
        } else {
            cloud_save_switch_img.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.live_btn_switch_off));
            sdcard_save_llayout.setVisibility(View.VISIBLE);
        }

        cloud_save_switch_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCloudSave = !isCloudSave;
                TimeZone tz = TimeZone.getDefault();
                int offset = tz.getOffset(System.currentTimeMillis()) / (3600 * 1000);
                if (isCloudSave) {
                    cloud_save_switch_img.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.live_btn_switch_on));
                    sdcard_save_llayout.setVisibility(View.VISIBLE);
                    if (mCamera != null) {
                        if (mCamera != null) {

//    						CameraManagerment.getInstance().userIPCSetTimeZone(mCamera.getmDevUID(), 268,
//    								dstenable,gx
//    								offset);
                            //打开云存储时设置时区，无需单独设置了
                            CameraManagerment.getInstance().userIPC_Cloud_SetReq(mCamera.getmDevUID(), 1, dstenable, offset);
                        }
                    }
                } else {
                    CameraManagerment.getInstance().userIPC_Cloud_SetReq(mCamera.getmDevUID(), 0, dstenable, offset);
                    cloud_save_switch_img.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.live_btn_switch_off));
                    sdcard_save_llayout.setVisibility(View.VISIBLE);
                }
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("isCloudSave", Context.MODE_PRIVATE).edit();
                editor.putBoolean(mCamera.getmDevUID(), isCloudSave);
                editor.commit();
            }
        });

        mView.findViewById(R.id.btnok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCamera.sendIOCtrl(
                        0,
                        896,
                        AVIOCTRLDEFs.SMsgAVIoctrlFormatExtStorageReq
                                .parseContent(0));
            }
        });


        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                mDialogcallback_.cancel();
            }
        });

        popupWindow.showAtLocation(mView.findViewById(R.id.title), Gravity.TOP, 0, 0);

    }

    private int px2dip(float pxValue) {
        final float scale = UbiaApplication.getInstance().getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



    public void showChoseDormancyTimeDialo(final  Context mcontext, String[] choseString, int choseItem, String titlesTr, final DialogChooseItemcallback mDialogChooseItemStringcallback) {
        View mView = LayoutInflater.from(mcontext).inflate(
                R.layout.popo_device_setting, null);
        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(mView);
        this.popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        ImageView back = (ImageView) mView.findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_action_left);
        back.setVisibility(View.VISIBLE);
        TextView title = (TextView) mView.findViewById(R.id.title);
        title.setText("" + titlesTr);
        ListAdapter mListAdapter = new ListAdapter();
        mListAdapter.setData(choseString, choseItem,false,mcontext);
        ListView list = (ListView) mView.findViewById(R.id.list);
        list.setAdapter(mListAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                if(position == 3){
                    DialogUtil.getInstance().showDelDialog(mcontext,mcontext.getString( R.string.setting_dormancytime)+"", mcontext.getString( R.string.setting_dormancytime_tip)+"",
                            new Dialogcallback(){

                                @Override
                                public void cancel() {

                                }

                                @Override
                                public void commit(String str) {
                                }

                                @Override
                                public void commit() {
                                    if (mDialogChooseItemStringcallback != null) {
                                        mDialogChooseItemStringcallback.chooseItem(position);
                                    }
                                    popupWindow.dismiss();
                                }

                            });
                }else{
                    if (mDialogChooseItemStringcallback != null) {
                        mDialogChooseItemStringcallback.chooseItem(position);
                    }
                    popupWindow.dismiss();
                }


            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(mView.findViewById(R.id.title),
                Gravity.TOP, 0, 0);
    }

    public void showChosePiviewDialo(Context mcontext, String[] choseString, int choseItem, String titlesTr, final DialogChooseItemcallback mDialogChooseItemStringcallback) {

        View mView = LayoutInflater.from(mcontext).inflate(
                R.layout.popo_device_setting, null);
        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(mView);
        this.popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        ImageView back = (ImageView) mView.findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_action_left);
        back.setVisibility(View.VISIBLE);
        TextView title = (TextView) mView.findViewById(R.id.title);
        title.setText("" + titlesTr);
        ListAdapter mListAdapter = new ListAdapter();
        mListAdapter.setData(choseString, choseItem,false,mcontext);
        ListView list = (ListView) mView.findViewById(R.id.list);
        list.setAdapter(mListAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mDialogChooseItemStringcallback != null) {
                    mDialogChooseItemStringcallback.chooseItem(position);
                }
                popupWindow.dismiss();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(mView.findViewById(R.id.title),
                Gravity.TOP, 0, 0);

    }

    int chooseItemback = 0;

    public void showChoseControyPiviewDialo(final Context mcontext, final String[] choseString, final int choseItem, String titlesTr, final DialogChooseItemcallback mDialogChooseItemStringcallback,View v) {
        chooseItemback = choseItem;
        final View mView = LayoutInflater.from(mcontext).inflate(
                R.layout.popo_device_setting_chose_controy, null);
        final PopupWindow popupWindow;
        popupWindow = new PopupWindow(mView, v.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(mView);
        this.popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
       /* ImageView back = (ImageView) mView.findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_action_left);
        back.setVisibility(View.VISIBLE);
         back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mDialogChooseItemStringcallback != null) {
                    mDialogChooseItemStringcallback.chooseItem(chooseItemback);
                }
                popupWindow.dismiss();
            }
        });
        TextView title = (TextView) mView.findViewById(R.id.title);
        title.setText("" + titlesTr);*/
        final ListAdapter mListAdapter = new ListAdapter();
        mListAdapter.setData(choseString, choseItem,true,mcontext);
        ListView list = (ListView) mView.findViewById(R.id.list);
        list.setAdapter(mListAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                chooseItemback = position;
                mDialogChooseItemStringcallback.chooseItem(position);
                mListAdapter.setData(choseString, position,true,mcontext);
                mListAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });

        mView.findViewById(R.id.btnok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chooseItemback == -1) {
                    Toast.makeText(
                            mcontext,
                            mcontext.getString(R.string.select_server), Toast.LENGTH_SHORT).show();
                    //    Log.i("IOTCamera", "请选择一个服务器 。。。。。");
                    return;
                }

                String[] countryArray = mcontext.getResources().getStringArray(R.array.controy_array);

                if (countryArray[chooseItemback]
                        .equals(mcontext.getResources().getString(R.string.China))) { //选择中国
                    if (UbiaUtil.getCurrentTimeZone().contains("+8:00")) { //8时区
                        if (mDialogChooseItemStringcallback != null) {
                            mDialogChooseItemStringcallback.chooseItem(chooseItemback);
                        }
                        popupWindow.dismiss();
                    } else {
                        if (mDialogChooseItemStringcallback != null) {
                            mDialogChooseItemStringcallback.chooseItem(chooseItemback);
                        }
                        popupWindow.dismiss();
                       /* DialogUtil.getInstance().showTimeZoneErrDialog(mcontext, "提示", "时区不对", new DialogUtil.Dialogcallback() {
                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void commit(String str) {

                            }

                            @Override
                            public void commit() {
                                if (mDialogChooseItemStringcallback != null) {
                                    mDialogChooseItemStringcallback.chooseItem(chooseItemback);
                                }
                                popupWindow.dismiss();
                                Log.e("时区,", "中国时区。。。。。。。。。。。。");
                            }
                        });*/
                    }
                }/*else if(countryArray[chooseItemback]
                        .equals(mcontext.getResources().getString(R.string.TaiWan))){ //选择台湾 ,实际美国

                    for(int i = 0;i<countryArray.length;i++){
                        countryArray[i].equals(mcontext.getResources().getString(R.string.usa));
                        chooseItemback = i;
                        break;
                    }

                    if (mDialogChooseItemStringcallback != null) {
                        mDialogChooseItemStringcallback.chooseItem(chooseItemback);
                    }
                    popupWindow.dismiss();

                }else if(countryArray[chooseItemback]
                        .equals(mcontext.getResources().getString(R.string.other))){ //选择其他 ,实际美国

                    for(int i = 0;i<countryArray.length;i++){
                        countryArray[i].equals(mcontext.getResources().getString(R.string.usa));
                        chooseItemback = i;
                        break;
                    }

                    if (mDialogChooseItemStringcallback != null) {
                        mDialogChooseItemStringcallback.chooseItem(chooseItemback);
                    }
                    popupWindow.dismiss();

                } */ else {
                    if (mDialogChooseItemStringcallback != null) {
                        mDialogChooseItemStringcallback.chooseItem(chooseItemback);
                    }
                    popupWindow.dismiss();
                }
            }
        });
      /*  popupWindow.showAtLocation(mView.findViewById(R.id.title),
                Gravity.TOP, 0, 0);*/
        popupWindow.showAsDropDown(v);
    }


    public void showAdvancedTextTipDialog(Context mcontext, String title,String cancelText, final Dialogcallback mDialogcallback,int textColor,int align) {
        final Dialog dialog = new Dialog(mcontext, R.style.Theme_Transparent);
        final View view = dialog.getLayoutInflater().inflate(R.layout.dialog_text_tip, null);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_title = (TextView) view.findViewById(R.id.text_title);
        text_title.setGravity(align);
        text_title.setText("" + title);
        text_title.setTextColor(textColor);
        TextView cancel = (TextView) view.findViewById(R.id.cancel_btn);
        cancel.setText(cancelText);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mDialogcallback != null)
                    mDialogcallback.cancel();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    public void showTextTipDialog(Context mcontext, String title,String cancelText, final Dialogcallback mDialogcallback) {
        final Dialog dialog = new Dialog(mcontext, R.style.Theme_Transparent);
        final View view = dialog.getLayoutInflater().inflate(R.layout.dialog_text_tip, null);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_title = (TextView) view.findViewById(R.id.text_title);
        text_title.setGravity(Gravity.LEFT);
        text_title.setPadding(50,0,0,0);
        text_title.setText("" + title);
        TextView cancel = (TextView) view.findViewById(R.id.cancel_btn);
        cancel.setText(cancelText);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mDialogcallback != null)
                    mDialogcallback.cancel();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    public void showEditTipDialog(Context mcontext, String title, final Dialogcallback mDialogcallback) {
        final Dialog dialog = new Dialog(mcontext, R.style.Theme_Transparent);
        final View view = dialog.getLayoutInflater().inflate(R.layout.dialog_edit_tip, null);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_title = (TextView) view.findViewById(R.id.text_title);
        final EditText passwordEdit = (EditText) view.findViewById(R.id.edit_view);

        TextView cancel = (TextView) view.findViewById(R.id.cancel_btn);
        TextView ok = (TextView) view.findViewById(R.id.comfirm_btn);
        text_title.setText("" + title);

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mDialogcallback != null)
                    mDialogcallback.cancel();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {

                                  @Override
                                  public void onClick(View v) {
                                      dialog.dismiss();
                                      if (mDialogcallback != null)
                                          mDialogcallback.commit(passwordEdit.getText().toString());

                                  }


                              }

        );

        dialog.setContentView(view);
        dialog.show();

        passwordEdit.setFocusable(true);
        passwordEdit.requestFocus();
        passwordEdit.setFocusableInTouchMode(true);
    }

    public void showTimeZoneErrDialog(Context mcontext, String title, String showContext, final Dialogcallback mDialogcallback) {
        final Dialog dialog = new Dialog(mcontext, R.style.Theme_Transparent);
        final View view = dialog.getLayoutInflater().inflate(R.layout.dialog_device_del, null);
        dialog.setCanceledOnTouchOutside(false);
        TextView tv1 = (TextView) view.findViewById(R.id.comfirm_del_device_content);
        TextView tv2 = (TextView) view.findViewById(R.id.comfirm_del_device_content2);
        TextView tv3 = (TextView) view.findViewById(R.id.comfirm_del_device_content3);
        TextView cancel = (TextView) view.findViewById(R.id.comfirm_del_device_cancel);
        TextView ok = (TextView) view.findViewById(R.id.comfirm_del_device_comfirm);
        tv1.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);
        tv3.setVisibility(View.VISIBLE);

        TextView text_title = (TextView) view.findViewById(R.id.text_title);
        text_title.setText("" + title);
        if (title.equals("")) {
            text_title.setVisibility(View.GONE);
        }
        tv3.setText("" + showContext);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mDialogcallback != null)
                    mDialogcallback.cancel();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {

                                  @Override
                                  public void onClick(View v) {
                                      dialog.dismiss();
                                      if (mDialogcallback != null)
                                          mDialogcallback.commit();

                                  }


                              }

        );

        dialog.setContentView(view);
        dialog.show();
    }

    public void showDelDialog(Context mcontext, String title, String showContext, final Dialogcallback mDialogcallback) {

        final Dialog dialog = new Dialog(mcontext, R.style.Theme_Transparent);
        final View view = dialog.getLayoutInflater().inflate(R.layout.dialog_device_del, null);
        dialog.setCanceledOnTouchOutside(false);
        TextView tv1 = (TextView) view.findViewById(R.id.comfirm_del_device_content);
        TextView tv2 = (TextView) view.findViewById(R.id.comfirm_del_device_content2);
        TextView tv3 = (TextView) view.findViewById(R.id.comfirm_del_device_content3);
        TextView cancel = (TextView) view.findViewById(R.id.comfirm_del_device_cancel);
        TextView ok = (TextView) view.findViewById(R.id.comfirm_del_device_comfirm);
        tv1.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);
        tv3.setVisibility(View.VISIBLE);

        TextView text_title = (TextView) view.findViewById(R.id.text_title);
        text_title.setText("" + title);
        if (title.equals("")) {
            text_title.setVisibility(View.GONE);
        }
        tv3.setText("" + showContext);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mDialogcallback != null)
                    mDialogcallback.cancel();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {

                                  @Override
                                  public void onClick(View v) {
                                      dialog.dismiss();
                                      if (mDialogcallback != null)
                                          mDialogcallback.commit();

                                  }


                              }

        );

        dialog.setContentView(view);
        dialog.show();
    }

    class ListAdapter extends BaseAdapter {
        String[] choseString;
        int sel;
        boolean isHighlight;
        Context context;
        public void setData(String[] mchoseString, int sel ,boolean isHighlight, Context context) {
            choseString = mchoseString;
            this.sel = sel;
            this.isHighlight = isHighlight;
            this.context = context;
        }

        @Override
        public int getCount() {
            return choseString == null ? 0 : choseString.length;
        }

        @Override
        public Object getItem(int position) {
            return choseString[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            timezoneViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new timezoneViewHolder();
                convertView = View.inflate(UbiaApplication.getInstance().getApplicationContext(),
                        R.layout.select_setting_item, null);
                viewHolder.setting_item_tv = (TextView) convertView
                        .findViewById(R.id.setting_item_tv);
                viewHolder.setting_item_iv = (ImageView) convertView
                        .findViewById(R.id.setting_item_iv);

                if(isHighlight){
                    viewHolder.setting_item_tv.setTextColor(context.getResources().getColor(R.color.red));
                }

                convertView.setBackgroundResource(R.color.color_bkground_none_color);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (timezoneViewHolder) convertView.getTag();
            }
            final String textName = choseString[position];
            viewHolder.setting_item_tv.setText("" + textName);


            if (position == sel) {
                viewHolder.setting_item_iv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.setting_item_iv.setVisibility(View.GONE);
            }
            return convertView;
        }

        class timezoneViewHolder {
            TextView setting_item_tv;
            ImageView setting_item_iv;

        }
    }


    private Drawable showpsdOn;
    private Drawable showpsdOff;
    private boolean flag_showpsd = false;
    EditTextDrawable devpwd;
    DrawableListener drawableListener = new DrawableListener() {

        @Override
        public void onRight() {
            flag_showpsd = !flag_showpsd;
            toggleShowpsd();
        }

        @Override
        public void onLeft() {
        }
    };

    private void toggleShowpsd() {
        if (flag_showpsd) {
            devpwd.setCompoundDrawables(devpwd.getCompoundDrawables()[0],
                    devpwd.getCompoundDrawables()[1], showpsdOn,
                    devpwd.getCompoundDrawables()[3]);
            devpwd.setTransformationMethod(HideReturnsTransformationMethod
                    .getInstance());
        } else {
            devpwd.setCompoundDrawables(devpwd.getCompoundDrawables()[0],
                    devpwd.getCompoundDrawables()[1], showpsdOff,
                    devpwd.getCompoundDrawables()[3]);
            devpwd.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
        }
        devpwd.setSelection(devpwd.getText().length());
    }
}
