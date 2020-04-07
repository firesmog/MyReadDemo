package com.example.mydemo.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

public class DeviceAdminManager {

    // circle: execute device admin
    private static final int CIRCLE_TIME_DEVICE_ADMIN = 1000;
    public static final String ALARM_BROADCAST_ACTION = "com.vrv.edp.deviceadmin.AlarmAction";
    private static AlarmManager mAlarmManager;
    private static PendingIntent mAlarmPendintent;
    private Context context;
    private ComponentName componentName;//权限监听器
    private DevicePolicyManager devicePolicyManager;
    private static boolean isLock = true;

    public static void alarmDeviceAdmin(Context context) {
        long firstTime = SystemClock.elapsedRealtime();
        if (null == mAlarmManager) {
            mAlarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
        }
        mAlarmPendintent = PendingIntent.getBroadcast(context, 0, new Intent(
                ALARM_BROADCAST_ACTION), 0);
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                firstTime, CIRCLE_TIME_DEVICE_ADMIN, mAlarmPendintent);
    }

    public static void cancelAlarm() {
        mAlarmManager.cancel(mAlarmPendintent);
    }




    public DeviceAdminManager(Context context) {
        this.context = context;
        componentName = new ComponentName(context, DeviceAdminManager.class);
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }


    /**
     * 注册成为"设备管理器"
     */
    public void registerDevicePolicyManager() {
        try {
            //检测是否已经是设备管理器
            if (!isAdminActive()) {
                activeAdmin();
            } else {
                // 已经是设备管理器了，就可以操作一些特殊的安全权限了
                Toast.makeText(context, "设备已经激活,请勿重复激活", Toast.LENGTH_SHORT).show(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAdminActive() { // 实例化系统的设备管理器
        return devicePolicyManager.isAdminActive(componentName);
    }


    /**
     * 激活设备管理器
     */
    private void activeAdmin() {
        //指定广播接收器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "hello~~,设备管理器");
        context.startActivity(intent);
    }

    /**
     * 移除程序 如果不移除程序 APP无法被卸载
     */
    public void onRemoveActivate() {
        isLock = false;
        devicePolicyManager.removeActiveAdmin(componentName);
    }

    public void lockDevice() {
        if (!isLock) {//不锁定
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
                while (i < 70) {
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
