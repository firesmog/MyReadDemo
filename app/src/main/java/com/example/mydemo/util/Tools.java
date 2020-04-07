package com.example.mydemo.util;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.mydemo.MainActivity;

import org.joda.time.DateTime;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Tools {
    private static final String TAG = Tools.class.getSimpleName();
    //保存的数据集合
    private static List<App> dataList=new LinkedList<>();
    private static  List<UsageStats> usageStatsList;

    public static List<App> getPackages(Context context) {
        // 获取已经安装的所有应用, PackageInfo　系统类，
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        List<App> apps = new ArrayList<App>();
        usageStatsList = UsageStatsUtil.getUsageStatsList(context);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            App app = new App();
            long frontTime = 0L;
            String pkgName = packageInfo.packageName;
            String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            frontTime = UsageStatsUtil.getUseDurationWithL(usageStatsList,pkgName);
            app.setPackageName(pkgName);
            app.setRealName(appName);
            app.setFrontTime(frontTime);

            if ((packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                app.setSystemApp(false);
            } else { // 系统应用system app info ==
                app.setSystemApp(true);
            }
            Log.d(TAG,"info  pkgName=== " + pkgName + "appName === "  + appName + ",time ="  + frontTime + "is systemApp " + app.isSystemApp());

            if(frontTime != 0){
                apps.add(app);
            }
        }
        return apps;
    }



    //拦截



}
