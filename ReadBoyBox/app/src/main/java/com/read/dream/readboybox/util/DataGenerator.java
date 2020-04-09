package com.read.dream.readboybox.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.read.dream.readboybox.R;
import com.read.dream.readboybox.fragment.main.AppManagerFragment;
import com.read.dream.readboybox.fragment.main.DetailFragment;
import com.read.dream.readboybox.fragment.main.TimeFragment;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {
    public static final int []mTabRes = new int[]{R.drawable.ic_launcher, R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
    public static final int []mTabResPressed = new int[]{R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};

    public static List<Fragment> getFragments(String from){
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(DetailFragment.newInstance(from));
        fragments.add(TimeFragment.newInstance(from));
        fragments.add(AppManagerFragment.newInstance(from));
        return fragments;
    }

    /**
     * 获取Tab 显示的内容
     * @param context
     * @param position
     * @return
     */
    public static View getImageAndTextView(Context context, int position){
        View view = LayoutInflater.from(context).inflate(R.layout.home_tab_content,null);
        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image);
        tabIcon.setImageResource(DataGenerator.mTabRes[position]);
        TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
        String[] tableText = context.getResources().getStringArray(R.array.array_main_string);
        tabText.setText(tableText[position]);
        return view;
    }

    /**
     * 获取Tab 显示的内容
     * @param context
     * @param position
     * @return
     */
    public static View getTextView(Context context, int position){
        View view = LayoutInflater.from(context).inflate(R.layout.home_tab_content,null);
        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image);
        tabIcon.setVisibility(View.GONE);
        TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
        String[] tableText = context.getResources().getStringArray(R.array.array_app_manager);
        tabText.setText(tableText[position]);
        return view;
    }

}

