package com.read.dream.readboybox.fragment.main;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.read.dream.readboybox.R;
import com.read.dream.readboybox.adpter.MyViewPagerAdapter;
import com.read.dream.readboybox.util.DataGenerator;

import java.util.ArrayList;
import java.util.List;


public class AppManagerFragment extends Fragment {
    private static final String TAG = AppManagerFragment.class.getSimpleName();
    private String mFrom;
    private ViewPager vpAppManager;
    private TabLayout tlAppManager;
    private List<Fragment> mFragmensts = new ArrayList<Fragment>();
    private MyViewPagerAdapter adapter;

    public AppManagerFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String from){
        AppManagerFragment homeFragment = new AppManagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("from",from);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mFrom = (String) getArguments().get("from");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_manager, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        vpAppManager = (ViewPager) view.findViewById(R.id.vp_app_manage);
        tlAppManager = (TabLayout) view.findViewById(R.id.tl_app_manager_below);
        mFragmensts.add(new TimeFragment());
        mFragmensts.add(new TimeFragment());
        mFragmensts.add(new AppManagerFragment());
        adapter = new MyViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragmensts);
        vpAppManager.setAdapter(adapter);
        vpAppManager.setOffscreenPageLimit(2);
        vpAppManager.setCurrentItem(0, false);
        tlAppManager.setupWithViewPager(vpAppManager);

        for (int i = 0; i < mFragmensts.size(); i++) {
            String text = "TAB" + i;
            tlAppManager.getTabAt(i).setCustomView(DataGenerator.getTextView(getContext(),i));
        }
        chooseFirst();
        tlAppManager.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(TAG, "onTabSelected111111: " + tab.getPosition());
                recoverItem();
                View view =tab.getCustomView();
                TextView textView = view.findViewById(R.id.tab_content_text);
                textView.setTextColor(getResources().getColor(R.color.colorAccent));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vpAppManager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlAppManager));
    }

    /**
     * 初始化
     */
    private void chooseFirst() {
        TabLayout.Tab tabAt =  tlAppManager.getTabAt(0);
        View view =tabAt.getCustomView();
        if(null == view ){
            return;
        }
        TextView textView = view.findViewById(R.id.tab_content_text);
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    /**
     * 恢复默认
     */
    private void recoverItem() {
        for (int i = 0; i < mFragmensts.size(); i++) {
            TabLayout.Tab tabAt = tlAppManager.getTabAt(i);
            View view = tabAt.getCustomView();
            if (null == view) {
                return;
            }
            TextView textView = view.findViewById(R.id.tab_content_text);
            textView.setTextColor(Color.GRAY);
        }
    }
}
