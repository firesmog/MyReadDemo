package com.read.dream.readboybox.fragment.appmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.read.dream.readboybox.R;


public class ForbideUseFragment extends Fragment {
    private String mFrom;

    public ForbideUseFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String from){
        ForbideUseFragment homeFragment = new ForbideUseFragment();
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
        return inflater.inflate(R.layout.fragment_app_manager, container, false);
    }

}
