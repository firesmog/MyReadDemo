package com.example.mydemo.accessibility;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.mydemo.R;
import com.example.mydemo.util.AccessibilityLog;
import com.example.mydemo.util.AccessibilityUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 辅助功能权限打开辅助activity，用于启动辅助功能设置页面
 */
public class AccessibilityOpenHelperActivity extends Activity {
    private boolean isFirstCome = true;
    private static final String ACTION = "action";
    private static final String ACTION_FINISH_SELF = "action_finis_self";

    private Timer timer;
    private TimerTask timerTask;
    private int mTimeoutCounter = 0;

    private int TIMEOUT_MAX_INTERVAL = 60 * 2; // 2 min

    private long TIMER_CHECK_INTERVAL = 1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_open_helper);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String action = intent.getStringExtra(ACTION);
            if (ACTION_FINISH_SELF.equals(action)) {
                finishCurrentActivity();
                return;
            }
        }
        mTimeoutCounter = 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishCurrentActivity();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null) {
            String action = intent.getStringExtra(ACTION);
            if (ACTION_FINISH_SELF.equals(action)) {
                finishCurrentActivity();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstCome) {
            finishCurrentActivity();
        } else {
            jumpActivities();
            dealFunctionIsOpen();
            //startCheckAccessibilityOpen();
        }
        isFirstCome = false;
    }

    private void jumpActivities() {
        try {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
            AccessibilityLog.printLog("jump access error = " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        freeTimeTask();
        super.onDestroy();
    }

    private void finishCurrentActivity() {
        freeTimeTask();
        finish();
    }

    private void startCheckAccessibilityOpen() {
        freeTimeTask();
        initTimeTask();
        timer.schedule(timerTask, 0, TIMER_CHECK_INTERVAL);
    }

    private void dealFunctionIsOpen(){
        if (AccessibilityUtil.isAccessibilitySettingsOn(AccessibilityOpenHelperActivity.this)){
            Intent intent = new Intent();
            intent.putExtra(ACTION, ACTION_FINISH_SELF);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(AccessibilityOpenHelperActivity.this, AccessibilityOpenHelperActivity.this.getClass());
            startActivity(intent);
        }
    }

    //开启轮询监听用户是否打开了辅助功能（那个开关是系统配置的，无法有效设置监听回调，只能轮询）
    private void initTimeTask() {
        /*timer = new Timer();
        mTimeoutCounter = 0;
        timerTask = new TimerTask() {

            @SuppressWarnings("static-access")
            @Override
            public void run() {
                AccessibilityLog.printLog("add test log by lzy for timerTask" + mTimeoutCounter);
                if (AccessibilityUtil.isAccessibilitySettingsOn(AccessibilityOpenHelperActivity.this)) {
                    freeTimeTask();
                    Looper.prepare();
                    try {
                        AccessibilityOpenHelperActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(AccessibilityOpenHelperActivity.this, "辅助功能开启成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent();
                        intent.putExtra(ACTION, ACTION_FINISH_SELF);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(AccessibilityOpenHelperActivity.this, AccessibilityOpenHelperActivity.this.getClass());
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Looper.loop();
                }
                // 超过2分钟超时，就释放timer。
                mTimeoutCounter++;
                if (mTimeoutCounter > TIMEOUT_MAX_INTERVAL) {
                    freeTimeTask();
                }
            }
        };*/
    }

    private void freeTimeTask() {
        /*if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }*/
    }
}


