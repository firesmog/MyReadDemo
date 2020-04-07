package com.example.mydemo.stopuninstall.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.mydemo.R;
import com.example.mydemo.util.DeviceAdminManager;
import com.example.mydemo.util.EdpPreferenceUtil;
import com.example.mydemo.util.SPUtils;

public class VrvDeviceAdminReceiver extends DeviceAdminReceiver {
    private static final String TAG = VrvDeviceAdminReceiver.class.getSimpleName();



    void showToast(Context context, String msg) {
        String status = context.getString(R.string.admin_receiver_status, msg);
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, context.getString(R.string.admin_receiver_status_enabled));

        Log.d(TAG, "onEnabled()");
        EdpPreferenceUtil.setStringValue(context, EdpPreferenceUtil.ADMIN_STATE_KEY,
                EdpPreferenceUtil.ADMIN_LIVE);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        lockDevice(context);
        return context.getString(R.string.admin_receiver_status_disable_warning);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.d(TAG, "onDisabled()");
        showToast(context, context.getString(R.string.admin_receiver_status_disabled));

        EdpPreferenceUtil.setStringValue(context, EdpPreferenceUtil.ADMIN_STATE_KEY,
                EdpPreferenceUtil.ADMIN_DEAD);
        DeviceAdminManager.alarmDeviceAdmin(context);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        // TODO(zhangtao2) not used. one bug(418) about changing notification which does not need at last.
        // showToast(context, context.getString(R.string.admin_receiver_status_pw_changed));
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        // TODO(zhangtao2) not used. one bug(418) about changing notification which does not need at last.
        // showToast(context, context.getString(R.string.admin_receiver_status_pw_failed));
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        // TODO(zhangtao2) not used. one bug(418) about changing notification which does not need at last.
        // showToast(context, context.getString(R.string.admin_receiver_status_pw_succeeded));
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        long expr = dpm.getPasswordExpiration(
                new ComponentName(context, VrvDeviceAdminReceiver.class));
        long delta = expr - System.currentTimeMillis();
        boolean expired = delta < 0L;
        String message = context.getString(expired ?
                R.string.expiration_status_past : R.string.expiration_status_future);
        showToast(context, message);
        Log.v(TAG, message);
    }


    public void lockDevice(Context context) {
        Log.d(TAG,"deviceActive == " + (Boolean) SPUtils.get(context,"deviceActive",true));
        if (!(Boolean) SPUtils.get(context,"deviceActive",true)) {//不锁定
            return;
        }
        //跳离当前询问是否取消激活的 dialog
        Intent outOfDialog = context.getPackageManager().getLaunchIntentForPackage("com.android.settings");
        outOfDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(outOfDialog);

        //调用设备管理器本身的功能，每 100ms 锁屏一次，用户即便解锁也会立即被锁，直至 7s 后
        final DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        dpm.lockNow();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 50) {
                    dpm.lockNow();
                    try {
                        Thread.sleep(100);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
