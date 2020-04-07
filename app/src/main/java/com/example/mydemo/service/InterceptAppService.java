package com.example.mydemo.service;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.mydemo.MainActivity;
import com.example.mydemo.R;
import com.example.mydemo.hook.GlobalActivityHookHelper;
import com.example.mydemo.util.App;
import com.example.mydemo.util.WindowUtils;
import com.example.mydemo.view.NoViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InterceptAppService extends Service {

    private final static String TAG = InterceptAppService.class.getSimpleName();
    // 启动notification的id，两次启动应是同一个id
    private final static int NOTIFICATION_ID = android.os.Process.myPid();
    private AssistServiceConnection mServiceConnection;
    private File file;
    private List<App> appList = new ArrayList<App>();
    private Handler mHandler = null;
    private final static int LOOPHANDLER = 0;
    private HandlerThread handlerThread = null;
    //间隔时间太短则会加大CPU的负荷，程序更耗电。内存占用更大
    private static long cycleTime = 100;
    private String lastPkgName;

    public InterceptAppService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // 设置为前台进程，降低oom_adj，提高进程优先级，提高存活机率
        setForeground();
        initData();
        //只能hook住当前应用自己的startActivity
        //GlobalActivityHookHelper.hook();
        //开始循环检查
        mHandler = new Handler(handlerThread.getLooper()) {
            public void dispatchMessage(android.os.Message msg) {
                switch (msg.what) {
                    case LOOPHANDLER:
                        //Log.i(TAG,"do something..."+(System.currentTimeMillis()/1000));
                        /**
                         * 这里需要注意的是：isLockName是用来判断当前的topActivity是不是我们需要加锁的应用
                         * 同时还是需要做一个判断，就是是否已经对这个app加过锁了，不然会出现一个问题
                         * 当我们打开app时，启动我们的加锁界面，解锁之后，回到了app,但是这时候又发现栈顶app是
                         * 需要加锁的app,那么这时候又启动了我们加锁界面，这样就出现死循环了。
                         * 可以自行的实验一下
                         * 所以这里用isUnLockActivity变量来做判断的
                         */
                        try {
                            if(isLockName(InterceptAppService.this) ){
                                //todo 小米/ov需要去手机设置里面打开后台弹出界面的权限
                               /* Intent intent1 = new Intent(InterceptAppService.this, NoViewActivity.class);
                                intent1.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent1);*/
                                WindowUtils.showPopupWindow(InterceptAppService.this);
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.d(TAG,"islocked now" + e.getMessage());
                        }
                        break;
                }
                mHandler.sendEmptyMessageDelayed(LOOPHANDLER, cycleTime);
            }
        };
        mHandler.sendEmptyMessage(LOOPHANDLER);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // 要注意的是android4.3之后Service.startForeground() 会强制弹出通知栏，解决办法是再
    // 启动一个service和推送共用一个通知栏，然后stop这个service使得通知栏消失。
    private void setForeground() {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(NOTIFICATION_ID, getNotification());
            return;
        }

        if (mServiceConnection == null) {
            mServiceConnection = new AssistServiceConnection();
        }
        // 绑定另外一条Service，目的是再启动一个通知，然后马上关闭。以达到通知栏没有相关通知
        // 的效果
        bindService(new Intent(this, AssistService.class), mServiceConnection,
                Service.BIND_AUTO_CREATE);
    }

    private class AssistServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Service assistService = ((AssistService.LocalBinder)service)
                    .getService();
            InterceptAppService.this.startForeground(NOTIFICATION_ID, getNotification());
            assistService.startForeground(NOTIFICATION_ID, getNotification());
            assistService.stopForeground(true);

            InterceptAppService.this.unbindService(mServiceConnection);
            mServiceConnection = null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private Notification getNotification() {
        String CHANNEL_ID = "com.example.recyclerviewtest.N1";
        String CHANNEL_NAME = "TEST";
        NotificationChannel notificationChannel = null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID).
                setContentTitle("This is content title").
                setContentText("This is content text").
                setWhen(System.currentTimeMillis()).
                setSmallIcon(R.mipmap.ic_launcher).
                setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)).
                setContentIntent(pendingIntent).build();
        return notification;
    }



    //初始化数据，获取禁用的应用
    private void initData(){
        handlerThread = new HandlerThread("count_thread");
        handlerThread.start();
        try {
            file = new File(getFilesDir(),"Test.json");//获取到应用在内部的私有文件夹下对应的Test.json文件
            FileInputStream fis = new FileInputStream(file);//获取一个文件输入流
            InputStreamReader isr = new InputStreamReader(fis);//读取文件内容
            BufferedReader bf = new BufferedReader(isr);//将字符流放入缓存中
            String line;//定义一个用来临时保存数据的变量
            StringBuilder sb = new StringBuilder();//实例化一个字符串序列化
            while((line = bf.readLine()) != null){
                sb.append(line);//将数据添加到字符串序列化中
            }
            //关闭流
            fis.close();
            isr.close();
            bf.close();
            JSONObject root = new JSONObject(sb.toString());//用JSONObject进行解析
            JSONArray array = root.getJSONArray("lockApp");//获取JSON数据中的数组数据
            for (int i = 0; i < array.length(); i++){
                if(array.isNull(i) ||null == array.getJSONObject(i)){
                    continue;
                }
                JSONObject object = array.getJSONObject(i);//遍历得到数组中的各个对象
                if("com.android.launcher3".equals(object.getString("packageName"))){
                    continue;
                }
                Log.d(TAG,"filter app Info = " + object.toString());
                App app =  new App();
                app.setPackageName(object.getString("packageName"));
                app.setRealName(object.getString("realName"));
                app.setFrontTime(object.getLong("frontTime"));
                app.setSystemApp(object.getBoolean("systemApp"));
                appList.add(app);
            }
        } catch (Exception e) {
            Log.d(TAG,"exception === " + e.getMessage());
        }
    }

    private boolean isLockName(Context mContext) throws PackageManager.NameNotFoundException {
        // TODO Auto-generated method stub
        ActivityManager mActivityManager;
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        String packageName;
        if (Build.VERSION.SDK_INT > 20) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext()
                    .getSystemService("usagestats");

            long ts = System.currentTimeMillis();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);

            UsageStats recentStats = null;
            for (UsageStats usageStats : queryUsageStats) {
                if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                    recentStats = usageStats;
                }
            }
            packageName = recentStats != null ? recentStats.getPackageName() : null;
        } else {
            // 5.0之前
            // 获取正在运行的任务栈(一个应用程序占用一个任务栈) 最近使用的任务栈会在最前面
            // 1表示给集合设置的最大容量 List<RunningTaskInfo> infos = am.getRunningTasks(1);
            // 获取最近运行的任务栈中的栈顶Activity(即用户当前操作的activity)的包名
            packageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();

        }

        if(null != appList){
            for (App app : appList) {
                if(!TextUtils.isEmpty(app.getPackageName()) && app.getPackageName().equals(packageName)){
                    forceStopPackageActivity(mContext,packageName);
                    return true;
                }
            }
        }
       return false;
    }
    private void forceStopPackageActivity(Context mContext,String packageName){
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        Method method = null;
        try {
            method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(mActivityManager, packageName);  //packageName是需要强制停止的应用程序包名
        } catch (Exception e) {
            Log.d(TAG,"forceStopPackageActivity error == " + e.getMessage());
        }
    }
}



