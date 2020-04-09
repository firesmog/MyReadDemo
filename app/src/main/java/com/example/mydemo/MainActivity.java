package com.example.mydemo;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mydemo.accessibility.AccessibilityMainActivity;
import com.example.mydemo.service.InterceptAppService;
import com.example.mydemo.stopuninstall.receiver.VrvDeviceAdminReceiver;
import com.example.mydemo.testdemo.MyTestDemoActivity;
import com.example.mydemo.util.App;
import com.example.mydemo.util.DeviceAdminManager;
import com.example.mydemo.util.EdpPreferenceUtil;
import com.example.mydemo.util.SPUtils;
import com.example.mydemo.util.Tools;
import com.example.mydemo.view.AppInfoAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends Activity {
    private List<App> apps;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView rvView;
    private AppInfoAdapter adapter;
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private ComponentName mVrvDeviceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVrvDeviceAdmin = new ComponentName(this, VrvDeviceAdminReceiver.class);
        //initView();
        Intent intent  = new Intent(MainActivity.this, InterceptAppService.class);
        startService(intent);

        //startActivity(new Intent(MainActivity.this, MyTestDemoActivity.class));
    }

    private void initView(){
        Button view = (Button) findViewById(R.id.bt_during);
        rvView = (RecyclerView)findViewById(R.id.rv_recycler_view);
        rvView.setLayoutManager(new LinearLayoutManager(this));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //必须先引导用户先打开权限
                showAppUsedInfo();
            }
        });

        Button button = (Button)findViewById(R.id.bt_access);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccessibilityMainActivity.class));
            }
        });


        Button button1 = (Button)findViewById(R.id.bt_active);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeDeviceAdmin();
            }
        });

        Button button2 = (Button)findViewById(R.id.bt_dis_active);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                devicePolicyManager.removeActiveAdmin(mVrvDeviceAdmin);
                SPUtils.put(MainActivity.this,"deviceActive",false);
            }
        });
    }

    private  void showAppUsedInfo(){
        Observable.create(new ObservableOnSubscribe<List<App>>() {
            @Override
            public void subscribe(ObservableEmitter<List<App>> emitter) throws Exception {
                apps = Tools.getPackages(MainActivity.this);
                JSONObject root = new JSONObject();//实例一个JSONObject对象
                JSONArray lockApp = new JSONArray();//实例一个JSON数组
                for (int i = 0; i < apps.size(); i++) {
                    App app = apps.get(i);
                    if(null != app && !app.isSystemApp() && !TextUtils.isEmpty(app.getPackageName())){
                        Log.d(TAG,"systemAPP lost = " + app.toString());
                        JSONObject obj = new JSONObject();//实例一个lan1的JSON对象
                        obj.put("packageName",app.getPackageName());
                        obj.put("frontTime",app.getFrontTime());
                        obj.put("realName",app.getRealName());
                        obj.put("systemApp",app.isSystemApp());
                        lockApp.put(i,obj);
                    }
                }
                root.put("lockApp",lockApp);
                File file = new File(getFilesDir(),"Test.json");//获取到应用在内部的私有文件夹下对应的Test.json文件
                FileOutputStream fos = new FileOutputStream(file);//创建一个文件输出流
                fos.write(root.toString().getBytes());//将生成的JSON数据写出
                fos.close();//关闭输出流
                emitter.onNext(apps);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<App>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<App> apps) {
                        Intent intent  = new Intent(MainActivity.this, InterceptAppService.class);
                        startService(intent);
                        if(null == adapter){
                            adapter = new AppInfoAdapter(MainActivity.this,apps);
                            rvView.setAdapter(adapter);
                        }else {
                            adapter.notifyDataSetChanged();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"front proc == " + isServiceRunning(MainActivity.this,InterceptAppService.class.getName()));

                    }
                });
    }


    public static boolean isServiceRunning(Context context,String serviceName ) {
        if (("").equals(serviceName) || serviceName == null){
            Log.d(TAG,"front proc111111 == ");
            return false;
        }

        ActivityManager myManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(10000);
        for (int i = 0; i < runningService.size(); i++) {
            Log.d(TAG,"front proc2222 == " + runningService.get(i).service.getClassName().toString());
            if (runningService.get(i).service.getClassName().toString()
                    .equals(serviceName)) {
                return true;
            }
        }

        return false;
    }

    private void activeDeviceAdmin() {
        IntentFilter intentFilter = new IntentFilter(DeviceAdminManager.ALARM_BROADCAST_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, intentFilter);
        DeviceAdminManager.alarmDeviceAdmin(this);
        SPUtils.put(MainActivity.this,"deviceActive",true);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent || TextUtils.isEmpty(intent.getAction())) return;
            String action = intent.getAction();

            if (DeviceAdminManager.ALARM_BROADCAST_ACTION.equals(action)) {

                String deviceLive = EdpPreferenceUtil.getStringValue(context, EdpPreferenceUtil.ADMIN_STATE_KEY,
                        EdpPreferenceUtil.ADMIN_DEAD);
                if (DeviceAdminManager.ALARM_BROADCAST_ACTION.equals(intent.getAction()) && !EdpPreferenceUtil.ADMIN_LIVE.equals(deviceLive)) {
                    Intent adminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    adminIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    adminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mVrvDeviceAdmin);
                    adminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            getString(R.string.add_admin_extra_app_text));
                    startActivityForResult(adminIntent, REQUEST_CODE_ENABLE_ADMIN);
                }
            }

        }

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }
}
