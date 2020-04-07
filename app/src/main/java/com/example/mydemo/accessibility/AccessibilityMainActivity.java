package com.example.mydemo.accessibility;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import com.example.mydemo.R;
import com.example.mydemo.util.AccessibilityLog;

public class AccessibilityMainActivity extends Activity implements View.OnClickListener {

    private View mOpenSetting;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_main);
        initView();
        AccessibilityOperator.getInstance().init(this);
    }

    private void initView() {
        mOpenSetting = findViewById(R.id.open_accessibility_setting);
        mOpenSetting.setOnClickListener(this);
        findViewById(R.id.accessibility_find_and_click).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.open_accessibility_setting:
                AccessibilityLog.printLog("open_accessibility_setting");
                OpenAccessibilitySettingHelper.jumpToSettingPage(this);
                break;
            case R.id.accessibility_find_and_click:
                dealStartActivity();
                doMasterClear();
                //startActivity(new Intent(this, AccessibilityNormalSample.class));
                break;
        }
    }

    private void dealStartActivity(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(AccessibilityMainActivity.this)){
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        }else {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
        }
    }

    private void doMasterClear() {   //  重点是这个方法，其实是发送了一个广播。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //在android8.0上加上intent.setPackage("android");才成功恢复出厂设置
            intent =new  Intent("android.intent.action.FACTORY_RESET");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.setPackage("android");
            sendBroadcast(intent);
        } else {
            intent = new Intent("android.intent.action.MASTER_CLEAR");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.setPackage("android");
            sendBroadcast(intent);
        }
    }


}
