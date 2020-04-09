package com.read.dream.readboybox;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends Activity {

    private RelativeLayout rlAcount;
    private ImageView ivMainHead;
    private TextView tvMainId;
    private ViewPager vpMain;
    private TabLayout tlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        rlAcount = (RelativeLayout) findViewById(R.id.rl_main_account);
        ivMainHead = (ImageView)findViewById(R.id.iv_main_head);
        tvMainId = (TextView)findViewById(R.id.tv_main_id);
        vpMain = (ViewPager)findViewById(R.id.vp_main);
        tlMain = (TabLayout)findViewById(R.id.tl_main_below);

        tlMain.addTab(tlMain.newTab().setText("TAB1"));
        tlMain.addTab(tlMain.newTab().setText("TAB2"));
        tlMain.addTab(tlMain.newTab().setText("TAB3"));
        tlMain.addTab(tlMain.newTab().setText("TAB4"));


    }
}
