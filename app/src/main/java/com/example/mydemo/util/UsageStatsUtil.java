package com.example.mydemo.util;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;

public class UsageStatsUtil {

    private static String TAG = UsageStatsUtil.class.getSimpleName();

    public static List<UsageStats> getUsageStatsList(Context context){

        if(null == context) return null;

        UsageStatsManager usm = getUsageStatsManager(context);

        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.DATE, 31);
        beginCal.set(Calendar.MONTH, 12);
        beginCal.set(Calendar.YEAR, 1970);

        long startTime = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).getMillis();
        long endTime = System.currentTimeMillis();

        final List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        if(usageStatsList.isEmpty()){
            Log.d("dasd","usage stats list is null");
        }else{
            Log.d("asdasf","usage stats list size = " + usageStatsList.size());
        }

        return usageStatsList;
    }


    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

    //使用时长
    public static long getUseDurationWithL(List<UsageStats> usageStatsList, String pkName) {
        for (UsageStats stat : usageStatsList){
            if(stat.getPackageName().equals(pkName)){
                return stat.getTotalTimeInForeground();
            }
        }
        return 0;
    }

    /**
     * Use reflect to get Package Usage Statistics data.<br>
     */
    public static void getPkgUsageStats() {
        Log.d(TAG, "[getPkgUsageStats]");
        try {
            Class<?> cServiceManager = Class
                    .forName("android.os.ServiceManager");
            Method mGetService = cServiceManager.getMethod("getService",
                    java.lang.String.class);
            Object oRemoteService = mGetService.invoke(null, "usagestats");

            // IUsageStats oIUsageStats =
            // IUsageStats.Stub.asInterface(oRemoteService)
            Class<?> cStub = Class
                    .forName("com.android.internal.app.IUsageStats$Stub");
            Method mUsageStatsService = cStub.getMethod("asInterface",
                    android.os.IBinder.class);
            Object oIUsageStats = mUsageStatsService.invoke(null,
                    oRemoteService);

            // PkgUsageStats[] oPkgUsageStatsArray =
            // mUsageStatsService.getAllPkgUsageStats();
            Class<?> cIUsageStatus = Class
                    .forName("com.android.internal.app.IUsageStats");
            Method mGetAllPkgUsageStats = cIUsageStatus.getMethod(
                    "getAllPkgUsageStats", (Class[]) null);
            Object[] oPkgUsageStatsArray = (Object[]) mGetAllPkgUsageStats
                    .invoke(oIUsageStats, (Object[]) null);
            Log.d(TAG, "[getPkgUsageStats] oPkgUsageStatsArray = "+oPkgUsageStatsArray);

            Class<?> cPkgUsageStats = Class
                    .forName("com.android.internal.os.PkgUsageStats");

            StringBuffer sb = new StringBuffer();
            sb.append("nerver used : ");
            for (Object pkgUsageStats : oPkgUsageStatsArray) {
                // get pkgUsageStats.packageName, pkgUsageStats.launchCount,
                // pkgUsageStats.usageTime
                String packageName = (String) cPkgUsageStats.getDeclaredField(
                        "packageName").get(pkgUsageStats);
                int launchCount = cPkgUsageStats
                        .getDeclaredField("launchCount").getInt(pkgUsageStats);
                long usageTime = cPkgUsageStats.getDeclaredField("usageTime")
                        .getLong(pkgUsageStats);
                if (launchCount > 0)
                    Log.d(TAG, "[getPkgUsageStats] "+ packageName + "  count: "
                            + launchCount + "  time:  " + usageTime);
                else {
                    sb.append(packageName + "; ");
                }
            }
            Log.d(TAG, "[getPkgUsageStats] " + sb.toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


}