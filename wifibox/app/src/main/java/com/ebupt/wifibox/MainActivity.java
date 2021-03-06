package com.ebupt.wifibox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.ebupt.wifibox.databases.GroupMSG;
import com.ebupt.wifibox.device.DeviceFragment;
import com.ebupt.wifibox.group.GroupFragment;
import com.ebupt.wifibox.settings.SettingsFragment;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;


public class MainActivity extends Activity implements View.OnClickListener{

    private GroupFragment groupFragment;
    private DeviceFragment deviceFragment;
    private SettingsFragment settingsFragment;

    private View grouplayout;
    private View devicelayout;
    private View settingslayout;

    private ImageView tab_group;
    private ImageView tab_device;
    private ImageView tab_settings;

    private BootstrapEditText groupDate;


    private FragmentManager fragmentManager;

    private TextView titletext;
    private ImageView titleright;
    private Dialog dialog;
    private MyApp myApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle);
        titletext = (TextView) findViewById(R.id.myTitle);

        myApp = (MyApp) getApplicationContext();

        initViews();
        fragmentManager = getFragmentManager();
        setTabSelection(0);


        IntentFilter login_success = new IntentFilter("login_success");
        registerReceiver(broadcastReceiver, login_success);
        IntentFilter login_error = new IntentFilter("login_error");
        registerReceiver(broadcastReceiver, login_error);
    }

    private void initViews() {
        grouplayout = findViewById(R.id.group_layout);
        devicelayout = findViewById(R.id.device_layout);
        settingslayout = findViewById(R.id.settings_layout);

        tab_group = (ImageView) findViewById(R.id.group_image);
        tab_device = (ImageView) findViewById(R.id.device_image);
        tab_settings = (ImageView) findViewById(R.id.settings_image);

        grouplayout.setOnClickListener(this);
        devicelayout.setOnClickListener(this);
        settingslayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_layout:
                setTabSelection(0);
                break;
            case R.id.device_layout:
                setTabSelection(1);
                break;
            case R.id.settings_layout:
                setTabSelection(2);
                break;
            default:
                break;
        }
    }

    private void clearSelection() {
        tab_group.setImageResource(R.drawable.tab_group);
        tab_device.setImageResource(R.drawable.tab_device);
        tab_settings.setImageResource(R.drawable.tab_settings);
    }

    private void setTabSelection(int index) {
        clearSelection();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        switch (index) {
            case 0:
                tab_group.setImageResource(R.drawable.tab_group_hot);
                if (groupFragment == null) {
                    groupFragment = new GroupFragment();
                    transaction.add(R.id.content, groupFragment);
                } else {
                    transaction.show(groupFragment);
                }
                titletext.setText("团管理");
                titleright = (ImageView) findViewById(R.id.myDetail);
                titleright.setVisibility(View.VISIBLE);
                titleright.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog();
                    }
                });
                break;
            case 1:
                tab_device.setImageResource(R.drawable.tab_device_hot);
                if (deviceFragment == null) {
                    deviceFragment = new DeviceFragment();
                    transaction.add(R.id.content, deviceFragment);
                } else {
                    transaction.show(deviceFragment);
                }
                titleright.setVisibility(View.GONE);
                titletext.setText("设备");
                break;
            case 2:
            default:
                tab_settings.setImageResource(R.drawable.tab_settings_hot);
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                    transaction.add(R.id.content, settingsFragment);
                } else {
                    transaction.show(settingsFragment);
                }
                titleright.setVisibility(View.GONE);
                titletext.setText("设置");
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (groupFragment != null) {
            transaction.hide(groupFragment);
        }
        if (deviceFragment != null) {
            transaction.hide(deviceFragment);
        }
        if (settingsFragment != null) {
            transaction.hide(settingsFragment);
        }
    }


    private void showDialog() {

        LayoutInflater inflaterDI = LayoutInflater.from(MainActivity.this);
        LinearLayout layout = (LinearLayout) inflaterDI.inflate(R.layout.add_group_layout, null);
        dialog = new AlertDialog.Builder(MainActivity.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        final BootstrapEditText groupName = (BootstrapEditText) layout.findViewById(R.id.add_group_name);
        groupDate = (BootstrapEditText) layout.findViewById(R.id.add_group_calendar);

        final BootstrapEditText groupCount = (BootstrapEditText) layout.findViewById(R.id.add_group_count);

        View.OnClickListener dateBtnListener =
                new BtnOnClickListener(1);

        ImageView calendar = (ImageView) layout.findViewById(R.id.add_group_calendar_img);
        calendar.setOnClickListener(dateBtnListener);

        Button groupOk = (Button) layout.findViewById(R.id.add_group_ok);
        groupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupName.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "请输入团名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (groupDate.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "请输入出发时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (groupCount.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "请输入参团人数", Toast.LENGTH_SHORT).show();
                }
                GroupMSG groupMSG = new GroupMSG();
                groupMSG.setGroup_name(groupName.getText().toString());
                groupMSG.setGroup_date(groupDate.getText().toString());
                groupMSG.setGroup_count(groupCount.getText().toString());

                StringBuffer str = new StringBuffer("");

                str.append(myApp.phone);
                str.append(groupDate.getText().toString().replaceAll("-", ""));
                str.append(getrandom());
                Log.e("xxx", str.toString());
                groupMSG.setGroup_id(str.toString());

                groupMSG.saveThrows();

                Toast.makeText(MainActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("addGroup");
                sendBroadcast(intent);
                dialog.hide();
            }
        });

        Button groupCancel = (Button) layout.findViewById(R.id.add_group_cancel);
        groupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });


    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("login_success")) {
                Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
            }
            if (intent.getAction().equals("login_error")) {
                Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected Dialog onCreateDialog(int id) {
        //用来获取日期和时间的
        Calendar calendar = Calendar.getInstance();

        Dialog dialog = null;
        switch(id) {
            case 1:
                DatePickerDialog.OnDateSetListener dateListener =
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker,
                                                  int year, int month, int dayOfMonth) {
                                StringBuffer str = new StringBuffer("");
                                str.append(year);
                                str.append("-");
                                str.append(month + 1);
                                str.append("-");
                                str.append(dayOfMonth);
                                groupDate.setText(str.toString());
                            }
                        };
                dialog = new DatePickerDialog(this,
                        dateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                break;
            default:
                break;
        }
        return dialog;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private class BtnOnClickListener implements View.OnClickListener {

        private int dialogId = 0;	//默认为0则不显示对话框

        public BtnOnClickListener(int dialogId) {
            this.dialogId = dialogId;
        }
        @Override
        public void onClick(View view) {
            showDialog(dialogId);
        }

    }

    private int getrandom() {
        Random ran=new Random();
        int r = 0;
        m1:while(true){
            int n=ran.nextInt(10000);
            r=n;
            int[] bs=new int[4];
            for(int i=0;i<bs.length;i++){
                bs[i]=n%10;
                n/=10;
            }
            Arrays.sort(bs);
            for(int i=1;i<bs.length;i++){
                if(bs[i-1]==bs[i]){
                    continue m1;
                }
            }
            break;
        }
        return r;
    }

}
