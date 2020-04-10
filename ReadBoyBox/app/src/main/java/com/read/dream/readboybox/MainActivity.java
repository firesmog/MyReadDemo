package com.read.dream.readboybox;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.read.dream.readboybox.activity.DataPickSimpleActivity;
import com.read.dream.readboybox.adpter.MyViewPagerAdapter;
import com.read.dream.readboybox.fragment.main.AppManagerFragment;
import com.read.dream.readboybox.fragment.main.DetailFragment;
import com.read.dream.readboybox.fragment.main.MineFragment;
import com.read.dream.readboybox.fragment.main.TimeFragment;
import com.read.dream.readboybox.util.CustomViewPager;
import com.read.dream.readboybox.util.DataGenerator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<Fragment> mFragmensts = new ArrayList<>();
    private MyViewPagerAdapter adapter;

    private RelativeLayout rlAccount;
    private ImageView ivMainHead;
    private TextView tvMainId;
    private CustomViewPager vpMain;
    private TabLayout tlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();// 隐藏ActionBar
        //initView();
        startActivity(new Intent(MainActivity.this, DataPickSimpleActivity.class));
    }

    private void initView(){
        rlAccount = (RelativeLayout) findViewById(R.id.rl_main_account);
        ivMainHead = (ImageView)findViewById(R.id.iv_main_head);
        tvMainId = (TextView)findViewById(R.id.tv_main_id);
        vpMain = (CustomViewPager)findViewById(R.id.vp_main);
        tlMain = (TabLayout)findViewById(R.id.tl_main_below);

        mFragmensts.add(new DetailFragment());
        mFragmensts.add(new AppManagerFragment());
        mFragmensts.add(new TimeFragment());
        mFragmensts.add(new MineFragment());
        adapter = new MyViewPagerAdapter(getSupportFragmentManager(),mFragmensts);
        vpMain.setScanScroll(false);
        vpMain.setAdapter(adapter);
        vpMain.setOffscreenPageLimit(2);
        vpMain.setCurrentItem(0,false);
        tlMain.setupWithViewPager(vpMain);

        for(int i= 0;i < mFragmensts.size(); i++){
            tlMain.getTabAt(i).setCustomView(DataGenerator.getImageAndTextView(this,i));
        }
        chooseFirst();
        tlMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(TAG, "onTabSelected: " + tab.getPosition());
                vpMain.setCurrentItem(tab.getPosition(),true);
                recoverItem();
                View view =tab.getCustomView();
                ImageView imageView = view.findViewById(R.id.tab_content_image);
                TextView textView = view.findViewById(R.id.tab_content_text);
                imageView.setImageDrawable(getResources().getDrawable(DataGenerator.mTabResPressed[tab.getPosition()]));
                textView.setTextColor(getResources().getColor(R.color.color_03DAC5));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlMain));
    }

    /**
     * 初始化
     */
    private void chooseFirst(){
        TabLayout.Tab tabAt =  tlMain.getTabAt(0);
        View view =tabAt.getCustomView();
        ImageView imageView = view.findViewById(R.id.tab_content_image);
        TextView textView = view.findViewById(R.id.tab_content_text);
        imageView.setImageDrawable(getResources().getDrawable(DataGenerator.mTabResPressed[0]));
        textView.setTextColor(getResources().getColor(R.color.color_03DAC5));
    }

    /**
     * 恢复默认
     */
    private void recoverItem() {
        for (int i = 0; i < mFragmensts.size(); i++) {
            TabLayout.Tab tabAt =  tlMain.getTabAt(i);
            View view =tabAt.getCustomView();
            if(null == view){
                return;
            }
            ImageView imageView = view.findViewById(R.id.tab_content_image);
            TextView textView = view.findViewById(R.id.tab_content_text);
            imageView.setImageDrawable(getResources().getDrawable(DataGenerator.mTabRes[i]));
            textView.setTextColor(Color.GRAY);
        }
    }
}
